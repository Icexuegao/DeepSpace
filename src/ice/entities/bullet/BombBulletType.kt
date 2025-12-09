package ice.entities.bullet

import ice.entities.bullet.base.BasicBulletType
import mindustry.gen.Sounds

open class BombBulletType(damage: Float, radius: Float) :
    BasicBulletType(0.7f) {
    init {
        this.damage=0f
        splashDamageRadius = radius
        splashDamage = damage
        collidesTiles = false
        collides = false
        lifetime = 30f
        keepVelocity = false
        collidesAir = false
        hitSound = Sounds.explosion
    }
}