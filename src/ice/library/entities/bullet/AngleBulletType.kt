package ice.library.entities.bullet

import arc.math.Angles
import arc.util.Time
import mindustry.gen.Bullet
import mindustry.gen.Unit

class AngleBulletType(speed: Float, damage: Float, lifetime: Float, var power: Float) : IceBasicBulletType() {
    init {
        shrinkY = 0f
        this.speed = speed
        this.damage = damage
        this.lifetime = lifetime
    }

    override fun update(b: Bullet) {
        super.update(b)
        val shooter = b.shooter()
        if (shooter is Unit) {
            val angle = Angles.angle(shooter.x, shooter.y, shooter.aimX, shooter.aimY)
            b.vel.setAngle(Angles.moveToward(b.rotation(), angle, Time.delta * this.power))
        }
    }
}