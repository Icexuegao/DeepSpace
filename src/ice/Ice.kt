package ice

import arc.Core
import arc.Events
import arc.graphics.Color
import arc.graphics.g2d.Draw
import arc.graphics.gl.FrameBuffer
import ice.content.*
import ice.library.EventType
import ice.library.type.baseContent.blocks.effect.Noise2dBlock
import ice.parse.JTContents
import ice.shader.IceShader
import ice.ui.bundle.BaseBundle
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
        val df = 250f
    }
    init {
        EventType.init()
    }

    override fun init() {
        val effectBuffer = FrameBuffer()
        effectBuffer.resize(Core.graphics.width, Core.graphics.height)
        Events.run(mindustry.game.EventType.Trigger.draw) {
            Draw.drawRange(df, {
                effectBuffer.begin(Color.clear)
            }) {
                effectBuffer.end()
                effectBuffer.blit(IceShader.shieldShader)
            }
        }
        UI.init()
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

    override fun loadContent() {
        Noise2dBlock("noise2d")
        IceItems.load()
        IceLiquids.load()
        IceStatus.laod()
        IceBlocks.load()
        IcePlanets.load()
        IceUnitTypes.load()
        JTContents.load("IceContent", ice)
        BaseBundle.load()

        object : Wall("反应堆核心") {
            init {
                size = 10
                buildVisibility = BuildVisibility.shown
            }
        }
    }
}