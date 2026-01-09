package ice.entities.bullet

import ice.entities.bullet.base.BulletType
import mindustry.content.Fx
import kotlin.math.max

/** 非绘制子弹类型的模板类，该子弹类型会爆炸并立即消失。  */
class ExplosionBulletType : BulletType {
    constructor(splashDamage: Float, splashDamageRadius: Float) {
        this.splashDamage = splashDamage
        this.splashDamageRadius = splashDamageRadius
        rangeOverride = max(rangeOverride, splashDamageRadius * 2f / 3f)
    }

    constructor()

    init {
        hittable = false
        lifetime = 1f
        speed = 0f
        rangeOverride = 20f
        shootEffect = Fx.massiveExplosion
        instantDisappear = true
        scaledSplashDamage = true
        killShooter = true
        collides = false
        keepVelocity = false
    }
}