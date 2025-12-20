package ice

import arc.Core
import arc.Events
import arc.files.Fi
import arc.func.Boolf
import arc.func.Cons
import arc.func.Intf
import arc.graphics.Pixmap
import arc.graphics.Pixmaps
import arc.graphics.Texture
import arc.graphics.Texture.TextureFilter
import arc.graphics.g2d.PixmapRegion
import arc.graphics.g2d.TextureAtlas
import arc.graphics.g2d.TextureRegion
import arc.struct.ObjectFloatMap
import arc.struct.ObjectMap
import arc.struct.Seq
import arc.util.Log
import arc.util.Structs
import arc.util.Time
import mindustry.Vars
import mindustry.ctype.Content
import mindustry.ctype.UnlockableContent
import mindustry.game.EventType.AtlasPackEvent
import mindustry.graphics.MultiPacker
import mindustry.graphics.MultiPacker.PageType
import mindustry.mod.Mods.LoadedMod
import java.util.concurrent.CountDownLatch
import java.util.concurrent.Future
import kotlin.math.max

object DFCS {
     fun loadAsync() {
        if (!Vars.mods.list().contains(Boolf { obj: LoadedMod? -> obj!!.enabled() })) return
        val startTime = Time.millis()
        //TODO this should estimate sprite sizes per page
        val packer = MultiPacker()
        val textureResize = ObjectFloatMap<String?>()
        val totalSprites = intArrayOf(0)
        //all packing tasks to await
        val tasks = Seq<Future<Runnable?>>()

         Vars.mods.eachEnabled(Cons { mod: LoadedMod? ->
            val sprites = mod!!.root.child("sprites").findAll(Boolf { f: Fi? -> f!!.extension() == "png" })
            val overrides = mod.root.child("sprites-override").findAll(Boolf { f: Fi? -> f!!.extension() == "png" })

            packSprites(packer, sprites, mod, true, tasks, textureResize)
            packSprites(packer, overrides, mod, false, tasks, textureResize)

            Log.debug("Packed @ images for mod '@'.", sprites.size + overrides.size, mod.meta.name)
            totalSprites[0] += sprites.size + overrides.size
        })

        for (result in tasks) {
            try {
                val packRun = result.get()
                if (packRun != null) { //can be null for very strange reasons, ignore if that's the case
                    try {
                        //actually pack the image
                        packRun.run()
                    } catch (e: Exception) { //the image can fail to fit in the spritesheet
                        Log.err("Failed to fit image into the spritesheet, skipping.")
                        Log.err(e)
                    }
                }
            } catch (e: Exception) { //this means loading the image failed, log it and move on
                Log.err(e)
            }
        }

        Log.debug("Total sprites: @", totalSprites[0])
        val filter = if (Core.settings.getBool("linear", true)) TextureFilter.linear else TextureFilter.nearest
        val whiteToDispose = arrayOf<Texture?>(null)

        class RegionEntry internal constructor(var name: String?, var region: PixmapRegion, var splits: IntArray?, var pads: IntArray?)

         val entries: Array<Seq<RegionEntry>?> = arrayOfNulls<Seq<RegionEntry>>(PageType.all.size)
        for (i in PageType.all.indices) {
            entries[i] = Seq<RegionEntry>()
        }
        val pageTypes = ObjectMap.of<Texture?, PageType?>(
            Core.atlas.find("white").texture, PageType.main,
            Core.atlas.find("stone1").texture, PageType.environment,
            Core.atlas.find("whiteui").texture, PageType.ui,
            Core.atlas.find("rubble-1-0").texture, PageType.rubble
        )

        for (region in Core.atlas.getRegions()) {
            val type = pageTypes.get(region.texture, PageType.main)

            if (!packer.has(type, region.name)) {
                entries[type.ordinal]!!.add(RegionEntry(region.name, Core.atlas.getPixmap(region), region.splits, region.pads))
            }
        }
        //先按大小排序每个页面类型，以实现最佳的打包
        for (i in PageType.all.indices) {
            val rects = entries[i]
            val type = PageType.all[i]
            //TODO is this in reverse order?
            rects!!.sort(Structs.comparingInt<RegionEntry?>(Intf { o: RegionEntry? -> -max(o!!.region.width, o.region.height) }))

            for (entry in rects) {
                packer.add(type, entry.name, entry.region, entry.splits, entry.pads)
            }
        }

        waitForMain(Runnable {
            Core.atlas.dispose()
            //dead shadow-atlas for getting regions, but not pixmaps
            val shadow = Core.atlas
            //返回“阴影”区域的虚拟纹理图集;用于模组加载
            Core.atlas = object : TextureAtlas() {
                var foundWhite: Boolean = false
                var whiteRegion: AtlasRegion? = null

                init {
                    //needed for the correct operation of the found() method in the TextureRegion
                    error = shadow.find("error")
                }

                override fun white(): AtlasRegion? {
                    if (Core.app.isOnMainThread() && !foundWhite) {
                        val pixmap = Pixmaps.blankPixmap()
                        val tex = Texture(pixmap)
                        whiteToDispose[0] = tex
                        return AtlasRegion(tex, 0, 0, 1, 1).also { whiteRegion = it }
                    }
                    return super.white()
                }

                override fun find(name: String?): AtlasRegion? {
                    val base = packer.get(name)

                    if (base != null) {
                        val reg = AtlasRegion(shadow.find(name).texture, base.x, base.y, base.width, base.height)
                        reg.name = name
                        reg.pixmapRegion = base
                        return reg
                    }

                    return shadow.find(name)
                }

                override fun isFound(region: TextureRegion?): Boolean {
                    return region !== shadow.find("error")
                }

                override fun find(name: String?, def: TextureRegion?): TextureRegion? {
                    return if (!has(name)) def else find(name)
                }

                override fun has(s: String?): Boolean {
                    return shadow.has(s) || packer.get(s) != null
                }

                //return the *actual* pixmap regions, not the disposed ones.
                override fun getPixmap(region: AtlasRegion): PixmapRegion? {
                    val out = packer.get(region.name)
                    //this should not happen in normal situations
                    if (out == null) return packer.get("error")
                    return out
                }
            }
        })
        //generate new icons
        for (arr in Vars.content.getContentMap()) {
            arr.each(Cons { c: Content? ->
                if (c is UnlockableContent && c.minfo.mod != null) {
                    c.load()
                    c.loadIcon()
                    if (c.generateIcons && !c.minfo.mod.meta.pregenerated) {
                        c.createIcons(packer)
                    }
                }
            })
        }

        waitForMain(Runnable {
            if (whiteToDispose[0] != null) {
                whiteToDispose[0]!!.dispose()
            }
            //replace old atlas data
            Core.atlas = packer.flush(filter, object : TextureAtlas() {
                override fun getPixmap(region: AtlasRegion?): PixmapRegion {
                    val other = super.getPixmap(region)
                    if (other.pixmap.isDisposed()) {
                        throw RuntimeException("不支持在createIcons之外调用getPixmap!")
                    }

                    return other
                }
            })

            textureResize.each(Cons { e: ObjectFloatMap.Entry<String?>? -> Core.atlas.find(e!!.key).scale = e.value })

            Core.atlas.setErrorRegion("error")
            Log.debug("Total pages: @", Core.atlas.getTextures().size)

            packer.printStats()

            Events.fire<AtlasPackEvent?>(AtlasPackEvent())

            packer.dispose()
            Log.debug("Total time to pack and generate sprites: @ms", Time.timeSinceMillis(startTime))
        })
    }
    private fun packSprites(packer: MultiPacker, sprites: Seq<Fi>, mod: LoadedMod, prefix: Boolean, tasks: Seq<Future<Runnable?>>, textureResize: ObjectFloatMap<String?>) {
        val bleed = Core.settings.getBool("linear", true) && !mod.meta.pregenerated
        val textureScale = mod.meta.texturescale

        for (file in sprites) {
            val baseName = file.nameWithoutExtension()
            val regionName = if (baseName.contains(".")) baseName.substring(0, baseName.indexOf(".")) else baseName

            if (!prefix && !Core.atlas.has(regionName)) {
                Log.warn("Sprite '@' in mod '@' attempts to override a non-existent sprite.", regionName, mod.name)
            }
            //并行读取和流血像素映射
            tasks.add(Vars.mainExecutor.submit<Runnable> {
                try {
                    val pix = Pixmap(file.readBytes())
                    //only bleeds when linear filtering is on at startup
                    if (bleed) {
                        Pixmaps.bleed(pix, 2)
                    }
                    //this returns a *runnable* which actually packs the resulting pixmap; this has to be done synchronously outside the method
                    return@submit Runnable {
                        //don't prefix with mod name if it's already prefixed by a category, e.g. `block-modname-content-full`.
                        val hyphen = baseName.indexOf('-')
                        val fullName = (if (prefix && !(hyphen != -1 && baseName.substring(hyphen + 1).startsWith(mod.name + "-"))) mod.name + "-" else "") + baseName

                        packer.add(getPage(file), fullName, PixmapRegion(pix))
                        if (textureScale != 1.0f) {
                            textureResize.put(fullName, textureScale)
                        }
                        pix.dispose()
                    }
                } catch (e: java.lang.Exception) {
                    //rethrow exception with details about the cause of failure
                    throw java.lang.Exception("Failed to load image " + file + " for mod " + mod.name, e)
                }
            })
        }
    }
    private fun getPage(file: Fi): PageType {
        val path = file.path()
        return if (path.contains("sprites/blocks/environment") || path.contains("sprites-override/blocks/environment")) PageType.environment else if (path.contains("sprites/rubble") || path.contains("sprites-override/rubble")) PageType.rubble else if (path.contains("sprites/ui") || path.contains("sprites-override/ui")) PageType.ui else PageType.main
    }

    fun waitForMain(run: Runnable) {
        val latch = CountDownLatch(1)
        Core.app.post(Runnable {
            run.run()
            latch.countDown()
        })
        try {
            latch.await()
        } catch (e: InterruptedException) {
            throw java.lang.RuntimeException(e)
        }
    }
}