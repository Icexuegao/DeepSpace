package ice.library

import arc.Core
import arc.Events
import arc.math.Interp
import arc.scene.actions.Actions
import arc.scene.ui.layout.Table
import arc.struct.Seq
import ice.library.components.BuildInterface
import ice.library.components.block.BlockDrawSelect
import ice.library.components.block.BlockUpdate
import ice.library.scene.tex.Colors
import ice.library.scene.tex.IStyles
import ice.library.struct.ifTrue
import ice.ui.dialog.AchievementDialog
import mindustry.Vars
import mindustry.game.EventType
import mindustry.gen.Iconc
import mindustry.world.Tile

object EventType {
    class AchievementUnlockEvent(var achievement: AchievementDialog.Achievement)
     class LogisticsHubFire
    private val updates = Seq<Tile>(Tile::class.java)
    fun init() {
        Events.on(AchievementUnlockEvent::class.java){ event ->
            if (Vars.state.isMenu)return@on
            val table = Table(IStyles.background101).margin(15f)
            table.image(IStyles.achievementUnlock).size(50f)
            table.add("成就: ${event.achievement.name} 已解锁 ${Iconc.lockOpen}", Colors.b4)
            table.pack()
            val container = Core.scene.table()
            container.top().add(table)
            container.setTranslation(0f, table.prefHeight)
            container.actions(Actions.translateBy(0f, -table.prefHeight, 1f, Interp.fade),
                Actions.delay(2.5f),
                Actions.run {
                    container.actions(Actions.translateBy(0f, table.prefHeight, 1f, Interp.fade), Actions.remove())
                })
        }
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