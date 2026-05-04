package ice.entities.bullet.base

import arc.Events
import arc.util.pooling.Pools
import ice.game.EventType
import mindustry.gen.Bullet
import mindustry.gen.Groups
import universecore.world.Load

class IceBullet :Bullet() {
  companion object :Load {
    override fun setup() {
      Pools.get(Bullet::class.java, ::IceBullet)
    }
  }

  override fun add() {
    if (added) return
    index__all = Groups.all.addIndex(this)
    index__bullet = Groups.bullet.addIndex(this)
    index__draw = Groups.draw.addIndex(this)
    type.init(this)
    Events.fire(EventType.BulletInitEvent(this))
    added = true
    updateLastPosition()
  }
}