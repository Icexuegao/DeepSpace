package ice

import arc.Core
import arc.Events
import arc.assets.Loadable
import arc.graphics.Texture
import ice.content.*
import ice.library.EventType
import ice.library.Schematics
import ice.library.content.blocks.effect.Noise2dBlock
import ice.music.SoundControl
import ice.ui.BaseBundle
import ice.ui.dialog.RemainsDialog
import ice.vars.UI
import mindustry.Vars
import mindustry.mod.Mod
import mindustry.mod.Mods
import mindustry.world.blocks.defense.Wall
import mindustry.world.meta.BuildVisibility

open class Ice : Mod(), Loadable {
    companion object {
        val mod: Mods.LoadedMod by lazy { Vars.mods.getMod(Ice::class.java) }
        val name: String by lazy { mod.name }
    }

    init {
        Core.assets.load(this)
        Vars.control.sound = SoundControl
        EventType.init()
    }

    override fun loadSync() {
        Schematics.loadSync()
    }

    override fun init() {
        Core.atlas.textures.forEach {
            val fid = Texture.TextureFilter.nearest
            it.setFilter(fid, fid)
        }
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
        RemainsDialog.load()
        Events.on(mindustry.game.EventType.WorldLoadEndEvent::class.java) {
            val text = "吾等之菌脉已覆盖此界17.6%表层,钢铁残骸转化效能62%,血肉养料吸收效能98%,已在稗落星净水厂和帝国\"雾棺\"实验室建立了隐秘节点\n集群适应性提升,有机体神经链接效率40%.帝国疆域12.7%,圣殿领地5.3%已接入永恒之网"
            //  ConversationFragment.blankScreenText("<<碎片回响>>", text)
            //   ConversationFragment.showText("章节: 碎片回响", "耳边萦绕着碎片的尖啸割裂了现实的帷幕", back = IStyles.background122)
            // VoiceoverFragment.blankScreenBottom()
        }
    }

    override fun loadContent() {
        Noise2dBlock("noise2d")
        IItems.load()
        ILiquids.load()
        IStatus.load()
        IBlocks.load()
        IWeathers.load()
        IPlanets.load()
        IUnitTypes.load()
        object : Wall("反应堆核心") {
            init {
                size = 10
                buildVisibility = BuildVisibility.shown
            }
        }
        BaseBundle.load()

    }
}