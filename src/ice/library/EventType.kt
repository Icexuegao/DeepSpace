package ice.library

import arc.Core
import arc.Events
import arc.struct.Seq
import arc.util.Interval
import ice.library.struct.ifTrue
import ice.library.type.components.BuildInterface
import ice.library.type.components.block.BlockDrawSelect
import ice.library.type.components.block.BlockUpdate
import ice.ui.dialog.AchievementDialog
import mindustry.Vars
import mindustry.game.EventType
import mindustry.world.Tile

object EventType {
    class AchievementUnlockEvent(var achievement: AchievementDialog.Achievement)
    private val updates = Seq<Tile>(Tile::class.java)
    fun init() {
        val d = Interval(1)
        Events.run(EventType.Trigger.update) {
            Vars.state.isGame.ifTrue {
                updates.forEach {
                    val block = it.block()
                    if (block is BlockUpdate) {
                        block.update(it)
                    } else {
                        updates.remove(it)
                    }
                }
            }
        }

        Events.run(EventType.Trigger.draw) {
            Vars.state.isGame.ifTrue {
                val mouseWorld = Core.input.mouseWorld()
                val tileWorld = Vars.world.tileWorld(mouseWorld.x, mouseWorld.y) ?: return@ifTrue
                val block = tileWorld.block()
                if (block is BlockDrawSelect) {
                    block.draw(tileWorld)
                }
            }
        }
        Events.on(EventType.ResetEvent::class.java) {
            updates.clear()
        }
        Events.on(EventType.WorldLoadEndEvent::class.java) {
            Vars.world.tiles.forEach {
                if (it == null) return@forEach
                val build = it.build
                if (build is BuildInterface.BuildWorldLoadEndEvent) {
                    build.worldLoadEvent()
                }
            }
        }
        Events.on(EventType.BlockBuildEndEvent::class.java) {
            val block = it.tile.block()
            if (block is BlockUpdate) updates.add(it.tile)
        }
    }
}