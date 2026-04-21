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
import ice.world.content.unit.IceUnitType
import ice.world.content.unit.ability.HealthRequireAbility
import mindustry.content.Fx
import mindustry.content.StatusEffects
import mindustry.entities.effect.ParticleEffect
import mindustry.entities.effect.WaveEffect
import mindustry.entities.part.DrawPart
import mindustry.entities.part.DrawPart.PartProgress
import mindustry.entities.part.HaloPart
import mindustry.entities.part.RegionPart
import mindustry.entities.part.ShapePart
import mindustry.entities.pattern.ShootBarrel
import mindustry.gen.Sounds
import mindustry.graphics.Layer

class StarEater :IceUnitType("unit_starEater") {
  init {
    localization {
      zh_CN {
        this.localizedName = "噬星"
        description = "由黑棘二次蛹化蜕变而成的生物战舰\n可以向敌人发射离散电浆炮和远距离穿透激光,且可以发射火花导弹摧毁敌军工事,对于近距离的敌人则快速发射穿透激光"
        details = "[#D75B6E]她多美啊!"
      }
    }
    lowAltitude = true
    flying = true
    health = 261000f
    armor = 103f
    hitSize = 96f
    speed = 0.6f
    rotateSpeed = 1f
    engineOffset = 57.5f
    engineSize = 10f
    engines.addAll(
      UnitEngine(19f, -53f, 8f, -90f), UnitEngine(-19f, -53f, 8f, -90f)
    )
    abilities.add(HealthRequireAbility(0.4f, StatusEffects.none, IStatus.迅疗))
    outlineColor = Color.valueOf("1F1F1F")
    fallSpeed = 0.0016666667f
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
      sizeFrom = 0f
      sizeTo = 260f
      strokeFrom = 25f
      strokeTo = 0f
      colorTo = Color.valueOf("FF584550")
    }).apply {
      this.lifetime = 500f
    }

    parts.addAll(
      HaloPart().apply {
        progress = PartProgress.smoothReload
        tri = true
        y = 10.75f
        shapes = 2
        radius = 3.6f
        triLength = 28.8f
        triLengthTo = 0f
        haloRadius = 25.2f
        haloRotation = 90f
        color = Color.valueOf("FF5845")
        colorTo = Color.valueOf("FF8663")
        layer= Layer.effect
      },
      HaloPart().apply {
        progress = PartProgress.smoothReload
        tri = true
        y = 10.75f
        shapeRotation = 180f
        shapes = 2
        radius = 3.6f
        triLength = 7.2f
        triLengthTo = 0f
        haloRadius = 25.2f
        haloRotation = 90f
        color = Color.valueOf("FF5845")
        colorTo = Color.valueOf("FF8663")
        layer= Layer.effect
      },
      HaloPart().apply {
        progress = PartProgress.smoothReload
        mirror = true
        tri = true
        y = 10.75f
        shapes = 2
        radius = 3.6f
        triLength = 21.6f
        triLengthTo = 0f
        haloRadius = 25.2f
        haloRotation = 60f
        color = Color.valueOf("FF5845")
        colorTo = Color.valueOf("FF8663")
        layer= Layer.effect
      },
      HaloPart().apply {
        progress = PartProgress.smoothReload
        mirror = true
        tri = true
        y = 10.75f
        shapeRotation = 180f
        shapes = 2
        radius = 3.6f
        triLength = 5.4f
        triLengthTo = 0f
        haloRadius = 25.2f
        haloRotation = 60f
        color = Color.valueOf("FF5845")
        colorTo = Color.valueOf("FF8663")
        layer= Layer.effect
      },
      ShapePart().apply {
        progress = PartProgress.smoothReload
        circle = true
        y = 10.75f
        radius = 4f
        radiusTo = 0f
        color = Color.valueOf("FF5845")
        layer= Layer.effect
      },
      HaloPart().apply {
        progress = PartProgress.smoothReload
        tri = true
        y = 10.75f
        shapes = 3
        radius = 4.5f
        triLength = 21.6f
        triLengthTo = 0f
        haloRadius = 12f
        haloRotation = -24f
        haloRotateSpeed = -0.5f
        color = Color.valueOf("FF5845")
        colorTo = Color.valueOf("FF8663")
        layer= Layer.effect
      },
      HaloPart().apply {
        progress = PartProgress.smoothReload
        tri = true
        y = 10.75f
        shapeRotation = 180f
        shapes = 3
        radius = 4.5f
        triLength = 5.4f
        triLengthTo = 0f
        haloRadius = 12f
        haloRotation = -24f
        haloRotateSpeed = -0.5f
        color = Color.valueOf("FF5845")
        colorTo = Color.valueOf("FF8663")
        layer= Layer.effect
      }
    )

    setWeapon("closeDefense") {
      x = 13.5f
      y = -38f
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
          Color.valueOf("DE4136"),
          Color.valueOf("FF5845"),
          Color.valueOf("FF8663")
        )
        status = IStatus.湍能
        statusDuration = 60f
        hitEffect = Fx.hitLancer
        despawnEffect = Fx.none
      }
    }
    setWeapon("closeDefense") {
      x = -21.75f
      y = 29f
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
          Color.valueOf("DE4136"),
          Color.valueOf("FF5845"),
          Color.valueOf("FF8663")
        )
        status = IStatus.湍能
        statusDuration = 60f
        hitEffect = Fx.hitLancer
        despawnEffect = Fx.none
      }
    }
    setWeapon("closeDefense") {
      x = 12.25f
      y = 50.25f
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
          Color.valueOf("DE4136"),
          Color.valueOf("FF5845"),
          Color.valueOf("FF8663")
        )
        status = IStatus.湍能
        statusDuration = 60f
        hitEffect = Fx.hitLancer
        despawnEffect = Fx.none
      }
    }
    setWeapon {
      x = 16f
      y = 55f
      reload = 600f
      shootY = 0f
      alternate = false
      shootSound = Sounds.shootMissile
      shoot .apply {
        shots = 4
        shotDelay = 5f
      }

      bullet = BulletType().apply {
        spawnUnit = IUnitTypes.飞蠓
        shootEffect = ParticleEffect().apply {
          followParent = false
          lifetime = 45f
          particles = 5
          sizeFrom = 3f
          sizeTo = 0f
          cone = 30f
          length = 33f
          interp = Interp.pow10Out
          sizeInterp = Interp.pow10In
          colorFrom = Color.valueOf("727272")
          colorTo = Color.valueOf("727272")
        }
      }
    }

    setWeapon {
      x = 0f
      recoil = 0f
      shake = 4f
      shootY = 5.75f
      reload = 1080f
      mirror = false
      shootCone = 0.05f
      cooldownTime = 1440f
      shootStatus = IStatus.过热
      shootStatusDuration = 121f
      chargeSound = Sounds.chargeLancer
      shootSound = ISounds.灼烧
      shoot.apply {
        firstShotDelay = 120f
      }

      bullet = LaserBulletType( 8400f).apply {
        lifetime = 30f
        length = 600f
        width = 75f
        largeHit = true
        laserAbsorb = false
        status = IStatus.熔融
        statusDuration = 1800f
        lightColor = Color.valueOf("FF5845")
        colors = arrayOf(
          Color.valueOf("DE4136"),
          Color.valueOf("FF5845"),
          Color.valueOf("FF8663")
        )

        chargeEffect = MultiEffect(
          ParticleEffect().apply {
            region = "star".appendModName()
            lifetime = 120f
            particles = 1
            sizeFrom = 2f
            sizeTo = 38f
            length = 0f
            spin = 11f
            colorFrom = Color.valueOf("FF5845")
            colorTo = Color.valueOf("FF5845")
          },
          WaveEffect().apply {
            lifetime = 120f
            sizeFrom = 90f
            sizeTo = 0f
            strokeFrom = 0f
            strokeTo = 8f
            interp = Interp.pow5In
            lightColor = Color.valueOf("FF8663")
            colorFrom = Color.valueOf("FF8663")
            colorTo = Color.valueOf("FF5845")
          },
          WaveEffect().apply {
            lifetime = 120f
            sizeFrom = 70f
            sizeTo = 0f
            strokeFrom = 0f
            strokeTo = 8f
            interp = Interp.pow5In
            lightColor = Color.valueOf("FF8663")
            colorFrom = Color.valueOf("FF8663")
            colorTo = Color.valueOf("FF5845")
          }
        )

        shootEffect = MultiEffect(
          ParticleEffect().apply {
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
          },
          WaveEffect().apply {
            lifetime = 25f
            sizeTo = 75f
            strokeFrom = 4f
            lightColor = Color.valueOf("FF5845")
            colorFrom = Color.valueOf("FF5845")
            colorTo = Color.valueOf("FF8663")
          },
          ParticleEffect().apply {
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
          },
          ParticleEffect().apply {
            region ="star".appendModName()
            particles = 1
            lifetime = 25f
            sizeFrom = 12f
            sizeTo = 0f
            length = 0f
            interp = Interp.swingIn
            lightColor = Color.valueOf("FF5845")
            colorFrom = Color.valueOf("FF5845")
            colorTo = Color.valueOf("FF8663")
          }
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

    setWeapon("weapon1") {
      x = 24.25f
      y = -6.5f
      shootY = 16f
      recoil = 5f
      rotate = true
      rotateSpeed = 0.8f
      shake = 8f
      reload = 960f
      shootCone = 5f
      cooldownTime = 840f
      layerOffset = 0.01f
      minWarmup = 0.99f
      parentizeEffects = true
      shootWarmupSpeed = 0.05f
      smoothReloadSpeed = 0.15f
      chargeSound = ISounds.月隐蓄力
      shootSound = ISounds.月隐发射
      shoot.apply {
        firstShotDelay = 120f
      }

      bullet = BasicBulletType(6f,880f,"arrows").apply {
        lifetime = 160f
        height = 36f
        width = 16f
        shrinkY = 0f
        hittable = false
        reflectable = false
        keepVelocity = false
        trailChance = 1f
        trailLength = 24
        trailWidth = 6f
        trailRotation = true
        trailColor = Color.valueOf("FF9C5A")
        homingDelay = 30f
        homingRange = 240f
        homingPower = 0.02f
        lightColor = Color.valueOf("FF9C5A")
        status = IStatus.蚀骨
        statusDuration = 300f
        splashDamage = 725f
        splashDamageRadius = 120f
        splashDamagePierce = true
        hitSound = Sounds.explosionPlasmaSmall
        despawnEffect = Fx.none
        despawnUnit = IUnitTypes.雷精

        chargeEffect = MultiEffect(
          ParticleEffect().apply {
            particles = 45
            offset = 100f
            sizeFrom = 0f
            sizeTo = 8f
            length = 200f
            baseLength = -200f
            interp = Interp.pow3In
            sizeInterp = Interp.pow5Out
            lifetime = 70f
            lightColor = Color.valueOf("FF9C5A")
            colorFrom = Color.valueOf("FF9C5A00")
            colorTo = Color.valueOf("FF9C5A")
          },
          ParticleEffect().apply {
            particles = 45
            offset = 100f
            sizeFrom = 0f
            sizeTo = 7f
            length = 250f
            baseLength = -250f
            interp = Interp.pow3In
            sizeInterp = Interp.pow5Out
            lifetime = 80f
            lightColor = Color.valueOf("FF9C5A")
            colorFrom = Color.valueOf("FF9C5A00")
            colorTo = Color.valueOf("FF9C5A")
          },
          ParticleEffect().apply {
            particles = 45
            offset = 100f
            sizeFrom = 0f
            sizeTo = 6f
            length = 300f
            baseLength = -300f
            interp = Interp.pow3In
            sizeInterp = Interp.pow5Out
            lifetime = 90f
            lightColor = Color.valueOf("FF9C5A")
            colorFrom = Color.valueOf("FF9C5A00")
            colorTo = Color.valueOf("FF9C5A")
          },
          ParticleEffect().apply {
            particles = 45
            offset = 100f
            sizeFrom = 0f
            sizeTo = 5f
            length = 350f
            baseLength = -350f
            interp = Interp.pow3In
            sizeInterp = Interp.pow5Out
            lifetime = 100f
            lightColor = Color.valueOf("FF9C5A")
            colorFrom = Color.valueOf("FF9C5A00")
            colorTo = Color.valueOf("FF9C5A")
          },
          ParticleEffect().apply {
            particles = 45
            offset = 100f
            sizeFrom = 0f
            sizeTo = 4f
            length = 400f
            baseLength = -400f
            interp = Interp.pow3In
            sizeInterp = Interp.pow5Out
            lifetime = 110f
            lightColor = Color.valueOf("FF9C5A")
            colorFrom = Color.valueOf("FF9C5A00")
            colorTo = Color.valueOf("FF9C5A")
          },
          ParticleEffect().apply {
            particles = 45
            offset = 100f
            sizeFrom = 0f
            sizeTo = 3f
            length = 450f
            baseLength = -450f
            interp = Interp.pow3In
            sizeInterp = Interp.pow5Out
            lifetime = 120f
            lightColor = Color.valueOf("FF9C5A")
            colorFrom = Color.valueOf("FF9C5A00")
            colorTo = Color.valueOf("FF9C5A")
          },
          WaveEffect().apply {
            lifetime = 120f
            sizeFrom = 150f
            sizeTo = 0f
            strokeFrom = 6f
            strokeTo = 0f
            interp = Interp.pow10Out
            colorFrom = Color.valueOf("FF9C5A")
            colorTo = Color.valueOf("EC7458")
          },
          WaveEffect().apply {
            lifetime = 120f
            sizeFrom = 120f
            sizeTo = 0f
            strokeFrom = 4f
            strokeTo = 0f
            interp = Interp.pow5Out
            colorFrom = Color.valueOf("FF9C5A")
            colorTo = Color.valueOf("EC7458")
          },
          ParticleEffect().apply {
            particles = 1
            sizeFrom = 0f
            sizeTo = 8f
            length = 0f
            lightColor = Color.valueOf("FF9C5A")
            lifetime = 120f
            colorFrom = Color.valueOf("FF9C5A")
            colorTo = Color.valueOf("FF9C5A")
            cone = 360f
          },
          ParticleEffect().apply {
            particles = 1
            sizeFrom = 0f
            sizeTo = 4f
            length = 0f
            lightColor = Color.valueOf("F6E096")
            lifetime = 120f
            colorFrom = Color.white
            colorTo = Color.white
          }
        ).apply {
          this.lifetime = 120f
          followParent = true
          rotWithParent = true
        }

        shootEffect = ParticleEffect().apply {
          particles = 10
          length = 40f
          lifetime = 40f
          cone = 60f
          offset = 20f
          sizeFrom = 4f
          sizeTo = 0f
          interp = Interp.circleOut
          colorFrom = Color.valueOf("FF8663")
          colorTo = Color.valueOf("FF5845")
        }

        trailEffect = ParticleEffect().apply {
          particles = 7
          lifetime = 30f
          sizeFrom = 7f
          sizeTo = 0f
          cone = 360f
          length = 9f
          baseLength = 9f
          offsetX = -22.5f
          interp = Interp.circleOut
          lightColor = Color.valueOf("FF9C5A")
          colorFrom = Color.valueOf("FF9C5A")
          colorTo = Color.valueOf("EC7458AA")
        }

        hitEffect = MultiEffect(
          ParticleEffect().apply {
            line = true
            particles = 16
            lifetime = 50f
            baseLength = 75f
            length = 180f
            cone = -360f
            strokeFrom = 2.5f
            lenFrom = 15f
            lenTo = 15f
            colorFrom = Color.valueOf("FF9C5A")
            colorTo = Color.valueOf("EC7458")
          },
          WaveEffect().apply {
            lifetime = 35f
            sizeFrom = 60f
            sizeTo = 150f
            strokeFrom = 6f
            strokeTo = 0f
            interp = Interp.pow10Out
            colorFrom = Color.valueOf("FF9C5A")
            colorTo = Color.valueOf("EC7458")
          },
          WaveEffect().apply {
            lifetime = 35f
            sizeFrom = 40f
            sizeTo = 130f
            strokeFrom = 4f
            strokeTo = 0f
            interp = Interp.pow5Out
            colorFrom = Color.valueOf("FF9C5A")
            colorTo = Color.valueOf("EC7458")
          },
          ParticleEffect().apply {
            particles = 12
            length = 200f
            baseLength = 20f
            lifetime = 80f
            interp = Interp.circleOut
            sizeInterp = Interp.pow5In
            sizeFrom = 8f
            sizeTo = 1f
            colorFrom = Color.valueOf("787878")
            colorTo = Color.valueOf("787878")
          },
          ParticleEffect().apply {
            particles = 25
            length = 120f
            baseLength = 10f
            lifetime = 130f
            interp = Interp.circleOut
            sizeInterp = Interp.pow5In
            sizeFrom = 28f
            sizeTo = 1f
            colorFrom = Color.valueOf("FF9C5A")
            colorTo = Color.valueOf("FF9C5A")
          },
          ParticleEffect().apply {
            particles = 15
            length = 150f
            baseLength = 10f
            lifetime = 130f
            interp = Interp.circleOut
            sizeInterp = Interp.pow5In
            sizeFrom = 18f
            sizeTo = 1f
            colorFrom = Color.valueOf("FF9C5A")
            colorTo = Color.valueOf("FF9C5A")
          },
          ParticleEffect().apply {
            particles = 10
            length = 150f
            baseLength = 10f
            lifetime = 170f
            interp = Interp.circleOut
            sizeInterp = Interp.pow5In
            sizeFrom = 11f
            sizeTo = 1f
            colorFrom = Color.valueOf("FF9C5A")
            colorTo = Color.valueOf("EC745875")
          }
        )
      }

      parts.add(RegionPart("-top").apply {
        mirror = true
        under = true
        moveX = 0.75f
        moveY = 1f
        moveRot = -10f
        moves.add(DrawPart.PartMove().apply {
          progress = PartProgress.recoil
          rot = -10f
        })
      })
    }

    setWeapon("missiles") {
      x = 37f
      y = 28f
      reload = 600f
      shootY = 0f
      alternate = false
      minWarmup = 0.99f
      shootWarmupSpeed = 0.04f
      shootSound = Sounds.shootMissile
      shoot = ShootBarrel().apply {
        shots = 3
        shotDelay = 5f
        barrels = floatArrayOf(
          0f, 0f, -30f,
          0f, 0f, -15f,
          0f, 0f, -45f
        )
      }

      bullet = BulletType().apply {
        spawnUnit = IUnitTypes.火苗
        speed = 0f
        shootEffect = ParticleEffect().apply {
          particles = 7
          lifetime = 40f
          sizeFrom = 4f
          sizeTo = 0f
          cone = 20f
          length = 40f
          baseRotation = 180f
          interp = Interp.circleOut
          colorFrom = Color.valueOf("FF8663")
          colorTo = Color.valueOf("FF5845")
        }
      }

      parts.add(RegionPart("-shot").apply {
        x = -0.75f
        y = -5.25f
        under = true
        moveX = 1.5f
        moveRot = -60f
        moveY = 0.625f
        layerOffset = -0.01f
      })
    }








  }
}