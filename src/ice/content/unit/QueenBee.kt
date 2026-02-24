package ice.content.unit

import arc.func.Func
import arc.graphics.Color
import arc.math.Interp
import ice.audio.ISounds
import ice.content.IStatus
import ice.entities.bullet.base.BulletType
import ice.entities.effect.MultiEffect
import ice.ui.bundle.BaseBundle
import ice.world.content.unit.IceUnitType
import mindustry.ai.types.DefenderAI
import mindustry.content.Fx
import mindustry.content.StatusEffects
import mindustry.entities.abilities.EnergyFieldAbility
import mindustry.entities.abilities.ShieldRegenFieldAbility
import mindustry.entities.abilities.StatusFieldAbility
import mindustry.entities.bullet.MissileBulletType
import mindustry.entities.effect.ParticleEffect
import mindustry.entities.effect.WaveEffect
import mindustry.entities.pattern.ShootPattern
import mindustry.gen.PayloadUnit
import mindustry.gen.Sounds
import mindustry.type.weapons.PointDefenseWeapon
import mindustry.type.weapons.RepairBeamWeapon

class QueenBee : IceUnitType("unit_queenBee", PayloadUnit::class.java) {
  init {
    BaseBundle.bundle {
      desc(zh_CN, "蜂后", "重型空中支援单位.发射追踪湍能弹攻击敌人,加装护盾辅助发生器以维持友军护盾持续作战.会以闪电场电击附近敌军并治疗友军,并对附近的友军提供迅疗效果.可携带大型单位或建筑进行部署","当它那庞大的堡垒碾碎星空的寂静时,敌人心中升起的将不再是恐惧,而是某种顿悟;这是一场早已写定的终局,是他们世间账上最后一笔清算.")
    }
    controller = Func { DefenderAI() }
    researchCostMultiplier = 40f
    lowAltitude = true
    faceTarget = false
    flying = true
    health = 138000f
    armor = 32f
    hitSize = 91f
    speed = 0.6f
    drag = 0.18f
    accel = 0.19f
    rotateSpeed = 0.9f
    buildSpeed = 8f
    engineSize = 10.6f
    engineOffset = 40f
    payloadCapacity = 16384f

    abilities.add(EnergyFieldAbility(225f, 30f, 240f).apply {
      y = -0.7f
      rotateSpeed = 3f
      maxTargets = 50
      healPercent = 2.5f
      status = IStatus.湍能
      statusDuration = 120f
    }, StatusFieldAbility(IStatus.迅疗, 150f, 300f, 320f).apply {
      activeEffect = WaveEffect().apply {
        lifetime = 30f
        sizeTo = 320f
        strokeFrom = 2f
        colorFrom = Color.valueOf("57D993")
        colorTo = Color.valueOf("73FFAE")
      }
      applyEffect = WaveEffect().apply {
        sides = 4
        lifetime = 30f
        sizeFrom = 8f
        sizeTo = 8f
        strokeFrom = 1.6f
        colorFrom = Color.valueOf("57D993")
        colorTo = Color.valueOf("73FFAE")
      }
    }, ShieldRegenFieldAbility(200f, 4000f, 65f, 240f).apply {
      activeEffect = WaveEffect().apply {
        lifetime = 35f
        sizeFrom = 16f
        sizeTo = 240f
        strokeFrom = 10f
        strokeTo = 0f
        colorFrom = Color.valueOf("FFD37F")
        colorTo = Color.valueOf("FFD37F")
      }
      applyEffect = WaveEffect().apply {
        lifetime = 60f
        sides = 6
        sizeFrom = 24f
        sizeTo = 0f
        strokeFrom = 0f
        strokeTo = 3f
        interp = Interp.exp10Out
        colorFrom = Color.valueOf("FFD37F")
        colorTo = Color.valueOf("FFD37F")
      }
    })

    setWeapon {
      x = 0f
      y = -0.7f
      shoot = ShootPattern().apply {
        shots = 5
        shotDelay = 5f
      }
      recoil = 0f
      reload = 90f
      rotate = true
      mirror = false
      shootCone = 5f
      inaccuracy = 15f
      rotateSpeed = 20f
      shootSound = ISounds.激射
      bullet = MissileBulletType().apply {
        sprite = "circle-bullet"
        damage = 88f
        lifetime = 60f
        speed = 12f
        drag = 0.015f
        width = 12f
        height = 12f
        shrinkY = 0f
        weaveMag = 2f
        weaveScale = 6f
        trailLength = 12
        trailWidth = 4f
        trailColor = Color.valueOf("84F491")
        backColor = Color.valueOf("73D188")
        frontColor = Color.valueOf("84F491")
        healPercent = 5f
        homingDelay = 20f
        homingRange = 80f
        homingPower = 0.04f
        status = StatusEffects.electrified
        statusDuration = 90f
        splashDamage = 65f
        splashDamageRadius = 45f
        reflectable = false
        collidesTeam = true
        shootEffect = ParticleEffect().apply {
          line = true
          particles = 7
          lifetime = 22f
          length = 35f
          cone = 60f
          lenFrom = 5f
          lenTo = 0f
          colorFrom = Color.valueOf("84F491")
          colorTo = Color.valueOf("73D188")
        }
        hitSound = Sounds.explosionPlasmaSmall
        hitEffect = MultiEffect(ParticleEffect().apply {
          particles = 5
          lifetime = 25f
          line = true
          sizeFrom = 4f
          sizeTo = 0f
          lenFrom = 6f
          lenTo = 0f
          length = 80f
          baseLength = 8f
          interp = Interp.exp10Out
          sizeInterp = Interp.pow5In
          colorFrom = Color.valueOf("73D188")
          colorTo = Color.valueOf("84F491")
        }, WaveEffect().apply {
          lifetime = 15f
          strokeFrom = 2f
          strokeTo = 0f
          sizeFrom = 0f
          sizeTo = 40f
          colorFrom = Color.valueOf("73D188")
          colorTo = Color.valueOf("84F491")
        })
        despawnEffect = MultiEffect(ParticleEffect().apply {
          particles = 8
          lifetime = 30f
          sizeFrom = 4f
          sizeTo = 0f
          length = 80f
          baseLength = 8f
          interp = Interp.pow2Out
          colorFrom = Color.valueOf("73D188")
          colorTo = Color.valueOf("84F491")
        }, WaveEffect().apply {
          lifetime = 10f
          sizeFrom = 8f
          sizeTo = 100f
          strokeFrom = 3f
          strokeTo = 0f
          colorFrom = Color.valueOf("73D188")
          colorTo = Color.valueOf("84F491")
        })
      }
    }

    setWeaponT<RepairBeamWeapon>("x") {
      x = 29.5f
      y = 46f
      shootY = 8f
      reload = 1f
      recoil = 0f
      shake = 0f
      mirror = true
      rotate = true
      rotateSpeed = 3f
      laserColor = Color.valueOf("73FFAE")
      controllable = false
      autoTarget = true
      alternate = false
      beamWidth = 0.9f
      pulseRadius = 6f
      pulseStroke = 2f
      repairSpeed = 13f
      bullet = BulletType().apply {
        maxRange = 360f
      }
    }

    setWeaponT<RepairBeamWeapon>("x") {
      x = 51f
      y = 27f
      shootY = 8f
      reload = 1f
      recoil = 0f
      shake = 0f
      mirror = true
      rotate = true
      rotateSpeed = 3f
      laserColor = Color.valueOf("73FFAE")
      controllable = false
      autoTarget = true
      alternate = false
      beamWidth = 0.9f
      pulseRadius = 6f
      pulseStroke = 2f
      repairSpeed = 13f
      bullet = BulletType().apply {
        maxRange = 360f
      }
    }

    setWeaponT<RepairBeamWeapon>("x") {
      x = 29.5f
      y = 22.5f
      shootY = 8f
      reload = 1f
      recoil = 0f
      shake = 0f
      mirror = true
      rotate = true
      rotateSpeed = 3f
      laserColor = Color.valueOf("73FFAE")
      controllable = false
      autoTarget = true
      alternate = false
      beamWidth = 0.9f
      pulseRadius = 6f
      pulseStroke = 2f
      repairSpeed = 13f
      bullet = BulletType().apply {
        maxRange = 360f
      }
    }

    setWeaponT<RepairBeamWeapon>("x") {
      x = 36.5f
      y = 2f
      shootY = 8f
      reload = 1f
      recoil = 0f
      shake = 0f
      mirror = true
      rotate = true
      rotateSpeed = 3f
      laserColor = Color.valueOf("73FFAE")
      controllable = false
      autoTarget = true
      alternate = false
      beamWidth = 0.9f
      pulseRadius = 6f
      pulseStroke = 2f
      repairSpeed = 13f
      bullet = BulletType().apply {
        maxRange = 360f
      }
    }

    setWeaponT<PointDefenseWeapon>("裂解") {
      x = 0f
      y = -0.7f
      recoil = 0f
      reload = 6f
      color = Color.valueOf("73FFAE")
      targetInterval = 6f
      targetSwitchInterval = 6f
      shootSound = Sounds.shootLaser
      bullet = BulletType().apply {
        maxRange = 360f
        damage = 225f
        shootEffect = Fx.sparkShoot
        hitEffect = Fx.pointHit
      }
    }
  }
}