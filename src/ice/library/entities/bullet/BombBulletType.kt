package ice.library.entities.bullet

import mindustry.gen.Sounds

open class BombBulletType(damage: Float, radius: Float, sprite: String = "bullet", apply: BombBulletType.() -> Unit = {}) :
    IceBasicBulletType(0.7f, sprite = sprite) {
    init {
        splashDamageRadius = radius
        splashDamage = damage
        collidesTiles = false
        collides = false
        lifetime = 30f
        keepVelocity = false
        collidesAir = false
        hitSound = Sounds.explosion
        apply(this)
    }
}