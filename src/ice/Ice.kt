package ice

import arc.Core
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
import ice.vars.SglTechThree
import ice.world.ICategory
import ice.world.content.blocks.abstractBlocks.IceBlock.Companion.requirements
import ice.world.content.blocks.effect.Noise2dBlock
import mindustry.Vars
import mindustry.mod.Mod
import mindustry.mod.Mods

open class Ice : Mod() {
    companion object {
        val mod: Mods.LoadedMod by lazy { Vars.mods.getMod(Ice::class.java) }
        val name = IFiles.getModName()
        const val displayName = "DeepSpace"
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
    }

    override fun init() {
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
        Remainss.init()
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
