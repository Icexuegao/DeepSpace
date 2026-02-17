package ice.content.unit

import arc.graphics.Color
import arc.math.Interp
import ice.content.IStatus
import ice.content.IUnitTypes.星光
import ice.entities.bullet.BombBulletType
import ice.entities.bullet.base.BulletType
import ice.entities.effect.MultiEffect
import ice.ui.bundle.BaseBundle.Companion.bundle
import ice.world.content.unit.IceUnitType
import mindustry.content.Fx
import mindustry.content.StatusEffects
import mindustry.entities.abilities.ArmorPlateAbility
import mindustry.entities.effect.ParticleEffect
import mindustry.entities.effect.WaveEffect
import mindustry.entities.effect.WrapEffect
import mindustry.entities.pattern.ShootBarrel
import mindustry.entities.pattern.ShootPattern
import mindustry.gen.Sounds
import mindustry.type.UnitType
import singularity.world.blocks.turrets.EmpBulletType

class HuaFire : IceUnitType("huaFire") {
  init {
    bundle {
      desc(zh_CN, "化火", "重型空中突击单位.投掷高爆航弹并辅以机炮攻击敌人,特种装甲外壳使其足以应对绝大部分负面状况.开火时减少所受伤害")
    }

    circleTarget = true
    faceTarget = false
    flying = true
    health = 112000f
    hitSize = 58f
    armor = 34f
    range = 40f
    accel = 0.08f
    drag = 0.02f
    speed = 1.2f
    rotateSpeed = 1.8f
    payloadCapacity = 2304f
    engineSize = 6f
    engineOffset = 13f
    trailLength = 16
    engineLayer = 110f

    engines.add(UnitEngine().apply {
      x = 16f
      y = -22f
      radius = 5f
      rotation = -45f
    })
    engines.add(UnitEngine().apply {
      x = -16f
      y = -22f
      radius = 5f
      rotation = -135f
    })
    immunities.addAll(
      StatusEffects.burning, StatusEffects.melting, StatusEffects.blasted, StatusEffects.wet, StatusEffects.freezing, StatusEffects.sporeSlowed, StatusEffects.slow, StatusEffects.tarred, StatusEffects.muddy, StatusEffects.sapped, StatusEffects.electrified, StatusEffects.unmoving, IStatus.熔融, IStatus.辐射, IStatus.衰变
    )
    abilities.add(ArmorPlateAbility().apply {
      healthMultiplier = 0.8f
    })
    setWeapon {
      x = 31f
      y = -8f
      shoot = ShootBarrel().apply {
        shots = 3
        shotDelay = 5f
        barrels = floatArrayOf(0f, 0f, -20f, 0f, 0f, -30f, 0f, 0f, -40f)
      }
      shootY = 0f
      reload = 90f
      shootCone = 360f
      shootSound = Sounds.shootMissile
      bullet = BulletType().apply {
        spawnUnit = 星光
        speed = 0f
        shootEffect = ParticleEffect().apply {
          lifetime = 35f
          particles = 10
          length = 40f
          cone = 20f
          sizeFrom = 4f
          sizeTo = 0f
          baseRotation = 180f
          interp = Interp.circleOut
          colorFrom = Color.valueOf("FF8663")
          colorTo = Color.valueOf("FF5845")
        }
      }
    }
    setWeapon {
      x = 22f
      y = 12f
      shoot = ShootPattern().apply {
        shots = 4
        shotDelay = 30f
      }
      reload = 360f
      alternate = false
      shootCone = 360f
      ignoreRotation = true
      minShootVelocity = 0.04f
      shootSound = Sounds.shootBeamPlasma
      bullet = ice.entities.bullet.EmpBulletType().apply {
        sprite = "large-bomb"
        damage = 137f
        lifetime = 90f
        drag = 0.05f
        speed = 1f
        spin = 6f
        width = 32f
        height = 32f
        shrinkX = 0.9f
        shrinkY = 0.9f
        collides = false
        collidesAir = false
        absorbable = false
        collidesTiles = false
        keepVelocity = false
        backColor = Color.valueOf("FF5845")
        frontColor = Color.valueOf("FF8663")
        hitColor = Color.valueOf("FF5845")
        despawnEffect = MultiEffect(
          WrapEffect(Fx.dynamicSpikes, Color.valueOf("FF5845"), 120f), ParticleEffect().apply {
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
        radius = 120f
        unitDamageScl = 1.2f
        powerDamageScl = 1.5f
        hitPowerEffect = ParticleEffect().apply {
          particles = 7
          lifetime = 22f
          line = true
          lenFrom = 6f
          lenTo = 6f
          cone = 360f
          length = 80f
          colorFrom = Color.valueOf("FF8663")
          colorTo = Color.valueOf("FF5845")
        }
        status = IStatus.熔融
        statusDuration = 180f
        splashDamage = 915f
        splashDamageRadius = 120f
        scaledSplashDamage = true
        lightning = 3
        lightningLength = 11
        lightningLengthRand = 5
        lightningDamage = 75f
        lightningColor = Color.valueOf("FF5845")
        hitShake = 4f
        hitSound = Sounds.shootBeamPlasma
        hitEffect = Fx.massiveExplosion
        fragBullets = 1
        fragBullet = EmpBulletType().apply {
          sprite = "stardart"
          lifetime = 480f
          damage = 0f
          speed = 0f
          width = 8f
          height = 8f
          shrinkY = 0f
          collides = false
          collidesAir = false
          absorbable = false
          collidesTiles = false
          backColor = Color.valueOf("FF5845")
          frontColor = Color.valueOf("FF8663")
          hitColor = Color.valueOf("FF8663")
          despawnEffect = Fx.none
          status = IStatus.熔融
          statusDuration = 30f
          splashDamage = 537f
          splashDamageRadius = 80f
          scaledSplashDamage = true
          radius = 80f
          unitDamageScl = 1.2f
          powerDamageScl = 1.5f
          hitPowerEffect = ParticleEffect().apply {
            particles = 7
            lifetime = 22f
            line = true
            lenFrom = 6f
            lenTo = 6f
            cone = 360f
            length = 80f
            colorFrom = Color.valueOf("FF8663")
            colorTo = Color.valueOf("FF5845")
          }
          bulletInterval = 48f
          intervalBullet = EmpBulletType().apply {
            damage = 0f
            hittable = false
            collides = false
            collidesAir = false
            absorbable = false
            instantDisappear = true
            despawnShake = 1f
            hitColor = Color.valueOf("FF8663")
            status = IStatus.熔融
            statusDuration = 30f
            splashDamage = 73f
            splashDamageRadius = 48f
            scaledSplashDamage = true
            splashDamagePierce = true
            radius = 48f
            unitDamageScl = 1.2f
            powerDamageScl = 1.5f
            hitPowerEffect = ParticleEffect().apply {
              particles = 7
              lifetime = 22f
              line = true
              lenFrom = 6f
              lenTo = 6f
              cone = 360f
              length = 80f
              colorFrom = Color.valueOf("FF8663")
              colorTo = Color.valueOf("FF5845")
            }
            despawnEffect = Fx.none
            hitEffect = WaveEffect().apply {
              lifetime = 24f
              sizeFrom = 0f
              sizeTo = 48f
              strokeFrom = 4f
              strokeTo = 0f
              colorFrom = Color.valueOf("FF5845")
              colorTo = Color.valueOf("FF8663")
            }
          }
          fragBullets = 4
          fragBullet = BombBulletType(465f, 48f).apply {
            sprite = "large-bomb"
            lifetime = 20f
            speed = 16f
            shrinkX = 0.9f
            shrinkY = 0.9f
            drag = 0.2f
            width = 8f
            height = 8f
            collides = false
            collidesAir = false
            absorbable = false
            backColor = Color.valueOf("FF5845")
            frontColor = Color.valueOf("FF8663")
            incendAmount = 3
            status = IStatus.熔融
            statusDuration = 60f
            hitSound = Sounds.explosion
            hitEffect = WrapEffect(Fx.dynamicSpikes, Color.valueOf("FF8663"), 40f)
            hitShake = 3f
            despawnEffect = Fx.massiveExplosion
          }
        }
      }
    }
  }
}