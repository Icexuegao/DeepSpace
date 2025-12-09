package ice.entities.bullet

import ice.entities.bullet.base.BulletType
import mindustry.content.Fx
import mindustry.gen.Bullet

open class EffectBulletType(lifetime: Float) : BulletType() {
    init {

        this.lifetime = lifetime
        hittable = false
        despawnEffect = Fx.none
        hitEffect = Fx.none
        shootEffect = Fx.none
        smokeEffect = Fx.none
        trailEffect = Fx.none

        absorbable = false
        collides = false
        collidesAir = false
        collidesGround = false
        collidesTeam = false
        collidesTiles = false
        collideFloor = false
        collideTerrain = false

        hitSize = 0f
        speed = 0.0001f
        drawSize = 120f
    }

    override fun draw(b: Bullet) {}
    override fun drawLight(b: Bullet) {}
}














