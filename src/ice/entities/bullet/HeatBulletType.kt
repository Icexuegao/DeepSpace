package ice.entities.bullet

import arc.func.Cons
import ice.content.IStatus
import ice.entities.bullet.base.BulletType
import mindustry.entities.Units
import mindustry.gen.Bullet
import mindustry.gen.Hitboxc
import mindustry.gen.Unit
import kotlin.math.min

open class HeatBulletType : BulletType() {
  var melDamageScl: Float = 0.4f
  var maxExDamage: Float = -1f
  var meltDownTime: Float = 10f

  override fun hitEntity(b: Bullet?, entity: Hitboxc?, health: Float) {
    super.hitEntity(b, entity, health)
    if (entity is Unit) {
      val mel = entity.getDuration(IStatus.熔毁)

      entity.damage(min(mel * melDamageScl, if (maxExDamage < 0) damage else maxExDamage))

      entity.apply(IStatus.熔毁, mel + meltDownTime)
    }
  }

  override fun createSplashDamage(b: Bullet, x: Float, y: Float) {
    super.createSplashDamage(b, x, y)
    Units.nearbyEnemies(b.team, x, y, splashDamageRadius, Cons { u: Unit? ->
      val mel = u!!.getDuration(IStatus.熔毁)
      u.damage(min(mel * melDamageScl, if (maxExDamage < 0) splashDamage else maxExDamage))
      u.apply(IStatus.熔毁, mel + meltDownTime)
    })
  }
}


