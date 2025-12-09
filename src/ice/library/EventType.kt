package ice.library

import arc.Core
import arc.Events
import arc.input.KeyCode
import arc.math.Interp
import arc.scene.actions.Actions
import arc.scene.ui.layout.Table
import arc.struct.Seq
import arc.util.Tmp
import ice.graphics.IStyles
import ice.graphics.IceColor
import ice.ui.dialog.AchievementDialog
import ice.world.content.blocks.distribution.conveyor.PackStack
import mindustry.Vars
import mindustry.game.EventType
import mindustry.gen.Groups
import mindustry.gen.Iconc
import mindustry.world.Tile

object EventType {
    fun lazyInit(run: Runnable) {
        inits.add(run)
    }

    class AchievementUnlockEvent(var achievement: AchievementDialog.Achievement)
    class LogisticsHubFire

    private val inits = Seq<Runnable>()
    private val updates = Seq<Tile>(Tile::class.java)
    fun init() {
        Events.on(EventType.ContentInitEvent::class.java) {
            inits.forEach { it.run() }
        }
        Events.on(AchievementUnlockEvent::class.java) { event ->
            if (Vars.state.isMenu) return@on
            val table = Table(IStyles.background101).margin(15f)
            table.image(IStyles.achievementUnlock).size(50f)
            table.add("成就: ${event.achievement.name} 已解锁 ${Iconc.lockOpen}", IceColor.b4)
            table.pack()
            val container = Core.scene.table()
            container.top().add(table)
            container.setTranslation(0f, table.prefHeight)
            container.actions(Actions.translateBy(0f, -table.prefHeight, 1f, Interp.fade), Actions.delay(2.5f),
                Actions.run {
                    container.actions(Actions.translateBy(0f, table.prefHeight, 1f, Interp.fade), Actions.remove())
                })
        }
        Events.on(EventType.ResetEvent::class.java) {
            updates.clear()
        }
        var df: PackStack? = null
        Events.run(EventType.Trigger.update) {
            if (Core.input.isTouched) {
                if(!Core.input.keyDown(KeyCode.mouseLeft))return@run
                val mouseWorld = Core.input.mouseWorld()
                val find = Groups.draw.find { entityc ->
                    entityc.dst2(mouseWorld.x, mouseWorld.y) <= 5 * 5 && entityc is PackStack
                } as? PackStack
                if (df == null) {
                    df = find
                }
            } else {
                df = null
            }
            df?.let {
                if (!it.added) {
                    df = null
                    return@run
                }
                val mouseWorld = Core.input.mouseWorld()
                val sub = Tmp.v1.set(mouseWorld).sub(df).scl(0.1f)
                if (!Vars.state.isPaused) it.move(sub)
            }

        }
    }
}