package ice.content.unit.flying.fire

import arc.graphics.Color
import ice.content.IStatus
import ice.entities.bullet.BombBulletType
import ice.entities.bullet.base.BasicBulletType
import ice.entities.effect.MultiEffect
import ice.ui.bundle.BaseBundle.Companion.bundle
import ice.world.content.unit.IceUnitType
import mindustry.content.Fx
import mindustry.content.StatusEffects
import mindustry.entities.abilities.ArmorPlateAbility
import mindustry.entities.effect.ParticleEffect
import mindustry.entities.effect.WrapEffect
import mindustry.entities.pattern.ShootPattern
import mindustry.gen.Sounds

class FuFire : IceUnitType("fuFire") {
  init {
    bundle {
      desc(zh_CN, "赴火", "重型空中突击单位.投掷高爆航弹并辅以机炮攻击敌人,高级气动外壳保证飞行速度在大多数情况下不会降低,开火时减少所受伤害")
    }
    circleTarget = true
    flying = true
    health = 27000f
    hitSize = 48f
    armor = 25f
    range = 40f
    drag = 0.02f
    speed = 1.6f
    rotateSpeed = 3f
    engineSize = 5f
    engineOffset = 12f
    trailLength = 16
    engineLayer = 110f
    immunities.addAll(
      StatusEffects.wet, StatusEffects.burning, StatusEffects.freezing, StatusEffects.sporeSlowed, StatusEffects.tarred, StatusEffects.muddy, StatusEffects.electrified, IStatus.辐射
    )
    abilities.addAll(ArmorPlateAbility().apply {
      healthMultiplier = 0.5f
    })
    engines.addAll(UnitEngine().apply {
      x = 9f
      y = -10f
      radius = 4f
      rotation = -90f
    }, UnitEngine().apply {
      x = -9f
      y = -10f
      radius = 4f
      rotation = -90f
    })
    setWeapon("weapon1") {
      x = 19.25f
      y = 9.25f
      recoil = 3f
      shake = 2f
      reload = 60f
      shootY = 7.25f
      shoot = ShootPattern().apply {
        shots = 4
        shotDelay = 3f
      }
      rotate = true
      rotateSpeed = 2f
      alternate = false
      rotationLimit = 45f
      shootSound = Sounds.shoot
      layerOffset = -0.001f
      bullet = BasicBulletType().apply {
        damage = 135f
        speed = 4f
        lifetime = 48f
        drag = -0.01f
        width = 12f
        height = 15f
        splashDamage = 65f
        splashDamageRadius = 64f
        shootEffect = Fx.shootBig
        ammoMultiplier = 4f
        status = StatusEffects.blasted
        statusDuration = 60f
        hitEffect = Fx.flakExplosionBig
      }
    }
    setWeapon {
      x = 14f
      y = 6f
      shoot = ShootPattern().apply {
        shots = 4
        shotDelay = 30f
      }
      reload = 160f
      alternate = false
      shootCone = 360f
      ignoreRotation = true
      minShootVelocity = 0.04f
      shootSound = Sounds.shoot
      bullet = BombBulletType(90f, 60f).apply {
        sprite = "large-bomb"
        lifetime = 90f
        speed = 0f
        spin = 6f
        width = 32f
        height = 32f
        shrinkX = 0.9f
        shrinkY = 0.9f
        absorbable = false
        backColor = Color.valueOf("#FF5845")
        frontColor = Color.valueOf("#FF8663")
        despawnEffect = MultiEffect(
          WrapEffect(Fx.dynamicSpikes, Color.valueOf("FF5845"), 80f), ParticleEffect().apply {
            particles = 24
            sizeFrom = 9f
            sizeTo = 0f
            length = 80f
            baseLength = 8f
            lifetime = 30f
            colorFrom = Color.valueOf("FF5845")
            colorTo = Color.valueOf("FF8663")
            cone = 360f
          })

        hitShake = 4f
        hitSound = Sounds.explosionPlasmaSmall
        hitEffect = Fx.massiveExplosion
        lightning = 3
        lightningLength = 15
        lightningDamage = 75f
        lightningColor = Color.valueOf("FF5845")
        status = IStatus.熔融
        statusDuration = 180f
        fragBullets = 4
        fragLifeMin = 0.7f
        fragBullet = BombBulletType(60f, 40f).apply {
          sprite = "large-bomb"
          lifetime = 20f
          speed = 16f
          shrinkX = 0.9f
          shrinkY = 0.9f
          width = 8f
          height = 8f
          absorbable = false
          backColor = Color.valueOf("FF5845")
          frontColor = Color.valueOf("FF8663")
          hitSound = Sounds.explosionPlasmaSmall
          hitEffect = WrapEffect(Fx.dynamicSpikes, Color.valueOf("FF8663"), 48f)
          hitShake = 3f
          despawnEffect = Fx.massiveExplosion
        }
      }
    }
  }
}
