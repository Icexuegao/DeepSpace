package ice.entities.bullet

import ice.entities.bullet.base.BulletType
import mindustry.entities.Units
import mindustry.gen.Bullet
import mindustry.gen.Hitboxc
import mindustry.gen.Unit

open class SglEmpBulletType(speed: Float = 1f, damage: Float = 1f) : BulletType(speed, damage) {
    var empDamage: Float = 0f
    var empRange: Float = 0f

    override fun hitEntity(b: Bullet?, entity: Hitboxc?, health: Float) {
        super.hitEntity(b, entity, health)
        if (empDamage > 0) {
            if (entity is Unit) {
                entity.damage(empDamage)
            }
        }
    }

    override fun createSplashDamage(b: Bullet, x: Float, y: Float) {
        super.createSplashDamage(b, x, y)
        if (empRange > 0 && empDamage > 0) Units.nearbyEnemies(b.team, b.x, b.y, empRange) { u ->
            u.damage(empDamage)
        }
    }
}