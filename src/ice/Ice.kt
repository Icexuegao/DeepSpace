package ice

import arc.Core
import arc.Events
import arc.assets.Loadable
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
import ice.shader.SglShaders
import ice.ui.UI
import ice.ui.bundle.BaseBundle
import ice.ui.dialog.RemainsDialog
import ice.ui.fragment.ConversationFragment
import ice.vars.SglTechThree
import ice.vars.SglTechTreeDialog
import ice.world.content.blocks.abstractBlocks.IceBlock.Companion.requirements
import ice.world.content.blocks.effect.Noise2dBlock
import mindustry.Vars
import mindustry.mod.Mod
import mindustry.mod.Mods
import mindustry.type.Category

open class Ice : Mod(), Loadable {
    companion object {
        val mod: Mods.LoadedMod by lazy { Vars.mods.getMod(Ice::class.java) }
        val name = IFiles.getModName()
        val configIce = SettingValue()
    }

    init {
        IFiles.init()
        configIce.init()
        IceRegister.init()
        BulletType.init()
        Vars.control.sound = SoundControl()
        EventType.init()
        Core.assets.load(this)
    }

    override fun loadSync() {
        Schematics.loadSync()
        Vars.asyncCore.processes.add(ParcelProcess)
    }

    override fun init() {
        if (!Core.app.isHeadless) {
            //设置屏幕采样器
            ScreenSampler.setup()
            //载入着色器
            SglShaders.load()
            //载入数学着色器
            MathRenderer.load()
            //加载绘制资源
            SglDrawConst.load()
        }
        SglTechTreeDialog().show()
        UI.init()/*val baseDialog = BaseDialog("xx")
        baseDialog.addCloseButton(200f)
        baseDialog.cont.add(Markdown("""
            ## 一个多方位内容的模组,从星球到建筑,摒弃过量数值内容,争取用机制来减少同质化问题

            ### 墓前还是开发中[悲]

            有多余的问题或者建议?

            ### 2315079583 this is my fucking QQ
        """.trimIndent(),MarkdownStyles.defaultMD)).grow()

        baseDialog.show()*/
        RemainsDialog.load()
        Events.on(mindustry.game.EventType.WorldLoadEndEvent::class.java) {
            "吾等之菌脉已覆盖此界17.6%表层,钢铁残骸转化效能62%,血肉养料吸收效能98%,已在稗落星净水厂和帝国\"雾棺\"实验室建立了隐秘节点\n集群适应性提升,有机体神经链接效率40%.帝国疆域12.7%,圣殿领地5.3%已接入永恒之网"
            //  ConversationFragment.blankScreenText("<<碎片回响>>", text)
            ConversationFragment.showText("血肉肿瘤", "{SPEED=0.1}耳边萦绕着碎片的尖啸割裂了现实的帷幕")
            // VoiceoverFragment.blankScreenBottom()
        }
    }

    override fun loadContent() {
        IceTeam.load()
        Noise2dBlock("noise2d").apply {
            requirements(Category.distribution, IItems.钴锭, 10)
        }
        IItems.load()
        ILiquids.load()
        IStatus.load()
        IBlocks.load()
        IWeathers.load()
        IPlanets.load()
        IUnitTypes.load()
        SglTechThree.load()
        BaseBundle.load()
    }
}
