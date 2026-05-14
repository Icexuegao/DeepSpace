package ice.game

import arc.Events
import arc.struct.Seq
import ice.entities.bullet.base.IceBullet
import ice.ui.menusDialog.AchievementDialog
import mindustry.game.EventType
import singularity.core.UpdateTiles
import universecore.world.Load

object EventType :Load {
  class AchievementUnlockEvent(var achievement: AchievementDialog.Achievement)
  class BulletInitEvent(val bullet: IceBullet)

  private val contentInitEvent = Seq<() -> Unit>()
  private val clientLoadEvent = Seq<() -> Unit>()

  override fun setup() {

    Events.on(EventType.ContentInitEvent::class.java) {
      contentInitEvent.forEach { it() }
    }
    Events.on(EventType.ClientLoadEvent::class.java) {
      clientLoadEvent.forEach { it() }
    }
    UpdateTiles.setup()
  }
  /** 添加内容初始化事件,在所以内容初始化以后调用*/
  fun addContentInitEvent(run: () -> Unit) {
    contentInitEvent.add(run)
  }
}