package ice.library

import arc.Core
import arc.Events
import arc.math.Interp
import arc.scene.actions.Actions
import arc.scene.ui.layout.Table
import arc.struct.Seq
import ice.library.scene.tex.IStyles
import ice.library.scene.tex.IceColor
import ice.ui.dialog.AchievementDialog
import mindustry.Vars
import mindustry.ctype.Content
import mindustry.game.EventType
import mindustry.gen.Iconc
import mindustry.world.Tile

object EventType {
    fun Content.lazyInit(run: Runnable) {
        Events.on(EventType.ContentInitEvent::class.java) {
            run.run()
        }
    }

    class AchievementUnlockEvent(var achievement: AchievementDialog.Achievement)
    class LogisticsHubFire

    private val updates = Seq<Tile>(Tile::class.java)
    fun init() {
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
    }
}