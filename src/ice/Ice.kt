package ice

import arc.Core
import arc.Events
import arc.files.Fi
import arc.graphics.Pixmap
import arc.graphics.Texture
import arc.graphics.g2d.TextureRegion
import ice.async.ParcelProcess
import ice.audio.SoundControl
import ice.content.*
import ice.core.SettingValue
import ice.entities.IceRegister
import ice.entities.bullet.base.BulletType
import ice.game.IceTeam
import ice.graphics.MathRenderer
import ice.graphics.ScreenSampler
import ice.graphics.SglDrawConst
import ice.library.EventType
import ice.library.IFiles
import ice.library.Schematics
import ice.library.universecore.UncCore
import ice.shader.SglShaders
import ice.ui.UI
import ice.ui.bundle.BaseBundle
import ice.ui.dialog.RemainsDialog
import ice.vars.SglTechThree
import ice.world.ICategory
import ice.world.content.blocks.abstractBlocks.IceBlock.Companion.requirements
import ice.world.content.blocks.effect.Noise2dBlock
import mindustry.Vars
import mindustry.ctype.ContentType
import mindustry.ctype.UnlockableContent
import mindustry.graphics.MultiPacker
import mindustry.graphics.MultiPacker.PageType
import mindustry.mod.Mod
import mindustry.mod.Mods

open class Ice : Mod() {
    companion object {
        val mod: Mods.LoadedMod by lazy { Vars.mods.getMod(Ice::class.java) }
        val name = IFiles.getModName()
    }

    init {
        UncCore.setup()
        IFiles.setup()
        SettingValue.setup()
        IceRegister.setup()
        BulletType.setup()
        Vars.control.sound = SoundControl()
        EventType.setup()
        ICategory.setup()
        IceTeam.setup()
        Events.on(mindustry.game.EventType.AtlasPackEvent::class.java){

        }
            val multiPacker = MultiPacker()
            init(multiPacker)
            multiPacker.flush(Texture.TextureFilter.linear, Core.atlas)


            val multiPacker2 = MultiPacker()
            init(multiPacker2)
         /* Vars.content.each {
                if (it is UnlockableContent&&it.minfo.mod==mod){
                    it.load()
                    it.loadIcon()
                    it.createIcons(multiPacker2)
                }
            }*/

            multiPacker2.flush(Texture.TextureFilter.linear, Core.atlas)
            multiPacker2.dispose()
            multiPacker.dispose()

    }

    fun getPage(file: Fi): PageType {
        val path = file.path()
        val type = when {
            path.contains("sprites-out/blocks/environment") -> PageType.environment
            path.contains("sprites-out/rubble") -> PageType.rubble
            path.contains("sprites-out/ui") -> PageType.ui
            else -> PageType.main
        }
        return type
    }

    fun init(packer: MultiPacker) {
        IFiles.sprites.forEach { (name, sprite) ->
            val dataWithoutHeader = sprite.readBytes()
            val pngHeader = byteArrayOf(0x89.toByte(), 0x50.toByte(), 0x4E.toByte(), 0x47.toByte(),
                0x0D.toByte(), 0x0A.toByte(), 0x1A.toByte(), 0x0A.toByte())
            val completeData = pngHeader + dataWithoutHeader
            val pixmap = Pixmap(completeData)

            val path = sprite.path()
            val name1 = "ice-" + sprite.nameWithoutExtension()
            packer.add(getPage(sprite), name1, pixmap)
            packer.add(PageType.environment, name1, pixmap)
            Core.atlas.addRegion(name1, TextureRegion(Texture(pixmap)))

            val type = when {
                path.contains("sprites-out/blocks") -> ContentType.block
                path.contains("sprites-out/items") -> ContentType.item
                path.contains("sprites-out/units") -> ContentType.unit
                else -> ContentType.error
            }
            packer.add(PageType.ui, "${type.name}-ice-"+sprite.nameWithoutExtension()+"-ui", pixmap)
        }
    }

    override fun init() {
        Vars.content.each {
            if (it is UnlockableContent&&it.minfo.mod==mod){
                it.load()
                it.loadIcon()
            }
        }
        UncCore.init()
        if (!Core.app.isHeadless) {
            //设置屏幕采样器
            ScreenSampler.init()
            //载入着色器
            SglShaders.load()
            //载入数学着色器
            MathRenderer.load()
            //加载绘制资源
            SglDrawConst.load()
        }
        //  SglTechTreeDialog().show()
        UI.init()
        RemainsDialog.init()
        Schematics.init()
        Vars.asyncCore.processes.add(ParcelProcess)
    }

    override fun loadContent() {
        Noise2dBlock("noise2d").apply {
            requirements(ICategory.矩阵, IItems.钴锭, 10)
        }
        IItems.load()
        ILiquids.load()
        IStatus.load()
        IUnitTypes.load()
        IBlocks.load()
        IWeathers.load()
        IPlanets.load()
        SglTechThree.load()
        BaseBundle.load()
    }
}
