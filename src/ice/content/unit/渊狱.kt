package ice.content.unit

import arc.graphics.Color
import arc.graphics.g2d.Draw
import arc.graphics.g2d.TextureRegion
import arc.math.Interp
import ice.content.IStatus
import ice.core.IFiles.appendModName
import ice.entities.bullet.ContinuousLaserBulletType
import ice.entities.bullet.base.BasicBulletType
import ice.entities.bullet.base.BulletType
import ice.entities.effect.MultiEffect
import ice.world.content.unit.IceUnitType
import ice.world.meta.IceEffects
import mindustry.content.Fx
import mindustry.content.StatusEffects
import mindustry.entities.Effect
import mindustry.entities.bullet.ShrapnelBulletType
import mindustry.entities.effect.ExplosionEffect
import mindustry.entities.effect.ParticleEffect
import mindustry.entities.effect.WaveEffect
import mindustry.entities.pattern.ShootBarrel
import mindustry.entities.pattern.ShootPattern
import mindustry.gen.Sounds
import mindustry.world.meta.BlockFlag
import universecore.math.pow3OutIntrp
import universecore.struct.texture.LazyTextureSingleDelegate
import universecore.util.toColor

class 渊狱 :IceUnitType("unit_abyssPrison") {
  init {
    localization {
      zh_CN {
        localizedName = "渊狱"
      }
    }

    flying = true
    health = 113000f
    hitSize = 78f
    armor = 37f
    speed = 0.6f
    rotateSpeed = 1.2f

    engineSize = 12f
    engineOffset = 52f
    lightColor = Color.valueOf("FF5845")
    lowAltitude = true

    targetFlags = arrayOf(BlockFlag.reactor, BlockFlag.generator, BlockFlag.factory)

    fallSpeed = 0.004f

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
      colorFrom = Color.valueOf("FF6666")
      colorTo = Color.valueOf("FF5845AA")
      sizeFrom = 3.5f
      sizeTo = 0f
    })

    deathExplosionEffect = MultiEffect(effects = arrayOf(ParticleEffect().apply {
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
    })).apply {
      lifetime = 500f
    }
    setWeapon()
  }

  private fun setWeapon() {
    fun applyBullet(bullet: BulletType) {
      bullet.apply {
        damage = 325f
        lifetime = 20f
        speed = 8f
        hittable = false
        reflectable = false
        pierce = true
        pierceBuilding = true
        lightColor = Color.valueOf("FF5845")
        status = StatusEffects.melting
        statusDuration = 30f
      }
    }
    setWeapon("rapidFire") {
      x = 19f
      y = 38f
      recoil = 1f
      reload = 3f
      rotate = true
      shootCone = 15f
      targetInterval = 5f
      targetSwitchInterval = 5f
      shootSound = Sounds.shootFlame

      bullet = BasicBulletType().apply {
        applyBullet(this)

        shootEffect = MultiEffect(ParticleEffect().apply {
          particles = 12
          lifetime = 25f
          length = 120f
          sizeFrom = 4f
          sizeTo = 0f
          cone = 12f
          interp = Interp.pow3Out
          sizeInterp = Interp.pow5In
          colorFrom = Color.valueOf("FF8663")
          colorTo = Color.valueOf("FF5845")
        }, ParticleEffect().apply {
          particles = 12
          lifetime = 25f
          length = 60f
          sizeFrom = 3f
          sizeTo = 0f
          cone = 8f
          interp = Interp.pow3Out
          sizeInterp = Interp.pow5In
          colorFrom = Color.valueOf("FF5845")
          colorTo = Color.valueOf("3F3F3F")
        })

        hitEffect = Fx.none
        despawnEffect = Fx.none
      }
    }
    setWeapon("rapidFire") {
      x = 28f
      y = 27f
      recoil = 1f
      reload = 3f
      rotate = true
      shootCone = 15f
      shootSound = Sounds.shootFlame
      targetInterval = 5f
      targetSwitchInterval = 5f

      bullet = BasicBulletType().apply {
        applyBullet(this)

        shootEffect = MultiEffect(ParticleEffect().apply {
          particles = 10
          lifetime = 30f
          length = 100f
          sizeFrom = 4f
          sizeTo = 0f
          interp = Interp.circleOut
          sizeInterp = Interp.pow5In
          lightColor = Color.valueOf("FF5845")
          colorFrom = Color.valueOf("FF8663")
          colorTo = Color.valueOf("FF5845")
          cone = 15f
        }, ParticleEffect().apply {
          particles = 10
          lifetime = 35f
          length = 110f
          sizeFrom = 4f
          sizeTo = 0.5f
          interp = Interp.fastSlow
          lightColor = Color.valueOf("FF5845")
          colorFrom = Color.valueOf("FF8663")
          colorTo = Color.valueOf("FF5845")
          cone = 15f
        }, ParticleEffect().apply {
          particles = 10
          lifetime = 45f
          length = 120f
          sizeFrom = 4f
          sizeTo = 0.5f
          interp = Interp.fastSlow
          lightColor = Color.valueOf("FF5845")
          colorFrom = Color.valueOf("FF8663")
          colorTo = Color.valueOf("FF5845")
          cone = 10f
        }, ParticleEffect().apply {
          particles = 10
          lifetime = 25f
          length = 140f
          sizeFrom = 3f
          sizeTo = 0f
          interp = Interp.circleOut
          sizeInterp = Interp.pow5In
          lightColor = Color.valueOf("FF5845")
          colorFrom = Color.valueOf("FF8663")
          colorTo = Color.valueOf("FF5845")
          cone = 10f
        })

        smokeEffect = MultiEffect(ParticleEffect().apply {
          particles = 20
          sizeFrom = 3f
          sizeTo = 0f
          length = 160f
          lifetime = 45f
          lightOpacity = 0f
          interp = Interp.fastSlow
          colorFrom = Color.valueOf("FF5845")
          colorTo = Color.valueOf("3F3F3F")
          cone = 15f
        }, ParticleEffect().apply {
          particles = 20
          sizeFrom = 5f
          sizeTo = 0f
          length = 180f
          lifetime = 55f
          lightOpacity = 0f
          interp = Interp.circleOut
          sizeInterp = Interp.pow5In
          colorFrom = Color.valueOf("FF5845")
          colorTo = Color.valueOf("3F3F3F")
          cone = 10f
        })

        hitEffect = ParticleEffect().apply {
          line = true
          particles = 4
          lifetime = 60f
          offset = 10f
          interp = Interp.circleOut
          length = 70f
          cone = -30f
          strokeFrom = 3f
          strokeTo = 0f
          lenFrom = 7f
          lenTo = 0f
          colorFrom = Color.valueOf("FF5845")
          colorTo = Color.valueOf("FF5845")
        }

        despawnEffect = Fx.none
      }
    }
    setWeapon("energyGatheringCannon") {
      x = 27f
      y = -2f
      rotate = true
      rotateSpeed = 2f
      recoil = 3f
      shake = 5f
      shootY = 16.5f
      shootCone = 15f
      reload = 360f
      cooldownTime = 275f
      shootSound = Sounds.shootLaser
      continuous = true

      bullet = ContinuousLaserBulletType().apply {
        damage = 75f
        length = 200f
        width = 4f
        fadeTime = 30f
        lifetime = 240f
        shake = 0.5f
        status = IStatus.熔融
        statusDuration = 60f
        incendChance = 1f
        incendAmount = 5
        colors = arrayOf("D75B6E".toColor(), "BF3E47".toColor(), "FF8663".toColor())
        val spin = 9f
        val tex: TextureRegion by LazyTextureSingleDelegate("wide".appendModName())
        val circle: TextureRegion by LazyTextureSingleDelegate("circle")
        val colorFrom1 = Color.valueOf("FF5845")
        val colorTo1 = Color.valueOf("FF5845")
        shootEffect = Effect(240f) { e ->
          IceEffects.unitMountSXY(e.data, this@setWeapon, offsetY = -8.5f) { bulletX, bulletY ->
            val rad = 40f * e.fout().pow3OutIntrp
            Draw.color(colorFrom1, colorTo1, e.fin())
            Draw.z(109f)
            Draw.rect(tex, bulletX, bulletY, rad, rad / tex.ratio(), e.rotation + e.time * spin)
            Draw.rect(tex, bulletX, bulletY, rad, rad / tex.ratio(), e.rotation + e.time * -spin)

            val rad2 = 13f * e.fout()
            Draw.rect(circle, bulletX, bulletY, rad2, rad2 / circle.ratio())
            Draw.color(Color.white)
            val rad1 = 5.5f * e.fout()
            Draw.rect(circle, bulletX, bulletY, rad1, rad1 / circle.ratio())
          }
        }

        smokeEffect = Fx.smokeCloud

        hitEffect = ParticleEffect().apply {
          line = true
          particles = 8
          lifetime = 15f
          length = 60f
          lenFrom = 25f
          lenTo = 0f
          strokeFrom = 3f
          strokeTo = 1.5f
          colorFrom = Color.valueOf("D86E56")
          colorTo = Color.valueOf("D86E5650")
        }

        despawnEffect = Fx.smokeCloud
      }
    }
    setWeapon("missiles") {
      x = 21f
      y = -29f
      rotate = true
      rotateSpeed = 3f
      reload = 120f
      shootSound = Sounds.shootMissile

      shoot = ShootBarrel().apply {
        shots = 5
        shotDelay = 5f
        barrels = floatArrayOf(
          9f, 2f, 4.5f, 4.5f, 2f, 2.25f, 0f, 2f, 0f, -4.5f, 2f, -2.25f, -9f, 2f, -4.5f
        )
      }

      bullet = BasicBulletType().apply {
        damage = 155f
        shrinkY = 0f
        speed = 4f
        lifetime = 75f
        drag = -0.01f

        frontColor = Color.valueOf("D86E56")
        backColor = Color.valueOf("FFFFFF40")

        homingDelay = 45f
        homingRange = 80f
        homingPower = 0.12f

        weaveMag = 1f
        weaveScale = 5f

        trailChance = 1f
        trailWidth = 2f
        trailLength = 25
        trailColor = Color.valueOf("D86E56")

        splashDamage = 85f
        splashDamageRadius = 40f
        status = IStatus.熔融
        statusDuration = 60f
        hitSound = Sounds.explosion

        shootEffect = ParticleEffect().apply {
          particles = 10
          length = 40f
          lifetime = 25f
          interp = Interp.circleOut
          cone = 20f
          offset = 20f
          colorFrom = Color.valueOf("FF8663")
          colorTo = Color.valueOf("FF5845")
          sizeFrom = 4f
          sizeTo = 0f
        }

        trailEffect = ParticleEffect().apply {
          particles = 6
          length = 3f
          baseLength = 3f
          lifetime = 25f
          interp = Interp.circleOut
          cone = 360f
          offset = 3f
          colorFrom = Color.valueOf("FF8663")
          colorTo = Color.valueOf("FF5845")
          sizeFrom = 3f
          sizeTo = 0f
        }

        val particleEffect = ParticleEffect().apply {
          particles = 15
          length = 40f
          lifetime = 36f
          interp = Interp.circleOut
          cone = 360f
          colorFrom = Color.valueOf("FF8663")
          colorTo = Color.valueOf("FF5845")
          sizeFrom = 5f
          sizeTo = 0f
        }
        hitEffect = particleEffect

        despawnEffect = particleEffect
      }
    }
    setWeapon("cannon") {
      x = 0f
      y = 0f
      mirror = false
      recoil = 0f
      shake = 3f
      reload = 1800f
      cooldownTime = 1200f
      shootCone = 1f
      shoot = ShootPattern().apply { firstShotDelay = 140f }
      shootStatus = IStatus.过热
      shootStatusDuration = 150f
      chargeSound = Sounds.chargeLancer
      shootSound = Sounds.shootLaser

      bullet = BasicBulletType(sprite = "star").apply {
        damage = 2750f
        lifetime = 80f
        width = 16f
        height = 16f
        shrinkX = -0.8f
        shrinkY = -0.8f
        drag = -0.001f
        speed = 9f
        spin = 9f
        pierce = true
        reflectable = false
        absorbable = false
        keepVelocity = false
        bulletInterval = 8f
        intervalBullets = 1
        intervalRandomSpread = 0f
        status = IStatus.熔融
        statusDuration = 600f
        splashDamage = 3250f
        splashDamageRadius = 80f
        frontColor = Color.valueOf("FF8663")
        backColor = Color.valueOf("FF5845")
        trailLength = 45
        trailWidth = 3f
        trailColor = Color.valueOf("D86E56")
        trailChance = 1f

        chargeEffect = MultiEffect(ParticleEffect().apply {
          lightOpacity = 0f
          particles = 50
          length = 130f
          baseLength = -130f
          lifetime = 80f
          layer = 106f
          interp = Interp.exp5
          sizeFrom = 24f
          sizeTo = 13f
          colorFrom = Color.valueOf("FF584510")
          colorTo = Color.valueOf("FF5845")
        }, ParticleEffect().apply {
          particles = 35
          offset = 15f
          baseLength = 25f
          sizeFrom = 0f
          sizeTo = 4f
          length = 100f
          lifetime = 120f
          colorFrom = Color.valueOf("FF5845")
          colorTo = Color.valueOf("FF5845")
          cone = 360f
        }, WaveEffect().apply {
          lifetime = 120f
          sizeFrom = 75f
          sizeTo = 0f
          interp = Interp.pow5Out
          strokeFrom = 0f
          strokeTo = 8f
          colorFrom = Color.valueOf("FF5845")
          colorTo = Color.valueOf("FF5845")
        }, WaveEffect().apply {
          lifetime = 120f
          sizeFrom = 95f
          sizeTo = 0f
          interp = Interp.pow10Out
          strokeFrom = 0f
          strokeTo = 8f
          colorFrom = Color.valueOf("FF5845")
          colorTo = Color.valueOf("FF5845")
        }, ParticleEffect().apply {
          particles = 45
          offset = 100f
          sizeFrom = 1f
          sizeTo = 6f
          length = 200f
          baseLength = -200f
          interp = Interp.pow3In
          sizeInterp = Interp.pow5Out
          lifetime = 90f
          colorFrom = Color.valueOf("FF584500")
          colorTo = Color.valueOf("FF5845")
        }, ParticleEffect().apply {
          particles = 45
          offset = 100f
          sizeFrom = 1f
          sizeTo = 6f
          length = 250f
          baseLength = -250f
          interp = Interp.pow3In
          sizeInterp = Interp.pow5Out
          lifetime = 100f
          colorFrom = Color.valueOf("FF584500")
          colorTo = Color.valueOf("FF5845")
        }, ParticleEffect().apply {
          particles = 45
          offset = 100f
          sizeFrom = 1f
          sizeTo = 6f
          length = 300f
          baseLength = -300f
          interp = Interp.pow3In
          sizeInterp = Interp.pow5Out
          lifetime = 110f
          colorFrom = Color.valueOf("FF584500")
          colorTo = Color.valueOf("FF5845")
        }, ParticleEffect().apply {
          particles = 45
          offset = 100f
          sizeFrom = 1f
          sizeTo = 6f
          length = 350f
          baseLength = -350f
          interp = Interp.pow3In
          sizeInterp = Interp.pow5Out
          lifetime = 120f
          colorFrom = Color.valueOf("FF584500")
          colorTo = Color.valueOf("FF5845")
        }, ParticleEffect().apply {
          particles = 45
          offset = 100f
          sizeFrom = 1f
          sizeTo = 6f
          length = 400f
          baseLength = -400f
          interp = Interp.pow3In
          sizeInterp = Interp.pow5Out
          lifetime = 130f
          colorFrom = Color.valueOf("FF584500")
          colorTo = Color.valueOf("FF5845")
        }, ParticleEffect().apply {
          particles = 45
          offset = 100f
          sizeFrom = 1f
          sizeTo = 6f
          length = 450f
          baseLength = -450f
          interp = Interp.pow3In
          sizeInterp = Interp.pow5Out
          lifetime = 140f
          colorFrom = Color.valueOf("FF584500")
          colorTo = Color.valueOf("FF5845")
        })

        intervalBullet = BasicBulletType().apply {
          damage = 0f
          splashDamage = 155f
          splashDamageRadius = 40f
          shrinkY = 0f
          speed = 0f
          lifetime = 75f
          drag = -0.01f
          frontColor = Color.valueOf("D86E56")
          backColor = Color.valueOf("FFFFFF40")
          weaveMag = 1f
          weaveScale = 5f
          trailChance = 1f
          trailWidth = 2f
          trailLength = 25
          trailColor = Color.valueOf("D86E56")
          status = IStatus.熔融
          statusDuration = 60f
          hitSound = Sounds.explosion
          hitEffect = Fx.plasticExplosionFlak
          val effect1 = ParticleEffect().apply {
            lightOpacity = 0f
            particles = 25
            length = 90f
            baseLength = 10f
            lifetime = 100f
            interp = Interp.circleOut
            sizeInterp = Interp.pow5In
            sizeFrom = 13f
            sizeTo = 1f
            colorFrom = Color.valueOf("3F3F3F")
            colorTo = Color.valueOf("3F3F3FD8")
          }
          despawnEffect = MultiEffect(WaveEffect().apply {
            lifetime = 15f
            sizeFrom = 0f
            sizeTo = 105f
            interp = Interp.circleOut
            strokeFrom = 5f
            strokeTo = 0f
            lightColor = Color.valueOf("FF5845")
            colorFrom = Color.valueOf("FF5845")
            colorTo = Color.valueOf("FF5845D8")
          }, effect1, effect1, ParticleEffect().apply {
            particles = 20
            length = 100f
            baseLength = 10f
            lifetime = 90f
            interp = Interp.circleOut
            sizeInterp = Interp.pow5In
            sizeFrom = 2.5f
            sizeTo = 0f
            lightColor = Color.valueOf("FF5845")
            colorFrom = Color.valueOf("FF5845")
            colorTo = Color.valueOf("FF5845D8")
          }, ParticleEffect().apply {
            lightOpacity = 0f
            particles = 20
            length = 100f
            baseLength = 10f
            lifetime = 80f
            interp = Interp.circleOut
            sizeInterp = Interp.pow5In
            sizeFrom = 6.5f
            sizeTo = 1f
            colorFrom = Color.valueOf("3F3F3F")
            colorTo = Color.valueOf("3F3F3FD8")
          }, ParticleEffect().apply {
            line = true
            particles = 10
            lifetime = 30f
            length = 175f
            cone = -360f
            lenFrom = 7f
            lenTo = 7f
            lightColor = Color.valueOf("FF5845")
            strokeFrom = 7f
            strokeTo = 0f
            colorFrom = Color.valueOf("FF8663")
            colorTo = Color.valueOf("FF5845")
          }, ParticleEffect().apply {
            line = true
            particles = 20
            lifetime = 30f
            length = 145f
            cone = -360f
            lenFrom = 10f
            lenTo = 10f
            lightColor = Color.valueOf("FF5845")
            strokeFrom = 4f
            strokeTo = 0f
            colorFrom = Color.valueOf("FF8663")
            colorTo = Color.valueOf("FF5845")
          })
          fragBullets = 8
          fragAngle = 0f
          fragSpread = 45f
          fragRandomSpread = 0f
          fragBullet = ShrapnelBulletType().apply {
            damage = 400f
            status = IStatus.熔融
            statusDuration = 120f
            width = 35f
            lifetime = 18f
            length = 200f
            fromColor = Color.valueOf("FF8663")
            toColor = Color.valueOf("FF5845")
            val effect = ParticleEffect().apply {
              line = true
              particles = 10
              lifetime = 20f
              length = 75f
              cone = -360f
              lenFrom = 6f
              lenTo = 6f
              lightColor = Color.valueOf("FF5845")
              strokeFrom = 3f
              strokeTo = 0f
              colorFrom = Color.valueOf("FF8663")
              colorTo = Color.valueOf("FF5845")
            }
            shootEffect = effect
            hitEffect = effect
          }
        }

        trailEffect = MultiEffect(ParticleEffect().apply {
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

        smokeEffect = ParticleEffect().apply {
          particles = 15
          length = 40f
          lifetime = 35f
          interp = Interp.circleOut
          cone = 20f
          offset = 20f
          colorFrom = Color.valueOf("787878")
          colorTo = Color.valueOf("787878")
          sizeFrom = 4f
          sizeTo = 0f
        }

        hitSound = Sounds.explosion

        despawnEffect = MultiEffect(ExplosionEffect().apply {
          waveColor = Color.valueOf("FF8663")
          sparkColor = Color.valueOf("FF8663")
          smokeColor = Color.valueOf("3F3F3F")
          smokes = 60
          smokeSize = 18f
          smokeSizeBase = 10.6f
          smokeRad = 73f
          waveLife = 60f
          waveStroke = 2f
          waveRad = 165f
          waveRadBase = 2f
          sparkLen = 25f
          sparks = 40
        }, WaveEffect().apply {
          lifetime = 70f
          sizeFrom = 0f
          sizeTo = 80f
          strokeFrom = 7f
          strokeTo = 0f
          colorFrom = Color.valueOf("FF5845")
          colorTo = Color.valueOf("FF8663")
        }, ParticleEffect().apply {
          particles = 1
          sizeFrom = 0f
          sizeTo = 60f
          length = 0f
          spin = 11f
          lightColor = Color.valueOf("FF5845")
          lifetime = 110f
          layer = 109f
          region = "star".appendModName()
          colorFrom = Color.valueOf("FF5845")
          colorTo = Color.valueOf("FF8663")
        }, ParticleEffect().apply {
          particles = 1
          sizeFrom = 0f
          sizeTo = 60f
          length = 0f
          lightColor = Color.valueOf("FF5845")
          spin = -11f
          lifetime = 110f
          layer = 109f
          region = "star".appendModName()
          colorFrom = Color.valueOf("FF5845")
          colorTo = Color.valueOf("FF8663")
        }, ParticleEffect().apply {
          lightOpacity = 0f
          particles = 50
          length = 100f
          baseLength = 30f
          lifetime = 80f
          layer = 106f
          interp = Interp.exp5
          sizeFrom = 24f
          sizeTo = 13f
          colorFrom = Color.valueOf("FF5845")
          colorTo = Color.valueOf("FF584510")
        }, ParticleEffect().apply {
          line = true
          particles = 25
          lifetime = 30f
          offset = 60f
          baseLength = 20f
          length = 120f
          cone = -360f
          strokeTo = 0f
          lightColor = Color.valueOf("FF5845")
          strokeFrom = 2.5f
          sizeFrom = 20f
          sizeTo = 20f
          colorFrom = Color.valueOf("FF5845")
          colorTo = Color.valueOf("FF5845")
        }, ParticleEffect().apply {
          particles = 1
          sizeFrom = 48f
          sizeTo = 0f
          length = 0f
          layer = 109f
          lifetime = 48f
          colorFrom = Color.valueOf("FF5845")
          colorTo = Color.valueOf("FF5845")
          cone = 360f
        }, ParticleEffect().apply {
          particles = 1
          sizeFrom = 15f
          sizeTo = 0f
          length = 0f
          lifetime = 48f
          colorFrom = Color.white
          colorTo = Color.white
          cone = 360f
        }, ParticleEffect().apply {
          line = true
          particles = 20
          lifetime = 60f
          baseLength = 30f
          offset = 30f
          interp = Interp.circleOut
          length = 100f
          cone = -360f
          lenFrom = 7f
          lenTo = 0f
          colorFrom = Color.valueOf("FF5845")
          colorTo = Color.valueOf("FF5845")
        }, ParticleEffect().apply {
          particles = 40
          length = 80f
          lifetime = 50f
          cone = 360f
          colorFrom = Color.valueOf("FF5845")
          colorTo = Color.valueOf("FF5845")
          sizeFrom = 5f
          sizeTo = 0f
        }, ParticleEffect().apply {
          particles = 40
          length = 100f
          lifetime = 80f
          cone = 360f
          colorFrom = Color.valueOf("FF5845")
          colorTo = Color.valueOf("FF5845")
          sizeFrom = 5f
          sizeTo = 1f
        }, WaveEffect().apply {
          lifetime = 25f
          sizeFrom = 0f
          sizeTo = 135f
          interp = Interp.circleOut
          strokeFrom = 3f
          strokeTo = 0f
          colorFrom = Color.valueOf("FF5845")
          colorTo = Color.valueOf("FF5845")
        }, WaveEffect().apply {
          lifetime = 25f
          sizeFrom = 0f
          sizeTo = 120f
          interp = Interp.circleOut
          strokeFrom = 4f
          strokeTo = 0f
          colorFrom = Color.valueOf("FF5845")
          colorTo = Color.valueOf("FF5845")
        })

        hitEffect = MultiEffect(ParticleEffect().apply {
          particles = 25
          sizeFrom = 5.5f
          sizeTo = 1f
          interp = Interp.circleOut
          length = 40f
          lifetime = 60f
          layer = 109f
          region = "star".appendModName()
          lightColor = Color.valueOf("FF5845")
          colorFrom = Color.valueOf("FF5845")
          colorTo = Color.valueOf("FF5845")
        }, ParticleEffect().apply {
          particles = 25
          sizeFrom = 5.5f
          sizeTo = 1f
          interp = Interp.circleOut
          length = 50f
          lifetime = 80f
          layer = 109f
          region = "star".appendModName()
          lightColor = Color.valueOf("FF5845")
          colorFrom = Color.valueOf("FF5845")
          colorTo = Color.valueOf("FF5845")
        }, ParticleEffect().apply {
          particles = 25
          sizeFrom = 5.5f
          sizeTo = 1f
          interp = Interp.circleOut
          length = 60f
          lifetime = 100f
          layer = 109f
          region = "star".appendModName()
          lightColor = Color.valueOf("FF5845")
          colorFrom = Color.valueOf("FF5845")
          colorTo = Color.valueOf("FF5845")
        }, ParticleEffect().apply {
          particles = 25
          sizeFrom = 5.5f
          sizeTo = 1f
          interp = Interp.circleOut
          length = 70f
          lifetime = 120f
          layer = 109f
          region = "star".appendModName()
          lightColor = Color.valueOf("FF5845")
          colorFrom = Color.valueOf("FF5845")
          colorTo = Color.valueOf("FF5845")
        }, ParticleEffect().apply {
          particles = 25
          sizeFrom = 5.5f
          sizeTo = 1f
          interp = Interp.circleOut
          length = 80f
          lifetime = 140f
          layer = 109f
          region = "star".appendModName()
          lightColor = Color.valueOf("FF5845")
          colorFrom = Color.valueOf("FF5845")
          colorTo = Color.valueOf("FF5845")
        })
      }
    }
  }
}