package ice.content.unit

import arc.graphics.Color
import arc.math.Interp
import ice.audio.ISounds
import ice.content.IStatus
import ice.entities.bullet.EmpBulletType
import ice.entities.bullet.LaserBulletType
import ice.entities.bullet.base.BasicBulletType
import ice.entities.bullet.base.BulletType
import ice.entities.effect.MultiEffect
import ice.library.util.toColor
import ice.ui.bundle.BaseBundle
import ice.world.content.unit.IceUnitType
import mindustry.content.Fx
import mindustry.entities.abilities.EnergyFieldAbility
import mindustry.entities.abilities.ShieldRegenFieldAbility
import mindustry.entities.bullet.ContinuousFlameBulletType
import mindustry.entities.effect.ParticleEffect
import mindustry.entities.effect.WaveEffect
import mindustry.entities.effect.WrapEffect
import mindustry.entities.part.DrawPart
import mindustry.entities.part.HaloPart
import mindustry.entities.part.RegionPart
import mindustry.entities.part.ShapePart
import mindustry.entities.pattern.ShootAlternate
import mindustry.gen.Sounds
import mindustry.type.weapons.PointDefenseWeapon

class Fearless : IceUnitType("fearless") {
  init {
    BaseBundle.bundle {
      desc(zh_CN, "无畏", "无畏级战列巡航舰,帝国舰队的中坚力量\n配备了两门火力凶猛的荷电粒子炮以及广域脉冲发生器,可以过载大范围内敌军的引擎和武器系统以及敌方工事的能源系统")
    }
    abilities.add(EnergyFieldAbility(0f, 60f, 0f).apply {
      y = -31.75f
      display = false
      maxTargets = 0
      healPercent = 0f
      color = Color.valueOf("FF5845")
    }, ShieldRegenFieldAbility(400f, 12000f, 60f, 240f))
    flying = true
    health = 64000f
    armor = 139f
    hitSize = 92f
    range = 480f
    speed = 1f
    rotateSpeed = 2f
    engineSize = 0f
    engineOffset = 44.25f
    faceTarget = false
    lowAltitude = true
    outlineColor = Color.valueOf("1F1F1F")
    setWeapon("weapon1") {
      x = 20.75f
      y = -3.25f
      reload = 180f
      shoot.apply {
        shots = 4
        shotDelay = 10f
      }
      shootY = 8f
      rotate = true
      rotateSpeed = 1f
      shootCone = 15f
      cooldownTime = 90f
      layerOffset = 0.01f
      minWarmup = 0.99f
      shootWarmupSpeed = 0.06f
      shootSound = ISounds.月隐发射
      bullet = EmpBulletType().apply {
        sprite = "shining"
        damage = 125f
        lifetime = 40f
        speed = 12f
        spin = -4f
        width = 48f
        height = 48f
        shrinkY = 0f
        scaleLife = true
        absorbable = false
        frontColor = Color.valueOf("FF8663")
        backColor = Color.valueOf("FF5845")
        splashDamage = 125f
        splashDamageRadius = 120f
        shootEffect = ParticleEffect().apply {
          line = true
          particles = 12
          lifetime = 20f
          length = 45f
          cone = 30f
          lenFrom = 6f
          lenTo = 6f
          strokeFrom = 3f
          strokeTo = 0f
          lightColor = Color.valueOf("FF5845")
          colorFrom = Color.valueOf("FFDCD8")
          colorTo = Color.valueOf("FF5845")
        }
        hitPowerEffect = ParticleEffect().apply {
          line = true
          particles = 6
          lifetime = 22f
          length = 120f
          cone = -360f
          lenFrom = 6f
          lenTo = 6f
          colorFrom = Color.valueOf("FF8663")
          colorTo = Color.valueOf("FF5845")
        }
        radius = 120f
        timeIncrease = 1.5f
        unitDamageScl = 0.5f
        status = IStatus.电链
        statusDuration = 60f
        homingPower = 0.08f
        homingRange = 240f
        suppressionRange = 120f
        suppressionDuration = 600f
        suppressionEffectChance = 100f
        trailColor = Color.valueOf("FF5845")
        trailLength = 6
        trailWidth = 4f
        trailInterval = 12f
        trailChance = 1f
        trailRotation = true
        trailEffect = ParticleEffect().apply {
          line = true
          particles = 3
          lifetime = 25f
          length = 24f
          baseLength = 0f
          lenFrom = 12f
          lenTo = 0f
          cone = 15f
          offsetX = -15f
          lightColor = Color.valueOf("FF8663")
          colorFrom = Color.valueOf("FF5845")
          colorTo = Color.valueOf("FF8663")
        }
        hitEffect = MultiEffect().apply {
          effects = arrayOf(WrapEffect().apply {
            effect = Fx.dynamicSpikes
            color = Color.valueOf("FF5845")
            rotation = 120f
          }, ParticleEffect().apply {
            particles = 1
            lifetime = 60f
            length = 0f
            sizeFrom = 12f
            sizeTo = 0f
            colorFrom = Color.valueOf("FF5845")
            colorTo = Color.valueOf("FF8663")
          }, ParticleEffect().apply {
            particles = 1
            length = 0f
            lifetime = 15f
            sizeFrom = 120f
            sizeTo = 120f
            interp = Interp.circle
            colorFrom = Color.valueOf("FF5845")
            colorTo = Color.valueOf("FF866300")
          }, WaveEffect().apply {
            lifetime = 60f
            sizeFrom = 120f
            sizeTo = 120f
            strokeFrom = 4f
            colorFrom = Color.valueOf("FF5845")
            colorTo = Color.valueOf("FF8663")
          })
        }
        despawnEffect = Fx.none
        fragBullets = 8
        fragLifeMin = 0.5f
        fragVelocityMin = 0.5f
        fragBullet = BasicBulletType().apply {
          sprite = "star"
          damage = 115f
          lifetime = 40f
          speed = 4f
          spin = 8f
          width = 15f
          height = 15f
          shrinkY = 0f
          impact = true
          knockback = -24f
          frontColor = Color.valueOf("FF8663")
          backColor = Color.valueOf("FF5845")
          weaveMag = 2f
          weaveScale = 7f
          trailColor = Color.valueOf("FF5845")
          trailLength = 9
          trailWidth = 2f
          status = IStatus.熔融
          statusDuration = 60f
          homingPower = 0.04f
          homingRange = 160f
          splashDamage = 85f
          splashDamageRadius = 40f
          hitEffect = ParticleEffect().apply {
            particles = 1
            sizeFrom = 6f
            sizeTo = 0f
            length = 0f
            spin = 3f
            interp = Interp.swing
            lifetime = 100f
            region = "ice-star"
            colorFrom = Color.valueOf("FF5845")
            colorTo = Color.valueOf("FF8663")
          }
          despawnEffect = ParticleEffect().apply {
            particles = 1
            sizeFrom = 6f
            sizeTo = 0f
            length = 0f
            spin = 3f
            interp = Interp.swing
            lifetime = 100f
            region = "ice-star"
            colorFrom = Color.valueOf("FF5845")
            colorTo = Color.valueOf("FF8663")
          }
        }
        parts.addAll(HaloPart().apply {
          tri = true
          radius = 54f
          triLength = 6f
          haloRadius = 20f
          haloRotateSpeed = 4f
          color = Color.valueOf("FF5845")
          layer = 110f
        }, HaloPart().apply {
          tri = true
          radius = 6f
          triLength = 24f
          haloRadius = 23f
          shapeRotation = 150f
          haloRotateSpeed = 4f
          haloRotation = 30f
          color = Color.valueOf("FF5845")
          layer = 110f
        }, HaloPart().apply {
          tri = true
          radius = 6f
          triLength = 36f
          haloRadius = 24f
          shapeRotation = -15f
          haloRotateSpeed = 4f
          haloRotation = -135f
          color = Color.valueOf("FF5845")
          layer = 110f
        })
      }
      parts.add(RegionPart().apply {
        suffix = "-barrel"
        mirror = true
        under = true
        moveY = 7.25f
        moves.add(DrawPart.PartMove().apply {
          progress = DrawPart.PartProgress.recoil
          y = -1f
        })
        children.add(
          RegionPart().apply {
            suffix = "-top"
            mirror = true
            under = true
            x = 0.25f
            moveY = 6f
            layerOffset = -0.0001f
          })
      })
    }
    setWeapon("weapon2") {
      x = 0f
      shoot.apply {
        shots = 3
        shotDelay = 30f
        firstShotDelay = 120f
      }
      shake = 8f
      recoil = 0f
      shootY = 0f
      reload = 1200f
      mirror = false
      shootCone = 360f
      cooldownTime = 1080f
      shootStatus = IStatus.庇护
      shootStatusDuration = 180f
      chargeSound = Sounds.shootLaser
      shootSound = Sounds.shootLaser
      bullet = mindustry.entities.bullet.EmpBulletType().apply {
        damage = 0f
        lifetime = 30f
        speed = 12f
        collides = false
        hittable = false
        absorbable = false
        reflectable = false
        pierceArmor = true
        shootEffect = Fx.none
        instantDisappear = true
        radius = 480f
        timeIncrease = 1f
        unitDamageScl = 1f
        powerDamageScl = 0f
        powerSclDecrease = 0f
        hitColor = Color.valueOf("FF5845")
        status = IStatus.电磁脉冲
        statusDuration = 300f
        splashDamage = 200f
        splashDamageRadius = 480f
        scaledSplashDamage = true
        hitPowerEffect = ParticleEffect().apply {
          line = true
          particles = 6
          lifetime = 22f
          length = 120f
          cone = 360f
          lenFrom = 6f
          lenTo = 6f
          colorFrom = Color.valueOf("FF8663")
          colorTo = Color.valueOf("FF5845")
        }
        hitEffect = MultiEffect().apply {
          lifetime = 120f
          effects = arrayOf(ParticleEffect().apply {
            particles = 12
            length = 40f
            lifetime = 36f
            cone = 360f
            sizeFrom = 5f
            sizeTo = 0f
            interp = Interp.circleOut
            colorFrom = Color.valueOf("FF8663")
            colorTo = Color.valueOf("FF5845")
          }, ParticleEffect().apply {
            particles = 6
            lifetime = 9f
            sizeFrom = 3f
            sizeTo = 0f
            length = 60f
            baseLength = 8f
            cone = 20f
            colorFrom = Color.valueOf("FF8663")
            colorTo = Color.valueOf("FF5845")
          }, WaveEffect().apply {
            lifetime = 10f
            sizeFrom = 8f
            sizeTo = 50f
            strokeFrom = 2f
            colorFrom = Color.valueOf("FF8663")
            colorTo = Color.valueOf("FF5845")
          })
        }
        despawnEffect = MultiEffect().apply {
          lifetime = 120f
          effects = arrayOf(ParticleEffect().apply {
            particles = 1
            length = 0f
            lifetime = 15f
            sizeFrom = 480f
            sizeTo = 480f
            colorFrom = Color.valueOf("FF584580")
            colorTo = Color.valueOf("FF866300")
          }, WaveEffect().apply {
            lifetime = 60f
            sizeFrom = 480f
            sizeTo = 480f
            strokeFrom = 4f
            colorFrom = Color.valueOf("FF8663")
            colorTo = Color.valueOf("FF5845")
          })
        }
      }
      parts.addAll(RegionPart().apply {
        suffix = "-arrow"
        mirror = true
        outline = false
        progress = DrawPart.PartProgress.smoothReload.absin(20f, 1f)
        x = -68f
        y = 68f
        moveX = 11f
        moveY = -11f
        rotation = -135f
        layer = 110f
        color = "FFD37F".toColor()
        colorTo = "F15454".toColor()
      }, RegionPart().apply {
        suffix = "-arrow"
        mirror = true
        outline = false
        progress = DrawPart.PartProgress.smoothReload.absin(20f, 1f)
        x = 68f
        y = -68f
        moveX = -11f
        moveY = 11f
        rotation = 45f
        layer = 110f
        color = "FFD37F".toColor()
        colorTo = "F15454".toColor()
      }, ShapePart().apply {
        progress = DrawPart.PartProgress.smoothReload
        circle = true
        hollow = true
        radius = 72f
        stroke = 3f
        layer = 110f
        color = "FFD37F".toColor()
        colorTo = "F15454".toColor()
      })
    }
    setWeapon("weapon3") {
      x = 15.5f
      y = 56f
      reload = 90f
      shoot.apply {
        shots = 3
        shotDelay = 15f
      }
      recoil = 2f
      rotate = true
      rotateSpeed = 3f
      shootCone = 15f
      cooldownTime = 60f
      shootSound = Sounds.shootLaser
      bullet = LaserBulletType(55f).apply {
        lifetime = 15f
        length = 320f
        width = 24f
        colors = arrayOf(
          Color.valueOf("DE4136"), Color.valueOf("FF5845"), Color.valueOf("FF8663")
        )
        status = IStatus.熔融
        statusDuration = 60f
      }
    }.copyAdd {
      x = 21.75f
      y = 40f
    }
    setWeapon("weapon4") {
      x = -15f
      y = -33.5f
      recoil = 3f
      shake = 3f
      shootY = 9f
      reload = 60f
      shootCone = 10f
      shoot = ShootAlternate().apply {
        shots = 3
        spread = 8f
        shotDelay = 5f
      }
      rotate = true
      rotateSpeed = 3f
      cooldownTime = 85f
      shootSound = Sounds.shootScathe
      ejectEffect = Fx.casing4
      bullet = BasicBulletType().apply {
        damage = 100f
        lifetime = 19f
        speed = 20f
        shrinkY = 0f
        width = 16f
        height = 16f
        weaveMag = 1f
        weaveScale = 4f
        trailLength = 12
        trailWidth = 3.2f
        status = IStatus.湍能
        statusDuration = 60f
        trailColor = Color.valueOf("F9C27B")
        hitColor = Color.valueOf("F9C27B")
        absorbable = false
        reflectable = false
        splashDamage = 610f
        splashDamageRadius = 40f
        shootEffect = ParticleEffect().apply {
          particles = 3
          lifetime = 25f
          sizeFrom = 3f
          sizeTo = 0f
          cone = 15f
          length = 33f
          colorFrom = Color.valueOf("F9C27B")
          colorTo = Color.valueOf("F9C27A")
        }
        smokeEffect = Fx.shootSmokeSquare
        trailChance = 0.25f
        trailEffect = ParticleEffect().apply {
          particles = 1
          lifetime = 25f
          sizeFrom = 3f
          sizeTo = 0f
          cone = 360f
          length = 23f
          sizeInterp = Interp.pow10In
          colorFrom = Color.valueOf("F9C27B")
          colorTo = Color.valueOf("F9C27A")
        }
        hitShake = 8f
        hitSound = Sounds.plantBreak
        despawnEffect = Fx.none
        hitEffect = MultiEffect().apply {
          effects = arrayOf(ParticleEffect().apply {
            region = "blank"
            particles = 3
            lifetime = 33f
            sizeFrom = 5f
            sizeTo = 0f
            cone = 360f
            offset = 45f
            length = 55f
            baseLength = 33f
            interp = Interp.pow5Out
            sizeInterp = Interp.pow10In
            colorFrom = Color.valueOf("F9C27B")
            colorTo = Color.valueOf("F9C27A")
          }, ParticleEffect().apply {
            lifetime = 33f
            particles = 9
            line = true
            strokeFrom = 2f
            lenFrom = 11f
            lenTo = 0f
            cone = 360f
            length = 80f
            colorFrom = Color.valueOf("F9C27B")
            colorTo = Color.valueOf("F9C27A")
          }, WaveEffect().apply {
            lifetime = 15f
            sizeTo = 80f
            strokeFrom = 4f
            colorFrom = Color.valueOf("F9C27A")
            colorTo = Color.valueOf("F9C27B")
          })
        }
      }
    }

    weapons.add(PointDefenseWeapon().apply {

      x = 0f
      y = -31.75f
      recoil = 0f
      reload = 6f
      color = Color.valueOf("FF5845")
      targetInterval = 1f
      targetSwitchInterval = 1f
      shootSound = Sounds.shootLaser
      bullet = BulletType().apply {
        damage = 125f
        maxRange = 320f
        shootEffect = Fx.sparkShoot
        hitEffect = Fx.pointHit
      }
    })
    setWeapon {
      x = 0f
      y = -44.25f
      shootY = 0f
      reload = 300f
      mirror = false
      useAmmo = false
      baseRotation = 180f
      shootSound = Sounds.none
      alwaysShooting = true
      alwaysContinuous = true
      bullet = ContinuousFlameBulletType().apply {
        colors = arrayOf(
          Color.valueOf("FF58458C"), Color.valueOf("FF5845B2"), Color.valueOf("FF5845CC"), Color.valueOf("FF8663"), Color.valueOf("FF8663CC")
        )
        damage = 20f
        lifetime = 30f
        length = 45f
        width = 3f
        drawFlare = false
        status = IStatus.熔融
        statusDuration = 150f
        hitEffect = ParticleEffect().apply {
          line = true
          particles = 7
          lifetime = 15f
          length = 65f
          cone = -360f
          strokeFrom = 2.5f
          strokeTo = 0f
          lenFrom = 8f
          lenTo = 0f
          colorFrom = Color.valueOf("FF5845")
          colorTo = Color.valueOf("FF8663")
        }
      }
    }
    fallSpeed = 0.0033333334f
    fallEffect = ParticleEffect().apply {
      line = true
      particles = 2
      length = 0.01f
      lifetime = 30f
      colorTo = Color.valueOf("FF584550")
      strokeFrom = 2f
      strokeTo = 0f
      lenFrom = 14f
      lenTo = 14f
    }
    fallEngineEffect = MultiEffect().apply {
      effects = arrayOf(ParticleEffect().apply {
        particles = 5
        length = 6f
        baseLength = 6f
        lifetime = 50f
        interp = Interp.pow3Out
        sizeInterp = Interp.pow5In
        cone = -30f
        offset = 30f
        colorFrom = Color.valueOf("787878")
        colorTo = Color.valueOf("787878")
        sizeFrom = 5f
        sizeTo = 0f
      }, ParticleEffect().apply {
        particles = 3
        length = 10f
        baseLength = 10f
        lifetime = 45f
        interp = Interp.pow3Out
        sizeInterp = Interp.pow5In
        cone = -30f
        offset = 30f
        colorFrom = Color.valueOf("FF8663")
        colorTo = Color.valueOf("FF5845AA")
        sizeFrom = 3.5f
        sizeTo = 0f
      })
    }
  }
}