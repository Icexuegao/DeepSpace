package ice.library

import arc.Core
import arc.Graphics
import arc.files.Fi
import arc.files.ZipFi
import arc.graphics.Pixmap
import arc.graphics.Texture
import arc.graphics.g2d.NinePatch
import arc.graphics.g2d.PixmapRegion
import arc.graphics.g2d.TextureAtlas.AtlasRegion
import arc.graphics.g2d.TextureRegion
import arc.scene.style.Drawable
import arc.scene.style.ScaledNinePatchDrawable
import arc.util.Log
import ice.Ice
import ice.library.world.Load
import mindustry.Vars

object IFiles : Load {
    const val displayName = "DeepSpace"
    private val rootDirectory = HashMap<String, Fi>()
    private val spritesIce = HashMap<String, Fi>()
    private val sprites = HashMap<String, Fi>()
    private val musics = HashMap<String, Fi>()
    private val sounds = HashMap<String, Fi>()
    private val shaders = HashMap<String, Fi>()
    private var initializer = false
    private val modFile: Fi = ZipFi(Vars.modDirectory.list { it.name.contains(displayName) }.first())

    override fun setup() {
        if (initializer) return
        initializer = true
        modFile.list().forEach {
            rootDirectory[it.name()] = it
        }
        rootDirectory.forEach { entry ->
            when (entry.key) {
                "sprites-ice" -> {
                    entry.value.findAll { f ->
                        f.extension().equals("png")
                    }?.forEach {
                        val nameWithoutExtension = it.nameWithoutExtension()
                        if (spritesIce.contains(nameWithoutExtension)) {
                            Log.warn("已收录pngice文件:${spritesIce.get(nameWithoutExtension)?.path()},未收录:${it.path()}")
                        } else {
                            spritesIce[nameWithoutExtension] = it
                        }
                    }
                }

                "sprites" -> {
                    entry.value.findAll { f ->
                        f.extension().equals("png")
                    }?.forEach {
                        sprites[it.nameWithoutExtension()] = it
                    }
                }

                "musics" -> {
                    entry.value.findAll { it.extension().equals("ogg") }?.forEach {
                        musics[it.name()] = it
                    }
                }

                "sounds" -> {
                    entry.value.findAll { it.extension().equals("ogg") }?.forEach {
                        sounds[it.name()] = it
                    }
                }

                "shaders" -> {
                    entry.value.walk {
                        if (!shaders.contains(it.name())) {
                            shaders[it.name()] = it
                        } else {
                            Log.warn("已收录shader文件:${shaders.get(it.name())?.path()},未收录:${it.path()}")
                        }
                    }
                }
            }
        }
    }

    fun getModName(): String {
        val findMeta = Vars.mods.findMeta(modFile)
        return findMeta.internalName
    }

    fun findSound(name: String) = sounds[name] ?: throw Exception("未找到文件:$name")
    fun findMusic(name: String) = musics[name] ?: throw Exception("未找到文件:$name")
    fun findShader(name: String) = shaders[name] ?: throw Exception("未找到文件:$name")
    fun findIcePng(name: String): AtlasRegion {
        val file = spritesIce[name] ?: throw Exception("未找到文件:$name.png")
        return getAtlasRegion(file)
    }

    fun findPng(name: String): AtlasRegion {
        return Core.atlas.find("${getModName()}-$name")
    }

    fun hasPng(name: String) = sprites.contains(name)
    fun hasIcePng(name: String) = spritesIce.contains(name)
    fun getPiX(name: String) = Pixmap(sprites[name] ?: throw Exception("未找到文件:$name.png"))

    fun getNormName(name: String): String = "${getModName()}-$name"
    fun getRepName(name: String): String = name.replace("${getModName()}-", "")

    fun getAtlasRegion(file: Fi): AtlasRegion {
        val texture = TextureRegion(Texture(file))
        val atlasRegion = AtlasRegion(texture)
        atlasRegion.offsetX = 0f
        atlasRegion.offsetY = 0f
        atlasRegion.x = 0
        atlasRegion.y = 0
        atlasRegion.name = "${Ice.name}-${file.name()}"
        atlasRegion.packedWidth = texture.width
        atlasRegion.packedHeight = texture.height
        atlasRegion.originalWidth = texture.width
        atlasRegion.originalHeight = texture.height
        return atlasRegion
    }

    /** 分割九宫图  */
    fun createNinePatch(name: String): Drawable {
        val texture = findIcePng("$name.9")
        val pixmapRegion = PixmapRegion(texture.texture.textureData.pixmap, 0, 0, texture.width, texture.height)
        val splits = getSplits(pixmapRegion)/* texture.set(1, 1, texture.width - 2, texture.height - 2)*/
        texture.set(1, 2, texture.width - 2, texture.height - 4)
        val copy = getScaledNinePatchDrawable(texture, splits!!)
        copy.minWidth = 0f
        copy.minHeight = 0f
        copy.topHeight = 0f
        copy.rightWidth = 0f
        copy.bottomHeight = 0f
        copy.leftWidth = 0f
        return copy
    }

    fun newCursor(filename: String): Graphics.Cursor {
        val p = findIcePng(filename).texture.textureData.pixmap
        val result = Core.graphics.newCursor(p, p.width / 2, p.height / 2)
        p.dispose()
        return result
    }

    private fun getScaledNinePatchDrawable(region: TextureRegion, ints: IntArray): ScaledNinePatchDrawable {
        return object : ScaledNinePatchDrawable(NinePatch(region, ints[0], ints[1], ints[2], ints[3])) {
            override fun getLeftWidth(): Float {
                return 0f
            }

            override fun getRightWidth(): Float {
                return 0f
            }

            override fun getTopHeight(): Float {
                return 0f
            }

            override fun getBottomHeight(): Float {
                return 0f
            }
        }
    }

    private fun getSplits(raster: PixmapRegion): IntArray? {
        var startX = getSplitPoint(raster, 1, 0, startPoint = true, true)
        var endX = getSplitPoint(raster, startX, 0, startPoint = false, true)
        var startY = getSplitPoint(raster, 0, 1, startPoint = true, false)
        var endY = getSplitPoint(raster, 0, startY, startPoint = false, false)
        // Ensure pixels after the end are not invalid.
        getSplitPoint(raster, endX + 1, 0, startPoint = true, true)
        getSplitPoint(raster, 0, endY + 1, startPoint = true, false)
        // No splits, or all splits.
        if (startX == 0 && endX == 0 && startY == 0 && endY == 0) return null
        // Subtraction here is because the coordinates were computed before the 1px border was stripped.
        if (startX != 0) {
            startX--
            endX = raster.width - 2 - (endX - 1)
        } else {
            // If no start point was ever found, we assume full stretch.
            endX = raster.width - 2
        }
        if (startY != 0) {
            startY--
            endY = raster.height - 2 - (endY - 1)
        } else {
            // If no start point was ever found, we assume full stretch.
            endY = raster.height - 2
        }

        return intArrayOf(startX, endX, startY, endY)
    }

    private fun getSplitPoint(
        raster: PixmapRegion,
        startX: Int,
        startY: Int,
        startPoint: Boolean,
        xAxis: Boolean,
    ): Int {
        var next = if (xAxis) startX else startY
        val end = if (xAxis) raster.width else raster.height
        val breakA = if (startPoint) 255 else 0
        var x = startX
        var y = startY
        while (next != end) {
            if (xAxis) x = next
            else y = next
            val a = raster.getA(x, y)
            if (a == breakA) return next

            next++
        }

        return 0
    }
}

