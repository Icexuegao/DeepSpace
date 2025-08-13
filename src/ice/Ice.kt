package ice

import arc.Events
import ice.content.*
import ice.library.EventType
import ice.library.baseContent.blocks.effect.Noise2dBlock
import ice.library.scene.tex.IStyles
import ice.ui.bundle.BaseBundle
import ice.ui.fragment.ConversationFragment
import ice.vars.UI
import mindustry.Vars
import mindustry.mod.Mod
import mindustry.mod.Mods
import mindustry.world.blocks.defense.Wall
import mindustry.world.meta.BuildVisibility

class Ice : Mod() {
    companion object {
        val ice: Mods.LoadedMod by lazy { Vars.mods.getMod(Ice::class.java) }
        val name: String by lazy { ice.name }
    }

    init {
        EventType.init()
    }

    override fun init() {
        BaseBundle.load()
        /* val effectBuffer = FrameBuffer()
         effectBuffer.resize(Core.graphics.width, Core.graphics.height)
         Events.run(mindustry.game.EventType.Trigger.draw) {
             Draw.drawRange(df, {
                 effectBuffer.begin(Color.clear)
             }) {
                 effectBuffer.end()
                 effectBuffer.blit(IceShader.shieldShader)
             }
         }*/
        UI.init()
        Events.on(mindustry.game.EventType.WorldLoadEndEvent::class.java) {
            val text = "吾等之菌脉已覆盖此界17.6%表层,钢铁残骸转化效能62%,血肉养料吸收效能98%,已在稗落星净水厂和帝国\"雾棺\"实验室建立了隐秘节点\n集群适应性提升,有机体神经链接效率40%.帝国疆域12.7%,圣殿领地5.3%已接入永恒之网"
          //  ConversationFragment.blankScreenText("<<碎片回响>>", text)
            ConversationFragment.showText("章节: 碎片回响","耳边萦绕着碎片的尖啸割裂了现实的帷幕", back = IStyles.background121)
           // VoiceoverFragment.blankScreenBottom()

            /*IcePlanets.阿德里.sectors.forEach { s ->
                s.info.wasCaptured = true
                s.info.hasCore = true
                Vars.state.rules.sector = s
                if (!s.hasSave()) {
                    Vars.state.wave = 999
                    Vars.control.saves.saveSector(s)
                }
                s.saveInfo()
            }*//*Events.run(mindustry.game.EventType.Trigger.draw) {
            Groups.unit.forEach {
                Draw.z(120f)
                Drawf.circles(it.x, it.y, it.hitSize / 2)
            }
        }*/
        }
    }

    override fun loadContent() {
        Noise2dBlock("noise2d")
        IItems.load()
        ILiquids.load()
        IStatus.laod()
        IBlocks.load()
        IPlanets.load()
        IUnitTypes.load()
        //  JTContents.load("IceContent", ice)
        object : Wall("反应堆核心") {
            init {
                size = 10
                buildVisibility = BuildVisibility.shown
            }
        }

    }
}