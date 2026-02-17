package ice.content.unit.flying.fire

import ice.entities.bullet.BombBulletType
import ice.ui.bundle.BaseBundle
import ice.world.content.unit.IceUnitType
import mindustry.content.Fx
import mindustry.content.StatusEffects
import mindustry.entities.abilities.ArmorPlateAbility
import mindustry.entities.pattern.ShootPattern
import mindustry.gen.Sounds

class Tuihuo : IceUnitType("tuihuo") {
  init {
    BaseBundle.Companion.bundle {
      desc(zh_CN, "趋火", "轻型轰炸机,配备五联装投弹器以快速杀伤敌军")
    }
    immunities.add(StatusEffects.wet)
    abilities.add(ArmorPlateAbility().apply {
      healthMultiplier = 0.2f
    })
    circleTarget = true
    faceTarget = false
    targetAir = false
    flying = true
    health = 675f
    hitSize = 13f
    armor = 5f
    range = 40f
    accel = 0.08f
    drag = 0.016f
    speed = 3f
    rotateSpeed = 5f
    engineSize = 2.5f
    engineOffset = 7f
    trailLength = 4
    engineLayer = 110f
    setWeapon {
      reload = 55f
      shootCone = 360f
      shoot = ShootPattern().apply {
        shots = 5
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
  }
}