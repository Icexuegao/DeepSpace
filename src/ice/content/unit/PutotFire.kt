package ice.content.unit

import ice.entities.bullet.BombBulletType
import ice.ui.bundle.BaseBundle.Companion.bundle
import ice.world.content.unit.IceUnitType
import mindustry.content.Fx
import mindustry.content.StatusEffects
import mindustry.entities.abilities.ArmorPlateAbility
import mindustry.entities.pattern.ShootPattern
import mindustry.gen.Sounds

class PutotFire : IceUnitType("putotFire") {
  init {
    circleTarget = true
    faceTarget = false
    targetAir = false
    flying = true
    health = 230f
    hitSize = 9f
    armor = 2f
    range = 40f
    accel = 0.08f
    drag = 0.04f
    speed = 3.6f
    rotateSpeed = 6f
    engineSize = 2f
    engineOffset = 4.5f
    trailLength = 4
    engineLayer = 110f
    abilities.add(ArmorPlateAbility().apply {
      healthMultiplier = 0.1f
    })

    setWeapon {
      reload = 65f
      shootCone = 360f
      shoot = ShootPattern().apply {
        shots = 3
        shotDelay = 5f
      }
      ignoreRotation = true
      minShootVelocity = 0.04f
      shootSound = Sounds.none
      bullet = BombBulletType(35f, 30f).apply {
        lifetime = 30f
        width = 9f
        height = 15f
        status = StatusEffects.blasted
        shootEffect = Fx.none
        smokeEffect = Fx.none
        hitEffect = Fx.flakExplosion
        despawnEffect = Fx.flakExplosion
      }
    }
    bundle {
      desc(zh_CN, "扑火", "微型轰炸机,以极高的机动性持续骚扰敌军")
    }
  }
}