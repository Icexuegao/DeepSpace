package ice.entities.bullet

import ice.entities.bullet.base.BasicBulletType
import mindustry.gen.Sounds

open class BombBulletType(splashDamage: Float = 1f, splashDamageRadius: Float = 1f, sprite: String = "shell") :
  BasicBulletType(0.7f, 0f, sprite) {
  init {
    this.splashDamage = splashDamage
    this.splashDamageRadius = splashDamageRadius
    collidesTiles = false
    collides = false
    shrinkY = 0.7f
    lifetime = 30f
    drag = 0.05f
    keepVelocity = false
    collidesAir = false
    hitSound = Sounds.explosion
  }
}
