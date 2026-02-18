package ice.content.unit.flying.rain

import arc.graphics.Blending
import arc.graphics.Color
import ice.audio.ISounds
import ice.content.IStatus
import ice.entities.bullet.LaserBulletType
import ice.entities.bullet.base.BasicBulletType
import ice.entities.effect.MultiEffect
import ice.ui.bundle.BaseBundle
import ice.world.content.unit.IceUnitType
import mindustry.content.Fx
import mindustry.content.StatusEffects
import mindustry.entities.abilities.EnergyFieldAbility
import mindustry.entities.abilities.ShieldArcAbility
import mindustry.entities.abilities.SuppressionFieldAbility
import mindustry.entities.bullet.LightningBulletType
import mindustry.entities.effect.ParticleEffect
import mindustry.entities.effect.WaveEffect
import mindustry.entities.part.RegionPart
import mindustry.entities.pattern.ShootHelix
import mindustry.entities.pattern.ShootPattern
import mindustry.gen.Sounds

class ThunderTribulation : IceUnitType("unit_thunderTribulation") {
  init {
    BaseBundle.bundle {
      desc(zh_CN, "雷劫", "重型空中突击单位.发射高能激光和双联球状闪电并辅等离子速射炮攻击敌人,正面投射弧形护盾抵御攻击.在场时抑制敌方建筑修复能力,同时以闪电场电击附近敌军并治疗友军")
    }


    lowAltitude = true
    flying = true
    health = 33000f
    armor = 21f
    hitSize = 54f
    speed = 0.54f
    rotateSpeed = 1f
    drag = 0.4f
    range = 380f
    engineSize = 6f
    engineOffset = 34f

    engines.add(UnitEngine().apply {
      x = 14f
      y = -38f
      radius = 4f
      rotation = -90f
    }, UnitEngine().apply {
      x = -14f
      y = -38f
      radius = 4f
      rotation = -90f
    })

    parts.add(
      RegionPart().apply {
        suffix = "-glow"
        outline = false
        color = Color.valueOf("E58956")
        blending = Blending.additive
      })

    abilities.add(
      SuppressionFieldAbility().apply {
        y = 6f
        reload = 90f
        range = 200f
        orbRadius = 4.1f
        orbMidScl = 0.33f
        orbSinScl = 8f
        orbSinMag = 1f
        color = Color.valueOf("FEEBB3")
        particleColor = Color.valueOf("F3E979")
        particles = 15
        particleSize = 4f
        particleLen = 7f
        rotateScl = 3f
        particleLife = 110f
        active = true
        applyParticleChance = 13f
      })
    abilities.add(
      ShieldArcAbility().apply {
        //"y": -16,
        max = 6000f
        regen = 2.5f
        cooldown = 900f
        radius = 60f
        angle = 180f
        width = 6f
      })
    abilities.add(
      EnergyFieldAbility(225f, 240f, 400f).apply {
        maxTargets = 40
        healPercent = 1f
        y = 6f
        blinkScl = 80f
        sectors = 3
        sectorRad = 0.18f
        rotateSpeed = 3f
        effectRadius = 5.6f
        color = Color.valueOf("F3E979")
        hitEffect = ParticleEffect().apply {
          particles = 15
          line = true
          lenFrom = 10f
          lenTo = 0f
          strokeFrom = 2f
          strokeTo = 0f
          length = 35f
          baseLength = 0f
          lifetime = 10f
          colorFrom = Color.valueOf("F3E979")
          colorTo = Color.valueOf("FEEBB3")
          cone = 360f
        }
        healEffect = WaveEffect().apply {
          lifetime = 20f
          sizeFrom = 0f
          sizeTo = 13f
          sides = 4
          strokeFrom = 8f
          strokeTo = 0f
          colorFrom = Color.valueOf("F3E979")
          colorTo = Color.valueOf("FEEBB3")
        }
      })

    setWeapon("mainGun") {
      x = 0f
      mirror = false
      shake = 4f
      recoil = 0f
      shootY = 6f
      reload = 900f
      shoot = ShootPattern().apply {
        firstShotDelay = 130f
      }
      shootCone = 0.05f
      cooldownTime = 960f
      shootStatus = StatusEffects.unmoving
      shootStatusDuration = 150f
      soundPitchMin = 0.95f
      soundPitchMax = 2.05f
      chargeSound = Sounds.chargeLancer
      shootSound = Sounds.shootLancer
      bullet = LaserBulletType(2480f).apply {
        chargeEffect = MultiEffect(ParticleEffect().apply {
          line = true
          particles = 25
          offset = 55f
          lifetime = 130f
          length = 65f
          baseLength = -65f
          cone = -360f
          lenFrom = 20f
          lenTo = 0f
          colorFrom = Color.valueOf("FEEBB3")
          colorTo = Color.valueOf("F3E979")
        }, ParticleEffect().apply {
          particles = 1
          sizeFrom = 1f
          sizeTo = 18f
          length = 0f
          lifetime = 130f
          colorFrom = Color.valueOf("FEEBB3")
          colorTo = Color.valueOf("F3E979")
          cone = 360f
        }, WaveEffect().apply {
          lifetime = 80f
          sizeFrom = 127f
          sizeTo = 0f
          strokeFrom = 0f
          strokeTo = 8f
          colorFrom = Color.valueOf("FEEBB3")
          colorTo = Color.valueOf("F3E979")
        })
        length = 440f
        width = 50f
        lifetime = 45f
        lightningSpacing = 28f
        lightningLength = 15
        lightningDelay = 1f
        lightningLengthRand = 20
        lightningDamage = 100f
        lightningAngleRand = 40f
        lightColor = Color.valueOf("F3E979")
        lightningColor = Color.valueOf("FEEBB3")
        largeHit = true
        status = IStatus.湍能
        statusDuration = 180f
        hitColor = Color.valueOf("F3E979")
        hitEffect = ParticleEffect().apply {
          line = true
          particles = 15
          lifetime = 20f
          offset = 50f
          length = 55f
          cone = -360f
          lenFrom = 5f
          lenTo = 0f
          colorFrom = Color.valueOf("F3E979")
          colorTo = Color.valueOf("FEEBB3")
        }
        colors = arrayOf(
          Color.valueOf("FEEBB3AA"), Color.valueOf("F3E979"), Color.white
        )
        sideAngle = 30f
        sideLength = 60f
        sideWidth = 6f
      }
    }

    setWeapon("auxiliaryGun") {
      x = 16.75f
      y = -18.75f
      reload = 960f
      rotate = true
      rotateSpeed = 1.6f
      shoot = ShootHelix().apply {
        mag = 1f
        scl = 5f
      }
      recoil = 4f
      shootY = 4f
      shootSound = Sounds.shootArtillery
      bullet = BasicBulletType().apply {
        sprite = "circle-bullet"
        damage = 625f
        lifetime = 240f
        speed = 1.5f
        shrinkY = 0f
        width = 24f
        height = 24f
        hitSize = 25f
        knockback = 2f
        shootEffect = ParticleEffect().apply {
          particles = 9
          sizeFrom = 5f
          sizeTo = 0f
          length = 65f
          baseLength = 16f
          lifetime = 46f
          colorFrom = Color.valueOf("F3E979")
          colorTo = Color.valueOf("FEEBB3")
          cone = 60f
        }
        frontColor = Color.valueOf("FEEBB3")
        backColor = Color.valueOf("F3E979")
        trailColor = Color.valueOf("F3E979")
        trailLength = 36
        trailWidth = 6f
        pierce = true
        absorbable = false
        status = IStatus.湍能
        statusDuration = 180f
        splashDamage = 425f
        splashDamageRadius = 100f
        hitSound = Sounds.explosionPlasmaSmall
        bulletInterval = 2.5f
        intervalBullets = 1
        intervalBullet = LightningBulletType().apply {
          damage = 23f
          lightningColor = Color.valueOf("FEEBB3FA0")
          hitColor = Color.valueOf("FEEBB3A0")
          lightningLength = 6
          lightningLengthRand = 5
        }
        hitEffect = Fx.none
        despawnEffect = MultiEffect(ParticleEffect().apply {
          particles = 13
          lifetime = 35f
          sizeFrom = 8f
          sizeTo = 0f
          length = 85f
          baseLength = 16f
          colorFrom = Color.valueOf("F3E979")
          colorTo = Color.valueOf("FEEBB3")
          cone = 360f
        }, WaveEffect().apply {
          lifetime = 18f
          sizeFrom = 2f
          sizeTo = 90f
          strokeFrom = 6f
          strokeTo = 0f
          colorFrom = Color.valueOf("F3E979")
          colorTo = Color.valueOf("FEEBB3")
        })
        fragBullets = 8
        fragBullet = BasicBulletType().apply {
          sprite = "circle-bullet"
          damage = 325f
          speed = 1.5f
          lifetime = 160f
          width = 16f
          height = 16f
          shrinkY = 0f
          trailLength = 24
          trailWidth = 3f
          trailColor = Color.valueOf("F3E979")
          frontColor = Color.valueOf("FEEBB3")
          backColor = Color.valueOf("F3E979")
          pierce = true
          absorbable = false
          homingPower = 0.08f
          homingRange = 240f
          splashDamage = 185f
          splashDamageRadius = 50f
          hitEffect = Fx.hitLancer
          despawnEffect = MultiEffect(ParticleEffect().apply {
            particles = 7
            lifetime = 25f
            sizeFrom = 4f
            sizeTo = 0f
            length = 43f
            baseLength = 7f
            colorFrom = Color.valueOf("F3E979")
            colorTo = Color.valueOf("FEEBB3")
            cone = 360f
          }, WaveEffect().apply {
            lifetime = 18f
            sizeFrom = 1f
            sizeTo = 45f
            strokeFrom = 3f
            strokeTo = 0f
            colorFrom = Color.valueOf("F3E979")
            colorTo = Color.valueOf("FEEBB3")
          })
          bulletInterval = 2.5f
          intervalBullets = 1
          intervalBullet = LightningBulletType().apply {
            damage = 7f
            lightningColor = Color.valueOf("FEEBB3FA0")
            hitColor = Color.valueOf("FEEBB3A0")
            lightningLength = 5
            lightningLengthRand = 2
          }
        }
      }
    }

    setWeapon("sideGun") {
      x = 11f
      y = 23.75f
      shoot = ShootPattern().apply {
        shots = 6
        shotDelay = 5f
      }
      recoil = 2f
      shootY = 5f
      reload = 80f
      rotate = true
      shootCone = 5f
      inaccuracy = 15f
      rotateSpeed = 4f
      shootSound = ISounds.激射
      bullet = BasicBulletType().apply {
        sprite = "circle-bullet"
        damage = 237f
        lifetime = 60f
        speed = 12f
        drag = 0.05f
        width = 12f
        height = 12f
        shrinkY = 0f
        weaveMag = 2f
        weaveScale = 6f
        trailLength = 8
        trailWidth = 4f
        trailChance = 0.2f
        trailColor = Color.valueOf("F3E979")
        frontColor = Color.valueOf("F3E979")
        backColor = Color.valueOf("FEEBB3")
        homingDelay = 10f
        homingRange = 80f
        homingPower = 0.04f
        status = StatusEffects.shocked
        lightning = 3
        lightningDamage = 37f
        lightningColor = Color.valueOf("FEEBB3")
        despawnEffect = Fx.none
        hitEffect = ParticleEffect().apply {
          particles = 5
          lifetime = 20f
          line = true
          length = 35f
          cone = 360f
          lenFrom = 5f
          lenTo = 0f
          colorFrom = Color.valueOf("F3E979")
          colorTo = Color.valueOf("FEEBB3")
        }
      }
    }

    setWeapon("sideGun") {
      x = -21f
      y = 11.25f
      shoot = ShootPattern().apply {
        shots = 4
        shotDelay = 5f
      }
      recoil = 2f
      shootY = 5f
      reload = 70f
      rotate = true
      shootCone = 5f
      inaccuracy = 15f
      rotateSpeed = 4f
      shootSound = ISounds.激射
      bullet = BasicBulletType().apply {
        sprite = "circle-bullet"
        damage = 237f
        lifetime = 60f
        speed = 12f
        drag = 0.05f
        width = 12f
        height = 12f
        shrinkY = 0f
        weaveMag = 2f
        weaveScale = 6f
        trailLength = 8
        trailWidth = 4f
        trailChance = 0.2f
        trailColor = Color.valueOf("F3E979")
        frontColor = Color.valueOf("F3E979")
        backColor = Color.valueOf("FEEBB3")
        homingDelay = 10f
        homingRange = 80f
        homingPower = 0.02f
        status = StatusEffects.shocked
        lightning = 3
        lightningDamage = 13f
        lightningColor = Color.valueOf("FEEBB3")
        despawnEffect = Fx.none
        hitEffect = ParticleEffect().apply {
          particles = 5
          lifetime = 20f
          line = true
          length = 35f
          cone = 360f
          lenFrom = 5f
          lenTo = 0f
          colorFrom = Color.valueOf("F3E979")
          colorTo = Color.valueOf("FEEBB3")
        }
      }
    }

  }
}