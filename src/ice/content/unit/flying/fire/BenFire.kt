package ice.content.unit.flying.fire

import arc.graphics.Color
import ice.entities.bullet.BombBulletType
import ice.ui.bundle.BaseBundle
import ice.world.content.unit.IceUnitType
import mindustry.content.Fx
import mindustry.content.StatusEffects
import mindustry.entities.abilities.ArmorPlateAbility
import mindustry.entities.abilities.ShieldRegenFieldAbility
import mindustry.entities.effect.WrapEffect
import mindustry.entities.pattern.ShootPattern
import mindustry.gen.Sounds

class BenFire : IceUnitType("benFire") {
  init {
    circleTarget = true
    faceTarget = false
    targetAir = false
    flying = true
    health = 1150f
    hitSize = 21f
    armor = 7f
    range = 40f
    accel = 0.07f
    drag = 0.016f
    speed = 2.4f
    rotateSpeed = 4.5f
    engineSize = 3f
    engineOffset = 7.5f
    trailLength = 4
    engineLayer = 110f
    BaseBundle.Companion.bundle {
      desc(zh_CN, "奔火", "中型轰炸机,以趋火为基础提升航弹装药量并加装护盾发生器以持续作战")
    }
    immunities.add(StatusEffects.wet)
    abilities.add(ArmorPlateAbility().apply {
      healthMultiplier = 0.25f
    }, ShieldRegenFieldAbility(40f, 240f, 120f, 120f))
    setWeapon {
      reload = 70f
      shootCone = 360f
      shoot = ShootPattern().apply {
        shots = 5
        shotDelay = 10f
      }
      ignoreRotation = true
      minShootVelocity = 0.04f
      shootSound = Sounds.drillImpact
      bullet = BombBulletType(60f, 40f).apply {
        sprite = "large-bomb"
        spin = 3f
        width = 16f
        height = 16f
        shrinkX = 0.9f
        shrinkY = 0.9f
        speed = 0f
        lifetime = 60f
        absorbable = false
        backColor = Color.valueOf("#FF5845")
        frontColor = Color.valueOf("#FF8663")
        hitSound = Sounds.explosionReactor
        hitEffect = WrapEffect(Fx.dynamicSpikes, Color.valueOf("#FF8663")).apply {
          rotation = 48f
        }
        hitShake = 3f
        despawnEffect = Fx.massiveExplosion
      }
    }
  }
}