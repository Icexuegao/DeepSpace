package singularity.world.blocks.turrets

import arc.func.Cons
import ice.entities.bullet.base.BulletType
import mindustry.entities.Units
import mindustry.gen.Bullet
import mindustry.gen.Hitboxc
import mindustry.gen.Unit
import singularity.Sgl

open class EmpBulletType : BulletType {
  var empDamage: Float = 0f
  var empRange: Float = 0f

  constructor()

  constructor(speed: Float, damage: Float) {
    this.speed = speed
    this.damage = damage
  }

  override fun hitEntity(b: Bullet?, entity: Hitboxc?, health: Float) {
    super.hitEntity(b, entity, health)
    if (empDamage > 0) {
      if (entity is Unit) {
        Sgl.empHealth.empDamage(entity, empDamage, false)
      }
    }
  }

  override fun createSplashDamage(b: Bullet, x: Float, y: Float) {
    super.createSplashDamage(b, x, y)
    if (empRange > 0 && empDamage > 0) Units.nearbyEnemies(b.team, b.x, b.y, empRange, Cons { u: Unit? ->
      Sgl.empHealth.empDamage(u, empDamage, false)
    })
  }
}