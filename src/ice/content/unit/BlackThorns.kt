package ice.content.unit

import arc.graphics.Color
import arc.math.Interp
import ice.audio.ISounds
import ice.content.IStatus
import ice.content.IUnitTypes
import ice.entities.bullet.LaserBulletType
import ice.entities.bullet.base.BasicBulletType
import ice.entities.bullet.base.BulletType
import ice.entities.effect.MultiEffect
import ice.library.IFiles.appendModName
import ice.ui.bundle.BaseBundle
import ice.world.content.unit.IceUnitType
import mindustry.content.Fx
import mindustry.content.StatusEffects
import mindustry.entities.effect.ParticleEffect
import mindustry.entities.effect.WaveEffect
import mindustry.entities.part.DrawPart.PartProgress
import mindustry.entities.part.HaloPart
import mindustry.entities.part.ShapePart
import mindustry.entities.pattern.ShootBarrel
import mindustry.entities.pattern.ShootPattern
import mindustry.gen.Sounds

class BlackThorns : IceUnitType("unit_blackThorns") {
  init {
    BaseBundle.bundle {
      desc(zh_CN, "黑棘", "可以向敌人发射火苗导弹和远距离穿透激光,对于近距离的敌人则快速发射穿透激光")
    }

    lowAltitude = true
    flying = true
    health = 174000f
    armor = 69f
    hitSize = 86f
    speed = 0.667f
    range = 864f
    rotateSpeed = 1.5f
    engineOffset = 62f
    engineSize = 10f
    outlineColor = Color.valueOf("1F1F1F")
    fallSpeed = 0.0033333334f

    engines.add(UnitEngine().apply {
      x = 22.5f
      y = -43.75f
      radius = 8f
      rotation = -90f
    }, UnitEngine().apply {
      x = -22.5f
      y = -43.75f
      radius = 8f
      rotation = -90f
    })

    parts.addAll(HaloPart().apply {
      progress = PartProgress.smoothReload
      tri = true
      y = 5.75f
      shapes = 2
      triLength = 24f
      triLengthTo = 0f
      haloRadius = 21f
      haloRotation = 90f
      color = Color.valueOf("FF5845")
      colorTo = Color.valueOf("FF8663")
      layer = 110f
    }, HaloPart().apply {
      progress = PartProgress.smoothReload
      tri = true
      y = 5.75f
      shapeRotation = 180f
      shapes = 2
      triLength = 6f
      triLengthTo = 0f
      haloRadius = 21f
      haloRotation = 90f
      color = Color.valueOf("FF5845")
      colorTo = Color.valueOf("FF8663")
      layer = 110f
    }, HaloPart().apply {
      progress = PartProgress.smoothReload
      mirror = true
      tri = true
      y = 5.75f
      shapes = 2
      triLength = 18f
      triLengthTo = 0f
      haloRadius = 21f
      haloRotation = 60f
      color = Color.valueOf("FF5845")
      colorTo = Color.valueOf("FF8663")
      layer = 110f
    }, HaloPart().apply {
      progress = PartProgress.smoothReload
      mirror = true
      tri = true
      y = 5.75f
      shapeRotation = 180f
      shapes = 2
      triLength = 4.5f
      triLengthTo = 0f
      haloRadius = 21f
      haloRotation = 60f
      color = Color.valueOf("FF5845")
      colorTo = Color.valueOf("FF8663")
      layer = 110f
    }, ShapePart().apply {
      progress = PartProgress.smoothReload
      circle = true
      y = 5.75f
      radius = 4f
      radiusTo = 0f
      color = Color.valueOf("FF5845")
      colorTo = Color.valueOf("FF8663")
      layer = 110f
    }, HaloPart().apply {
      progress = PartProgress.smoothReload
      tri = true
      y = 5.75f
      shapes = 4
      triLength = 18f
      triLengthTo = 0f
      haloRadius = 12f
      haloRotation = -12.6f
      haloRotateSpeed = -0.5f
      color = Color.valueOf("FF5845")
      colorTo = Color.valueOf("FF8663")
      layer = 110f
    }, HaloPart().apply {
      progress = PartProgress.smoothReload
      tri = true
      y = 5.75f
      shapeRotation = 180f
      shapes = 4
      triLength = 4.5f
      triLengthTo = 0f
      haloRadius = 12f
      haloRotation = -12.6f
      haloRotateSpeed = -0.5f
      color = Color.valueOf("FF5845")
      colorTo = Color.valueOf("FF8663")
      layer = 110f
    })

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

    fallEngineEffect = MultiEffect(ParticleEffect().apply {
      particles = 5
      length = 6f
      baseLength = 6f
      lifetime = 50f
      interp = Interp.pow3Out
      sizeInterp = Interp.pow5In
      cone = 30f
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
      cone = 30f
      offset = 30f
      colorFrom = Color.valueOf("FF8663")
      colorTo = Color.valueOf("FF5845AA")
      sizeFrom = 3.5f
      sizeTo = 0f
    })

    deathExplosionEffect = MultiEffect(ParticleEffect().apply {
      particles = 200
      length = 220f
      baseLength = 40f
      lifetime = 500f
      sizeFrom = 24f
      sizeTo = 0f
      colorFrom = Color.valueOf("FF5845")
      colorTo = Color.valueOf("FF584575")
    }, ParticleEffect().apply {
      lightOpacity = 0f
      particles = 100
      length = 200f
      lifetime = 500f
      sizeFrom = 32f
      sizeTo = 0f
      colorFrom = Color.valueOf("787878")
      colorTo = Color.valueOf("78787875")
      sizeInterp = Interp.pow5In
    }, ParticleEffect().apply {
      particles = 40
      line = true
      lifetime = 180f
      length = 450f
      lenFrom = 20f
      lenTo = 0f
      strokeFrom = 12f
      strokeTo = 0f
      colorTo = Color.valueOf("FF8663")
    }, WaveEffect().apply {
      lifetime = 90f
      sizeTo = 260f
      strokeFrom = 25f
      colorTo = Color.valueOf("FF584550")
    })

    setWeapon("main") {
      x = 0f
      shoot = ShootPattern().apply {
        firstShotDelay = 120f
      }
      recoil = 0f
      shake = 4f
      shootY = 5.75f
      reload = 1080f
      mirror = false
      shootCone = 0.05f
      cooldownTime = 1440f
      shootStatus = StatusEffects.overdrive
      shootStatusDuration = 121f
      chargeSound = Sounds.chargeLancer
      shootSound = ISounds.灼烧
      bullet = LaserBulletType(8400f).apply {
        lifetime = 30f
        length = 600f
        width = 75f
        largeHit = true
        laserAbsorb = false
        status = StatusEffects.melting
        statusDuration = 1800f
        lightColor = Color.valueOf("FF5845")
        chargeEffect = MultiEffect(ParticleEffect().apply {
          region = "star".appendModName()
          lifetime = 120f
          particles = 1
          sizeFrom = 2f
          sizeTo = 38f
          length = 0f
          spin = 11f
          colorFrom = Color.valueOf("FF5845")
          colorTo = Color.valueOf("FF5845")
        }, WaveEffect().apply {
          lifetime = 120f
          sizeFrom = 90f
          sizeTo = 0f
          strokeFrom = 0f
          strokeTo = 8f
          interp = Interp.pow5In
          lightColor = Color.valueOf("FF8663")
          colorFrom = Color.valueOf("FF8663")
          colorTo = Color.valueOf("FF5845")
        }, WaveEffect().apply {
          lifetime = 120f
          sizeFrom = 70f
          sizeTo = 0f
          strokeFrom = 0f
          strokeTo = 8f
          interp = Interp.pow5In
          lightColor = Color.valueOf("FF8663")
          colorFrom = Color.valueOf("FF8663")
          colorTo = Color.valueOf("FF5845")
        })
        shootEffect = MultiEffect(ParticleEffect().apply {
          particles = 15
          lifetime = 30f
          line = true
          lenFrom = 10f
          lenTo = 10f
          cone = 15f
          length = 100f
          baseLength = -15f
          colorFrom = Color.valueOf("FF5845")
          colorTo = Color.valueOf("FF8663")
        }, WaveEffect().apply {
          lifetime = 25f
          sizeTo = 75f
          strokeFrom = 4f
          lightColor = Color.valueOf("FF5845")
          colorFrom = Color.valueOf("FF5845")
          colorTo = Color.valueOf("FF8663")
        }, ParticleEffect().apply {
          region = "star".appendModName()
          lifetime = 25f
          particles = 1
          sizeFrom = 35f
          sizeTo = 0f
          length = 0f
          interp = Interp.swingIn
          lightColor = Color.valueOf("FF5845")
          colorFrom = Color.valueOf("FF5845")
          colorTo = Color.valueOf("FF8663")
        }, ParticleEffect().apply {
          region = "star".appendModName()
          lifetime = 25f
          particles = 1
          sizeFrom = 12f
          sizeTo = 0f
          length = 0f
          interp = Interp.swingIn
          lightColor = Color.valueOf("FF5845")
          colorFrom = Color.valueOf("FF5845")
          colorTo = Color.valueOf("FF8663")
        })
        colors = arrayOf(
          Color.valueOf("DE4136"), Color.valueOf("FF5845"), Color.valueOf("FF8663")
        )
        hitEffect = ParticleEffect().apply {
          particles = 7
          lifetime = 30f
          line = true
          lenFrom = 9f
          lenTo = 0f
          cone = 360f
          length = 65f
          baseLength = -15f
          colorFrom = Color.valueOf("FF8663")
          colorTo = Color.valueOf("FF5845")
        }
      }
    }

    setWeapon {
      x = 25f
      y = 12f
      reload = 360f
      shoot = ShootBarrel().apply {
        shots = 8
        shotDelay = 5f
        barrels = floatArrayOf(0f, 0f, -30f, 0f, 0f, -37.5f, 0f, 0f, -22.5f, 0f, 0f, -45f)
      }


      shootY = 0f
      alternate = false
      shootSound = Sounds.shootMissile
      bullet = BulletType().apply {
        spawnUnit = IUnitTypes.火花
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
      x = 24.75f
      y = -19f
      shoot = ShootBarrel().apply {
        shots = 16
        shotDelay = 5f
        barrels = floatArrayOf(0f, 0f, -30f, 0f, 0f, -37.5f, 0f, 0f, -22.5f, 0f, 0f, -45f)
      }
      shake = 2f
      shootY = 0f
      reload = 360f
      shootSound = ISounds.激射
      bullet = BasicBulletType(5f, 425f, "arrows").apply {
        lifetime = 160f
        drag = -0.001f
        shrinkY = 0f
        height = 18f
        width = 8f
        shootEffect = ParticleEffect().apply {
          lifetime = 25f
          particles = 10
          length = 40f
          cone = 20f
          sizeFrom = 4f
          sizeTo = 0f
          interp = Interp.circleOut
          colorFrom = Color.valueOf("FF8663")
          colorTo = Color.valueOf("FF5845")
        }
        reflectable = false
        homingDelay = 30f
        homingRange = 320f
        homingPower = 0.04f
        splashDamage = 275f
        splashDamageRadius = 80f
        suppressionRange = 60f
        suppressionDuration = 600f
        suppressionEffectChance = 1f
        frontColor = Color.valueOf("FF8663")
        backColor = Color.valueOf("FF5845")
        weaveMag = 1f
        weaveScale = 5f
        trailChance = 1f
        trailWidth = 2f
        trailLength = 24
        trailColor = Color.valueOf("D86E56")
        trailRotation = true
        trailEffect = ParticleEffect().apply {
          lifetime = 25f
          particles = 6
          length = 6f
          cone = 360f
          offsetX = -5f
          sizeFrom = 3f
          sizeTo = 0f
          interp = Interp.circleOut
          colorFrom = Color.valueOf("FF8663")
          colorTo = Color.valueOf("FF5845")
        }
        hitShake = 2f
        impact = true
        knockback = 48f
        status = StatusEffects.melting
        statusDuration = 120f
        hitSound = Sounds.explosion
        hitEffect = ParticleEffect().apply {
          particles = 15
          length = 40f
          lifetime = 36f
          cone = 360f
          sizeFrom = 5f
          sizeTo = 0f
          interp = Interp.circleOut
          colorFrom = Color.valueOf("FF8663")
          colorTo = Color.valueOf("FF5845")
        }
        despawnEffect = MultiEffect(ParticleEffect().apply {
          particles = 6
          sizeFrom = 3f
          sizeTo = 0f
          length = 60f
          baseLength = 8f
          lifetime = 9f
          colorFrom = Color.valueOf("FF8663")
          colorTo = Color.valueOf("FF5845")
          cone = 20f
        }, WaveEffect().apply {
          lifetime = 10f
          sizeFrom = 8f
          sizeTo = 50f
          strokeFrom = 2f
          colorFrom = Color.valueOf("FF8663")
          colorTo = Color.valueOf("FF5845")
        })
        fragBullets = 8
        fragLifeMin = 0.5f
        fragVelocityMin = 0.5f
        fragBullet = BasicBulletType(4f, 115f, "star").apply {
          lifetime = 40f
          spin = 8f
          shrinkX = 0f
          shrinkY = 0f
          height = 15f
          width = 15f
          impact = true
          knockback = -24f
          frontColor = Color.valueOf("FF8663")
          backColor = Color.valueOf("FF5845")
          weaveMag = 2f
          weaveScale = 7f
          trailColor = Color.valueOf("FF5845")
          trailLength = 9
          trailWidth = 2f
          trailEffect = Fx.none
          status = StatusEffects.melting
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
            region = "star".appendModName()
            colorFrom = Color.valueOf("FF5845")
            colorTo = Color.valueOf("FF5845")
          }
          despawnEffect = ParticleEffect().apply {
            particles = 1
            sizeFrom = 6f
            sizeTo = 0f
            length = 0f
            spin = 3f
            interp = Interp.swing
            lifetime = 100f
            region = "star".appendModName()
            colorFrom = Color.valueOf("FF5845")
            colorTo = Color.valueOf("FF5845")
          }
        }
      }
    }

    setWeapon("closeDefense") {
      x = 12.25f
      y = 47.25f
      reload = 60f
      recoil = 2f
      rotate = true
      rotateSpeed = 4f
      shootCone = 15f
      cooldownTime = 60f
      shootSound = Sounds.shootLaser
      bullet = LaserBulletType(225f).apply {
        lifetime = 15f
        length = 320f
        width = 24f
        colors = arrayOf(
          Color.valueOf("DE4136"), Color.valueOf("FF5845"), Color.valueOf("FF8663")
        )
        status = IStatus.熔融
        statusDuration = 60f
        hitEffect = Fx.hitLancer
      }
    }
    setWeapon("closeDefense") {
      x = -14f
      y = -42.75f
      reload = 60f
      recoil = 2f
      rotate = true
      rotateSpeed = 4f
      shootCone = 15f
      cooldownTime = 60f
      shootSound = Sounds.shootLaser
      bullet = LaserBulletType(225f).apply {
        lifetime = 15f
        length = 320f
        width = 24f
        colors = arrayOf(
          Color.valueOf("DE4136"), Color.valueOf("FF5845"), Color.valueOf("FF8663")
        )
        status = IStatus.熔融
        statusDuration = 60f
        hitEffect = Fx.hitLancer
      }
    }
  }
}