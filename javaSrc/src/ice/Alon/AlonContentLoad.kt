package ice.Alon

import arc.Events
import arc.graphics.g2d.Draw
import arc.graphics.g2d.Lines
import arc.math.geom.Vec2
import arc.struct.Seq
import arc.util.Log
import arc.util.serialization.Json
import arc.util.serialization.JsonValue
import ice.Alon.Parse.JTContents
import ice.Alon.Text.AStarBlock
import ice.Alon.Text.Conveyor
import ice.Alon.Text.Noise2dBlock
import ice.Alon.async.CeProcess
import ice.Alon.async.CeProcess.polyVecs
import ice.Alon.content.*
import ice.Alon.content.blocks.IceBlocks
import ice.Alon.library.drawUpdate.DrawUpdates
import ice.Alon.library.drawUpdate.DrawUpdates.Companion.updateSeq
import ice.Alon.library.drawUpdate.DrawUpdates.DrawUpdate
import ice.Alon.music.IceMusics
import ice.Alon.ui.Ui
import ice.Ice
import mindustry.Vars
import mindustry.content.Liquids
import mindustry.content.UnitTypes
import mindustry.ctype.UnlockableContent
import mindustry.game.EventType
import mindustry.gen.Groups
import mindustry.gen.Unit
import mindustry.io.JsonIO
import mindustry.type.Liquid
import mindustry.type.LiquidStack
import mindustry.world.meta.BuildVisibility
import org.jetbrains.kotlin.gradle.targets.js.extractWithUpToDate

/**
 * 用于辅助加载content内容防止主类紊乱
 */
object AlonContentLoad {
    /**来点单例*/
    fun init() {
        Vars.asyncCore.processes.clear().add(CeProcess())
        Events.on(ice.Alon.game.EventType.SaveExitEvent::class.java) { updateSeq.each { if (!it.overall) it.kill() } }
        Events.on(EventType.WorldLoadEndEvent::class.java) {
            object : DrawUpdate() {
                lateinit var seq: Seq<Unit>
                override fun update() {
                    seq = Groups.unit.copy()
                    super.update()
                }

                override var overall = false

                override fun draw() {
                    seq.forEach {
                        if (it.type.name == UnitTypes.omura.name) {
                            for (i in polyVecs.indices) {
                                val vec2 = Vec2(polyVecs[i].x, polyVecs[i].y)
                                val vec22: Vec2 = if (i + 1 < polyVecs.size) {
                                    Vec2(polyVecs[i + 1].x, polyVecs[i + 1].y)
                                } else {
                                    Vec2(polyVecs[0].x, polyVecs[0].y)
                                }
                                vec2.rotate(it.rotation)
                                vec22.rotate(it.rotation)
                                Draw.z(100f)
                                Lines.line(it.x + vec2.x, it.y + vec2.y, it.x + vec22.x, it.y + vec22.y)
                            }
                        }

                    }
                    super.draw()
                }
            }

        }
        Ui.load()
        DrawUpdates.load()
        Vars.content.each {
            if (it is UnlockableContent) {
                it.unlock()
            }
        }
    }

    fun load() {
        IceMusics.load()
        IceItems.load()
        IceLiquids.load()
        IceStatus.load()
        IceBlocks.load()
        IceUnitTypes.load()
        IcePlanets.load()
        IceWeathers.load()
        text()
    }

    private fun text() {
        Noise2dBlock("1223")
        Vars.content.blocks().select { it.minfo.mod === Ice.ice }.forEach {
            it.buildVisibility = BuildVisibility.shown
        }
        Conveyor("armored-conveyor")
        AStarBlock("Astar")
        JTContents.load()
    }
}

