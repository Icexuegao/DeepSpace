package ice.entities.bullet.base

import arc.math.Mathf
import arc.util.Tmp
import arc.util.pooling.Pools
import ice.content.IStatus
import ice.library.world.Load
import mindustry.gen.Bullet
import mindustry.gen.Unit

class IceBullet : Bullet() {
  companion object : Load {
    override fun setup() {
      Pools.get(Bullet::class.java, ::IceBullet)
    }
  }

  override fun add() {
    super.add()
    if (owner is Unit) {
      val u = owner as Unit
      if (u.hasEffect(IStatus.电子干扰)) {
        val deflect = 16.4f * Mathf.clamp(u.getDuration(IStatus.电子干扰) / 120)
        val rot = Mathf.random(-deflect, deflect)
        rotation(rotation() + rot)
        Tmp.v1.set(aimX - x, aimY - y).rotate(rot)
        aimX = Tmp.v1.x
        aimY = Tmp.v1.y
      }

      if (u.hasEffect(IStatus.电磁损毁)) {
        val rot = Mathf.random(-45, 45).toFloat()
        rotation(rotation() + rot)
        Tmp.v1.set(aimX - x, aimY - y).rotate(rot)
        aimX = Tmp.v1.x
        aimY = Tmp.v1.y
      }
    }
  }
}