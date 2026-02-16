package ice.content.unit

import arc.graphics.Blending
import arc.graphics.Color
import arc.math.Interp
import arc.math.geom.Rect
import ice.audio.ISounds
import ice.content.IStatus
import ice.content.IUnitTypes.重压
import ice.entities.bullet.base.BasicBulletType
import ice.entities.bullet.base.BulletType
import ice.entities.effect.MultiEffect
import ice.ui.bundle.BaseBundle
import ice.world.content.unit.IceUnitType
import ice.world.content.unit.ability.UnitSpawnAbility
import mindustry.content.Fx
import mindustry.entities.abilities.StatusFieldAbility
import mindustry.entities.effect.ExplosionEffect
import mindustry.entities.effect.ParticleEffect
import mindustry.entities.effect.WaveEffect
import mindustry.entities.part.DrawPart
import mindustry.entities.part.HaloPart
import mindustry.entities.part.RegionPart
import mindustry.entities.part.ShapePart
import mindustry.entities.pattern.ShootAlternate
import mindustry.gen.Sounds
import mindustry.graphics.Pal

class Scream : IceUnitType("scream") {
  init {
    BaseBundle.bundle {
      desc(zh_CN, "悲鸣", "超重型坦克,将只用于大型舰只及阵地防御的轨道炮作为主炮\n配备四门速射粒子炮及两门弧光冲击炮,正面火力极为凶猛\n新型装甲镀层使其足以抵御大口径炮弹并适应复杂的战场环境\n状态场使其能够更好地协同周围友军突破敌军阵线", "钢铁的履带滚滚向前")
    }
    squareShape = true
    omniMovement = false
    health = 499520f
    armor = 241f
    hitSize = 96f
    speed = 0.48f
    accel = 0.1f
    drag = 0.08f
    rotateSpeed = 0.54f
    healColor = Color.valueOf("FF5845")
    outlineColor = Color.valueOf("1F1F1F")
    hovering = true
    faceTarget = false
    rotateMoveFirst = false
    treadFrames = 20
    crushDamage = 96f
    treadPullOffset = 1
    treadRects = arrayOf(Rect(-145f, 171f, 50f, 40f), Rect(-153f, -211f, 56f, 40f))
    parts.add(RegionPart().apply {
      suffix = "-glow"
      outline = false
      color = Color.valueOf("FF0000")
      blending = Blending.additive
    })
    abilities.addAll(StatusFieldAbility(IStatus.庇护, 360f, 480f, 200f).apply {
      onShoot = true
      activeEffect = WaveEffect().apply {
        lifetime = 60f
        sides = 4
        sizeTo = 128f
        strokeFrom = 4f
        interp = Interp.exp10Out
        colorFrom = Color.valueOf("FFD37F")
        colorTo = Color.valueOf("FFD37F")
      }
    }, StatusFieldAbility(IStatus.突袭, 600f, 480f, 240f).apply {
      onShoot = true
      activeEffect = Fx.none
    }, UnitSpawnAbility(重压, 1200f).apply {
      color= Pal.remove
      alpha = 0.4f
      spawnY = -41f
      display = false
    })
    setWeapon("速射") {
      x = -19.5f
      y = 35f
      recoil = 3f
      shake = 3f
      shootY = 9f
      reload = 60f
      inaccuracy = 5f
      shootCone = 10f
      shoot = ShootAlternate().apply {
        shots = 3
        spread = 8f
        shotDelay = 5f
      }
      rotate = true
      rotateSpeed = 3f
      rotationLimit = 120f
      cooldownTime = 85f
      shootStatus = IStatus.鼓舞
      shootStatusDuration = 120f
      shootSound = Sounds.shootCorvus
      ejectEffect = Fx.casing4
      bullet = BasicBulletType(20f, 310f).apply {
        lifetime = 19f
        shrinkY = 0f
        width = 16f
        height = 16f
        weaveMag = 1f
        weaveScale = 4f
        trailLength = 12
        trailWidth = 3.2f
        trailColor = Color.valueOf("FFD37F")
        hitColor = Color.valueOf("FFD37F")
        absorbable = false
        reflectable = false
        status = IStatus.熔融
        statusDuration = 60f
        splashDamage = 610f
        splashDamageRadius = 40f
        buildingDamageMultiplier = 1.5f
        shootEffect = ParticleEffect().apply {
          particles = 3
          lifetime = 25f
          sizeFrom = 3f
          sizeTo = 0f
          cone = 15f
          length = 33f
          colorFrom = Color.valueOf("FFD37F")
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
          colorFrom = Color.valueOf("FFD37F")
          colorTo = Color.valueOf("F9C27A")
        }
        hitShake = 8f
        hitSound = Sounds.explosionPlasmaSmall
        despawnEffect = Fx.none
        hitEffect = MultiEffect(
          ParticleEffect().apply {
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
          colorFrom = Color.valueOf("FFD37F")
          colorTo = Color.valueOf("F9C27A")
        }, ParticleEffect().apply {
          particles = 9
          lifetime = 33f
          line = true
          strokeFrom = 2f
          lenFrom = 11f
          lenTo = 0f
          cone = 360f
          length = 80f
          colorFrom = Color.valueOf("FFD37F")
          colorTo = Color.valueOf("F9C27A")
        }, WaveEffect().apply {
          lifetime = 15f
          sizeTo = 80f
          strokeFrom = 4f
          colorFrom = Color.valueOf("F9C27A")
          colorTo = Color.valueOf("FFD37F")
        }, Fx.hitSquaresColor
        )
      }
      parts.add(RegionPart().apply {
        suffix = "-glow"
        outline = false
        color = Color.valueOf("FF0000")
        blending = Blending.additive
      })
    }

    setWeapon("速射") {
      x = 31.25f
      y = 10.25f
      recoil = 3f
      shake = 3f
      shootY = 9f
      reload = 60f
      inaccuracy = 5f
      shootCone = 10f
      shoot = ShootAlternate().apply {
        shots = 3
        spread = 8f
        shotDelay = 5f
      }
      rotate = true
      rotateSpeed = 3f
      rotationLimit = 120f
      cooldownTime = 85f
      shootStatus = IStatus.鼓舞
      shootStatusDuration = 120f
      shootSound = Sounds.shootCorvus
      ejectEffect = Fx.casing4
      bullet = BasicBulletType(20f, 310f).apply {
        lifetime = 19f
        shrinkY = 0f
        width = 16f
        height = 16f
        weaveMag = 1f
        weaveScale = 4f
        trailLength = 12
        trailWidth = 3.2f
        trailColor = Color.valueOf("FFD37F")
        hitColor = Color.valueOf("FFD37F")
        absorbable = false
        reflectable = false
        status = IStatus.湍能
        statusDuration = 60f
        splashDamage = 610f
        splashDamageRadius = 40f
        buildingDamageMultiplier = 1.5f
        shootEffect = ParticleEffect().apply {
          particles = 3
          lifetime = 25f
          sizeFrom = 3f
          sizeTo = 0f
          cone = 15f
          length = 33f
          colorFrom = Color.valueOf("FFD37F")
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
          colorFrom = Color.valueOf("FFD37F")
          colorTo = Color.valueOf("F9C27A")
        }
        hitShake = 8f
        hitSound = Sounds.explosionPlasmaSmall
        despawnEffect = Fx.none
        hitEffect = MultiEffect(
          ParticleEffect().apply {
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
          colorFrom = Color.valueOf("FFD37F")
          colorTo = Color.valueOf("F9C27A")
        }, ParticleEffect().apply {
          particles = 9
          lifetime = 33f
          line = true
          strokeFrom = 2f
          lenFrom = 11f
          lenTo = 0f
          cone = 360f
          length = 80f
          colorFrom = Color.valueOf("FFD37F")
          colorTo = Color.valueOf("F9C27A")
        }, WaveEffect().apply {
          lifetime = 15f
          sizeTo = 80f
          strokeFrom = 4f
          colorFrom = Color.valueOf("F9C27A")
          colorTo = Color.valueOf("FFD37F")
        }, Fx.hitSquaresColor
        )
      }
      parts.add(RegionPart().apply {
        suffix = "-glow"
        outline = false
        color = Color.valueOf("FF0000")
        blending = Blending.additive
      })
    }

    setWeapon("副炮") {
      x = 30.75f
      y = -25.5f
      recoil = 4f
      shake = 5f
      shootY = 19f
      reload = 120f
      shootCone = 5f
      rotate = true
      rotateSpeed = 2f
      layerOffset = 0.01f
      rotationLimit = 120f
      cooldownTime = 170f
      ejectEffect = Fx.casing4
      shootSound = Sounds.shootConquer
      bullet = BasicBulletType(20f, 1530f).apply {
        lifetime = 28.8f
        shrinkY = 0f
        width = 16f
        height = 16f
        trailLength = 12
        trailWidth = 3.2f
        trailColor = Color.valueOf("FFD37F")
        absorbable = false
        reflectable = false
        status = IStatus.电链
        statusDuration = 180f
        splashDamage = 730f
        splashDamageRadius = 90f
        pierceDamageFactor = 0.4f
        buildingDamageMultiplier = 3f
        shootEffect = MultiEffect(ParticleEffect().apply {
          followParent = false
          particles = 7
          lifetime = 25f
          line = true
          strokeFrom = 3f
          strokeTo = 3f
          lenFrom = 12f
          lenTo = 0f
          length = 57f
          colorFrom = Color.valueOf("FFD37F")
          colorTo = Color.valueOf("F9C27A")
          cone = 15f
        }, WaveEffect().apply {
          lifetime = 10f
          sizeFrom = 5f
          sizeTo = 80f
          strokeFrom = 3f
          strokeTo = 0f
          colorFrom = Color.valueOf("FFD37F")
          colorTo = Color.valueOf("F9C27A")
        })
        smokeEffect = MultiEffect(
          ParticleEffect().apply {
            followParent = false
            particles = 9
            lifetime = 125f
            sizeFrom = 4f
            sizeTo = 0f
            cone = 30f
            length = 60f
            interp = Interp.pow10Out
            sizeInterp = Interp.pow10In
            colorFrom = Color.valueOf("727272")
            colorTo = Color.valueOf("727272")
          }, Fx.smokeCloud
        )
        trailChance = 1f
        trailEffect = ParticleEffect().apply {
          region = "blank"
          particles = 1
          lifetime = 55f
          sizeFrom = 3f
          sizeTo = 0f
          cone = 360f
          length = 23f
          offset = 45f
          sizeInterp = Interp.pow5In
          colorFrom = Color.valueOf("FFD37F")
          colorTo = Color.valueOf("F9C27A")
        }
        hitShake = 8f
        hitSound = Sounds.explosionPlasmaSmall
        despawnEffect = Fx.none
        hitEffect = MultiEffect(ParticleEffect().apply {
          particles = 13
          lifetime = 145f
          sizeFrom = 16f
          sizeTo = 0f
          cone = 360f
          length = 55f
          baseLength = 33f
          interp = Interp.pow10Out
          sizeInterp = Interp.pow10In
          colorFrom = Color.valueOf("727272")
          colorTo = Color.valueOf("727272")
        }, ExplosionEffect().apply {
          lifetime = 33f
          waveLife = 15f
          waveStroke = 8f
          waveRadBase = 20f
          waveRad = 120f
          waveColor = Color.valueOf("FFD37F")
          smokes = 11
          smokeRad = 73f
          smokeColor = Color.valueOf("FFD37F")
          sparkColor = Color.valueOf("FFD37F")
          sparks = 23
          sparkRad = 120f
          sparkStroke = 3f
          sparkLen = 17f
        })
        parts.add(ShapePart().apply {
          hollow = true
          sides = 4
          radius = 16f
          stroke = 2f
          color = Color.valueOf("F9C27A")
          layer = 110f
        })
      }
      parts.add(RegionPart().apply {
        progress = DrawPart.PartProgress.recoil
        suffix = "-shot"
        under = true
        moveY = -4.5f
        children.add(RegionPart().apply {
          suffix = "-heater"
          outline = false
          color = Color.valueOf("FF0000")
          blending = Blending.additive
        })
        children.add(RegionPart().apply {
          suffix = "-glow"
          outline = false
          color = Color.valueOf("FF0000")
          blending = Blending.additive
        })
      })
    }

    setWeapon("炮") {
      x = 0f
      recoil = 8f
      shake = 20f
      shootY = 73f
      reload = 480f
      shootCone = 1f
      mirror = false
      rotate = true
      rotateSpeed = 1f
      layerOffset = 0.01f
      cooldownTime = 660f
      heatColor = Color.valueOf("F03B0E")
      shootSound = ISounds.聚爆
      parts.add(RegionPart().apply {
        progress = DrawPart.PartProgress.recoil
        suffix = "-shot"
        under = true
        moveY = -18.5f
        children.add(RegionPart().apply {
          suffix = "-shot-glow"
          outline = false
          color = Color.valueOf("FF0000")
          blending = Blending.additive
        })
      }, RegionPart().apply {
        suffix = "-glow"
        outline = false
        color = Color.valueOf("FF0000")
        blending = Blending.additive
      })
      bullet = BasicBulletType(20f, 7490f).apply {
        lifetime = 33.6f
        shrinkY = 0f
        width = 32f
        height = 32f
        trailLength = 12
        trailWidth = 6.4f
        trailColor = Color.valueOf("FFD37F")
        absorbable = false
        reflectable = false
        status = IStatus.电磁脉冲
        statusDuration = 240f
        splashDamage = 5370f
        splashDamageRadius = 210f
        buildingDamageMultiplier = 2.5f
        trailChance = 1f
        hitShake = 20f
        hitSound = Sounds.explosionPlasmaSmall
        despawnEffect = Fx.none
        fragBullets = 4
        fragLifeMin = 0.2f
        fragVelocityMin = 1f
        shootEffect = MultiEffect(ParticleEffect().apply {
          followParent = false
          particles = 12
          lifetime = 25f
          line = true
          strokeFrom = 5f
          lenFrom = 24f
          lenTo = 0f
          cone = 30f
          length = 109f
          colorFrom = Color.valueOf("FFD37F")
          colorTo = Color.valueOf("F9C27A")
        }, WaveEffect().apply {
          lifetime = 10f
          sizeFrom = 5f
          sizeTo = 80f
          strokeFrom = 3f
          strokeTo = 0f
          colorFrom = Color.valueOf("FFD37F")
          colorTo = Color.valueOf("F9C27A")
        })
        smokeEffect = MultiEffect(
          ParticleEffect().apply {
            followParent = false
            particles = 23
            lifetime = 125f
            sizeFrom = 8f
            sizeTo = 0f
            cone = 30f
            length = 60f
            interp = Interp.pow10Out
            sizeInterp = Interp.pow10In
            colorFrom = Color.valueOf("727272")
            colorTo = Color.valueOf("727272")
          }, Fx.smokeCloud
        )
        trailEffect = ParticleEffect().apply {
          region = "blank"
          particles = 1
          lifetime = 55f
          sizeFrom = 3f
          sizeTo = 0f
          cone = 360f
          length = 43f
          offset = 45f
          sizeInterp = Interp.pow5In
          colorFrom = Color.valueOf("FFD37F")
          colorTo = Color.valueOf("F9C27A")
        }
        hitEffect = MultiEffect(ParticleEffect().apply {
          particles = 23
          lifetime = 145f
          sizeFrom = 24f
          sizeTo = 0f
          cone = 360f
          length = 135f
          baseLength = 33f
          interp = Interp.pow10Out
          sizeInterp = Interp.pow10In
          colorFrom = Color.valueOf("F9C27A")
          colorTo = Color.valueOf("FFD37F")
        }, ParticleEffect().apply {
          particles = 19
          lifetime = 32f
          line = true
          strokeFrom = 8f
          lenFrom = 23f
          lenTo = 0f
          cone = 360f
          length = 193f
          baseLength = 47f
          colorFrom = Color.valueOf("FFD37F")
          colorTo = Color.valueOf("F9C27A")
        }, WaveEffect().apply {
          lifetime = 15f
          sizeFrom = 20f
          sizeTo = 240f
          strokeFrom = 12f
          strokeTo = 0f
          colorFrom = Color.valueOf("F9C27A")
          colorTo = Color.valueOf("FFD37F")
        })
        parts.addAll(HaloPart().apply {
          tri = true
          shapes = 2
          radius = 6f
          triLength = 64f
          haloRadius = 64f
          haloRotation = 90f
          color = Color.valueOf("F9C27A")
          layer = 110f
        }, HaloPart().apply {
          tri = true
          shapeRotation = 180f
          shapes = 2
          radius = 6f
          triLength = 16f
          haloRadius = 64f
          haloRotation = 90f
          color = Color.valueOf("F9C27A")
          layer = 110f
        }, ShapePart().apply {
          hollow = true
          sides = 4
          radius = 24f
          stroke = 2f
          color = Color.valueOf("F9C27A")
          layer = 110f
        }, ShapePart().apply {
          hollow = true
          sides = 4
          radius = 42f
          stroke = 2f
          color = Color.valueOf("F9C27A")
          layer = 110f
        })
        fragBullet = BulletType(4f, 360f).apply {
          lifetime = 40f
          splashDamage = 280f
          splashDamageRadius = 80f
          hitShake = 8f
          hitSound = ISounds.棱镜
          hitEffect = MultiEffect(ParticleEffect().apply {
            particles = 11
            lifetime = 121f
            sizeFrom = 8f
            sizeTo = 0f
            cone = 360f
            length = 56f
            baseLength = 15f
            interp = Interp.pow10Out
            sizeInterp = Interp.pow10In
            colorFrom = Color.valueOf("F9C27A")
            colorTo = Color.valueOf("FFD37F")
          }, ParticleEffect().apply {
            particles = 19
            lifetime = 33f
            line = true
            strokeFrom = 5f
            lenFrom = 29f
            lenTo = 0f
            cone = 360f
            length = 147f
            baseLength = 33f
            colorFrom = Color.valueOf("F9C27A")
            colorTo = Color.valueOf("FFD37F")
          }, WaveEffect().apply {
            lifetime = 15f
            sizeFrom = 5f
            sizeTo = 180f
            strokeFrom = 9f
            strokeTo = 0f
            colorFrom = Color.valueOf("F9C27A")
            colorTo = Color.valueOf("FFD37F")
          })
          despawnEffect = Fx.none
          fragBullets = 3
          fragLifeMin = 0.2f
          fragVelocityMin = 1f
          fragBullet = BulletType(4f, 155f).apply {
            lifetime = 30f
            hitShake = 4f
            hitSound = ISounds.棱镜
            hitEffect = MultiEffect(ParticleEffect().apply {
              particles = 5
              lifetime = 97f
              sizeFrom = 6f
              sizeTo = 0f
              cone = 360f
              length = 46f
              baseLength = 5f
              interp = Interp.pow10Out
              sizeInterp = Interp.pow10In
              colorFrom = Color.valueOf("F9C27A")
              colorTo = Color.valueOf("FFD37F")
            }, ParticleEffect().apply {
              particles = 13
              lifetime = 33f
              line = true
              strokeFrom = 4f
              lenFrom = 23f
              lenTo = 0f
              cone = 360f
              length = 100f
              baseLength = 20f
              colorFrom = Color.valueOf("FFD37F")
              colorTo = Color.valueOf("F9C27A")
            }, WaveEffect().apply {
              lifetime = 15f
              sizeFrom = 0f
              sizeTo = 120f
              strokeFrom = 6f
              strokeTo = 0f
              colorFrom = Color.valueOf("F9C27A")
              colorTo = Color.valueOf("FFD37F")
            })
            despawnEffect = Fx.none
            splashDamage = 95f
            splashDamageRadius = 40f
            fragBullets = 7
            fragLifeMin = 0.2f
            fragVelocityMin = 1f
            fragBullet = BulletType(4f, 9f).apply {
              lifetime = 20f
              splashDamage = 7f
              splashDamageRadius = 20f
              hitShake = 2f
              hitEffect = MultiEffect(ParticleEffect().apply {
                particles = 2
                lifetime = 73f
                sizeFrom = 4f
                sizeTo = 0f
                cone = 360f
                length = 26f
                interp = Interp.pow10Out
                sizeInterp = Interp.pow10In
                colorFrom = Color.valueOf("F9C27A")
                colorTo = Color.valueOf("FFD37F")
              }, ParticleEffect().apply {
                particles = 8
                lifetime = 33f
                line = true
                strokeFrom = 3f
                lenFrom = 17f
                lenTo = 0f
                cone = 360f
                length = 47f
                baseLength = 13f
                colorFrom = Color.valueOf("F9C27A")
                colorTo = Color.valueOf("FFD37F")
              }, WaveEffect().apply {
                lifetime = 15f
                sizeFrom = 0f
                sizeTo = 60f
                strokeFrom = 3f
                strokeTo = 0f
                colorFrom = Color.valueOf("F9C27A")
                colorTo = Color.valueOf("FFD37F")
              })
              despawnEffect = Fx.none
            }
          }
        }

      }

    }
  }
}