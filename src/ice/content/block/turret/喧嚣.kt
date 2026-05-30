package ice.content.block.turret

import arc.graphics.Color
import arc.math.Interp
import ice.audio.ISounds
import ice.content.IItems
import ice.content.IStatus
import ice.entities.bullet.base.BasicBulletType
import ice.entities.effect.MultiEffect
import mindustry.content.Fx
import mindustry.content.StatusEffects
import mindustry.entities.effect.ParticleEffect
import mindustry.entities.effect.WaveEffect
import mindustry.entities.pattern.ShootHelix
import mindustry.entities.pattern.ShootMulti
import mindustry.gen.Sounds
import mindustry.type.Category
import mindustry.type.Item
import mindustry.type.Liquid
import singularity.world.blocks.turrets.SglTurret

class 喧嚣 :SglTurret("turret_clamor") {
  init {
    localization {
      zh_CN {
        localizedName = "喧嚣"
        description = "扩散性等离子炮塔,向敌人发射逐渐消散的螺旋等离子体\\n等离子体在消散的过程中会分裂出许多小型能量裂片"
      }
    }

    health = 4320
    size = 4
    shake = 5f
    recoil = 4f
    shootY = 4f
    range = 288f
    rotateSpeed = 4f
    cooldownTime = 165f
    shootSound = Sounds.shootSmite
    ammoUseEffect = Fx.casing3Double

    shoot = ShootMulti().apply {
      source = ShootHelix().apply {
        scl = 8f
        mag = 1f
      }
      dest = arrayOf(ShootHelix().apply {
        scl = 8f
        mag = 1f
      })
    }

    newCoolant(1f, 0.4f, { l: Liquid? -> l!!.heatCapacity >= 0.4f && l.temperature <= 0.5f }, 0.25f, 20f)

    requirements(
      Category.turret, IItems.铜锭, 1100, IItems.铱板, 480, IItems.钴钢, 240, IItems.导能回路, 210, IItems.暮光合金, 225
    )

    setAmmo()
    limitRange()
  }

  fun setAmmo() {
    addAmmoType(IItems.铬锭) {
      BasicBulletType(6f, 31f).apply {
        lifetime = 50f
        width = 9f
        height = 16f
        shrinkY = 0f
        backColor = Color.valueOf("626F9B")
        frontColor = Color.valueOf("95ABD9")
        hitColor = Color.valueOf("95ABD9")
        trailColor = Color.valueOf("95ABD9")
        trailLength = 12
        trailWidth = 2f
        trailChance = 1f

        trailEffect = ParticleEffect().apply {
          particles = 1
          lifetime = 20f
          length = 12f
          cone = 360f
          sizeFrom = 2.3f
          sizeTo = 0f
          interp = Interp.pow10Out
          sizeInterp = Interp.pow5In
          colorFrom = Color.valueOf("95ABD9")
          colorTo = Color.valueOf("95ABD9")
        }

        pierceCap = 2
        knockback = 8f
        pierceBuilding = true
        splashDamage = 17f
        splashDamageRadius = 24f
        ammoMultiplier = 6f
        reloadMultiplier = 1.4f
        shootEffect = Fx.shootSmokeSmite

        smokeEffect = MultiEffect(ParticleEffect().apply {
          particles = 20
          lifetime = 12f
          line = true
          strokeFrom = 2f
          strokeTo = 0f
          lenFrom = 16f
          lenTo = 8f
          length = 60f
          colorFrom = Color.valueOf("626F9B")
          colorTo = Color.valueOf("95ABD9")
          cone = 20f
        }, ParticleEffect().apply {
          particles = 12
          lifetime = 36f
          length = 60f
          sizeFrom = 4f
          cone = 20f
          interp = Interp.pow2Out
          sizeInterp = Interp.pow2In
          lightColor = Color.valueOf("626F9B")
          colorFrom = Color.valueOf("626F9B")
          colorTo = Color.valueOf("95ABD9")
        })

        despawnEffect = Fx.none
        hitShake = 2f
        hitSound = ISounds.棱镜

        hitEffect = MultiEffect(Fx.hitBulletColor, ParticleEffect().apply {
          particles = 3
          lifetime = 25f
          sizeFrom = 3f
          sizeTo = 0f
          length = 12f
          interp = Interp.pow5Out
          sizeInterp = Interp.pow2In
          colorFrom = Color.valueOf("626F9B")
          colorTo = Color.valueOf("95ABD9")
        }, WaveEffect().apply {
          lifetime = 15f
          sizeFrom = 0f
          sizeTo = 64f
          strokeFrom = 6f
          strokeTo = 0f
          colorFrom = Color.valueOf("626F9B")
          colorTo = Color.valueOf("95ABD9")
        })

        bulletInterval = 3f
        intervalBullets = 2
        intervalSpread = 45f
        intervalRandomSpread = 0f

        intervalBullet = BasicBulletType(3f, 16f).apply {
          lifetime = 24f
          trailWidth = 1f
          trailLength = 6
          knockback = 4f
          splashDamage = 9f
          splashDamageRadius = 6f
          trailColor = Color.valueOf("95ABD9")
          backColor = Color.valueOf("626F9B")
          frontColor = Color.valueOf("95ABD9")
          hitSound = Sounds.shootLaser
          despawnEffect = Fx.none

          hitEffect = WaveEffect().apply {
            lifetime = 10f
            sizeTo = 6f
            strokeFrom = 4f
            colorFrom = Color.valueOf("626F9B")
            colorTo = Color.valueOf("95ABD9")
          }
        }
      }
    }

    addAmmoType(IItems.钍锭) {
      BasicBulletType(6f, 53f).apply {
        lifetime = 50f
        width = 9f
        height = 16f
        shrinkY = 0f
        backColor = Color.valueOf("CB8EBF")
        frontColor = Color.valueOf("F9A3C7")
        hitColor = Color.valueOf("F9A3C7")
        trailColor = Color.valueOf("F9A3C7")
        trailLength = 12
        trailWidth = 2f
        trailChance = 1f

        trailEffect = ParticleEffect().apply {
          particles = 1
          lifetime = 20f
          length = 12f
          cone = 360f
          sizeFrom = 2.3f
          sizeTo = 0f
          interp = Interp.pow10Out
          sizeInterp = Interp.pow5In
          colorFrom = Color.valueOf("F9A3C7")
          colorTo = Color.valueOf("F9A3C7")
        }

        pierceCap = 3
        knockback = 8f
        status = IStatus.辐射
        statusDuration = 120f
        pierceBuilding = true
        splashDamage = 37f
        splashDamageRadius = 24f
        shootEffect = Fx.shootSmokeSmite

        smokeEffect = MultiEffect(ParticleEffect().apply {
          particles = 20
          lifetime = 12f
          line = true
          strokeFrom = 2f
          strokeTo = 0f
          lenFrom = 16f
          lenTo = 8f
          length = 60f
          colorFrom = Color.valueOf("CB8EBF")
          colorTo = Color.valueOf("F9A3C7")
          cone = 20f
        }, ParticleEffect().apply {
          particles = 12
          lifetime = 36f
          length = 60f
          sizeFrom = 4f
          cone = 20f
          interp = Interp.pow2Out
          sizeInterp = Interp.pow2In
          lightColor = Color.valueOf("CB8EBF")
          colorFrom = Color.valueOf("CB8EBF")
          colorTo = Color.valueOf("F9A3C7")
        })

        despawnEffect = Fx.none
        hitShake = 3f
        hitSound = ISounds.棱镜

        hitEffect = MultiEffect(Fx.hitBulletColor, ParticleEffect().apply {
          particles = 3
          lifetime = 25f
          sizeFrom = 3f
          sizeTo = 0f
          length = 12f
          interp = Interp.pow5Out
          sizeInterp = Interp.pow2In
          colorFrom = Color.valueOf("CB8EBF")
          colorTo = Color.valueOf("F9A3C7")
        }, WaveEffect().apply {
          lifetime = 15f
          sizeFrom = 0f
          sizeTo = 64f
          strokeFrom = 6f
          strokeTo = 0f
          colorFrom = Color.valueOf("CB8EBF")
          colorTo = Color.valueOf("F9A3C7")
        })

        bulletInterval = 3f
        intervalBullets = 2
        intervalSpread = 45f
        intervalRandomSpread = 0f

        intervalBullet = BasicBulletType(3f, 27f).apply {
          lifetime = 24f
          trailWidth = 1f
          trailLength = 6
          knockback = 4f
          status = IStatus.辐射
          statusDuration = 30f
          splashDamage = 12f
          splashDamageRadius = 6f
          trailColor = Color.valueOf("F9A3C7")
          backColor = Color.valueOf("CB8EBF")
          frontColor = Color.valueOf("F9A3C7")
          hitSound = Sounds.shootLaser
          despawnEffect = Fx.none

          hitEffect = WaveEffect().apply {
            lifetime = 10f
            sizeTo = 6f
            strokeFrom = 4f
            colorFrom = Color.valueOf("CB8EBF")
            colorTo = Color.valueOf("F9A3C7")
          }
        }
      }
    }

    addAmmoType(IItems.暮光合金) {
      BasicBulletType(6f, 26f).apply {
        lifetime = 50f
        width = 9f
        height = 16f
        shrinkY = 0f
        backColor = Color.valueOf("E8D174")
        frontColor = Color.valueOf("FCF387")
        hitColor = Color.valueOf("FCF387")
        trailColor = Color.valueOf("FCF387")
        trailLength = 12
        trailWidth = 2f
        trailChance = 1f

        trailEffect = ParticleEffect().apply {
          particles = 1
          lifetime = 20f
          length = 12f
          cone = 360f
          sizeFrom = 2.3f
          sizeTo = 0f
          interp = Interp.pow10Out
          sizeInterp = Interp.pow5In
          colorFrom = Color.valueOf("FCF387")
          colorTo = Color.valueOf("FCF387")
        }

        pierceCap = 4
        knockback = 8f
        status = StatusEffects.shocked
        pierceBuilding = true
        lightning = 3
        lightningLength = 7
        splashDamage = 150f
        splashDamageRadius = 38f
        ammoMultiplier = 3f
        shootEffect = Fx.shootSmokeSmite

        smokeEffect = MultiEffect(ParticleEffect().apply {
          particles = 20
          lifetime = 12f
          line = true
          strokeFrom = 2f
          strokeTo = 0f
          lenFrom = 16f
          lenTo = 8f
          length = 60f
          colorFrom = Color.valueOf("E8D174")
          colorTo = Color.valueOf("FCF387")
          cone = 20f
        }, ParticleEffect().apply {
          particles = 12
          lifetime = 36f
          length = 60f
          sizeFrom = 4f
          cone = 20f
          interp = Interp.pow2Out
          sizeInterp = Interp.pow2In
          lightColor = Color.valueOf("E8D174")
          colorFrom = Color.valueOf("E8D174")
          colorTo = Color.valueOf("FCF387")
        })

        despawnEffect = Fx.none
        hitShake = 3f
        hitSound = ISounds.棱镜

        hitEffect = MultiEffect(Fx.hitBulletColor, ParticleEffect().apply {
          particles = 3
          lifetime = 25f
          sizeFrom = 3f
          sizeTo = 0f
          length = 12f
          interp = Interp.pow5Out
          sizeInterp = Interp.pow2In
          colorFrom = Color.valueOf("E8D174")
          colorTo = Color.valueOf("FCF387")
        }, WaveEffect().apply {
          lifetime = 15f
          sizeFrom = 0f
          sizeTo = 64f
          strokeFrom = 6f
          strokeTo = 0f
          colorFrom = Color.valueOf("E8D174")
          colorTo = Color.valueOf("FCF387")
        })

        bulletInterval = 3f
        intervalBullets = 2
        intervalSpread = 45f
        intervalRandomSpread = 0f

        intervalBullet = BasicBulletType(3f, 13f).apply {
          lifetime = 24f
          trailWidth = 1f
          trailLength = 6
          knockback = 4f
          status = StatusEffects.shocked
          lightning = 1
          lightningLength = 3
          splashDamage = 50f
          splashDamageRadius = 9.5f
          trailColor = Color.valueOf("FCF387")
          backColor = Color.valueOf("E8D174")
          frontColor = Color.valueOf("FCF387")
          hitSound = Sounds.shootLaser
          despawnEffect = Fx.none

          hitEffect = WaveEffect().apply {
            lifetime = 10f
            sizeTo = 6f
            strokeFrom = 4f
            colorFrom = Color.valueOf("E8D174")
            colorTo = Color.valueOf("FCF387")
          }
        }
      }
    }

    addAmmoType(IItems.爆炸化合物) {
      BasicBulletType(6f, 16f).apply {
        lifetime = 50f
        width = 9f
        height = 16f
        shrinkY = 0f
        backColor = Color.valueOf("C85C51")
        frontColor = Color.valueOf("FF795E")
        hitColor = Color.valueOf("FF795E")
        trailColor = Color.valueOf("FF795E")
        trailLength = 12
        trailWidth = 2f
        trailChance = 1f

        trailEffect = ParticleEffect().apply {
          particles = 1
          lifetime = 20f
          length = 12f
          cone = 360f
          sizeFrom = 2.3f
          sizeTo = 0f
          interp = Interp.pow10Out
          sizeInterp = Interp.pow5In
          colorFrom = Color.valueOf("FF795E")
          colorTo = Color.valueOf("FF795E")
        }

        pierceCap = 2
        knockback = 8f
        status = StatusEffects.blasted
        pierceBuilding = true
        splashDamage = 90f
        splashDamageRadius = 60f
        reloadMultiplier = 0.9f
        shootEffect = Fx.shootSmokeSmite

        smokeEffect = MultiEffect(ParticleEffect().apply {
          particles = 20
          lifetime = 12f
          line = true
          strokeFrom = 2f
          strokeTo = 0f
          lenFrom = 16f
          lenTo = 8f
          length = 60f
          colorFrom = Color.valueOf("C85C51")
          colorTo = Color.valueOf("FF795E")
          cone = 20f
        }, ParticleEffect().apply {
          particles = 12
          lifetime = 36f
          length = 60f
          sizeFrom = 4f
          cone = 20f
          interp = Interp.pow2Out
          sizeInterp = Interp.pow2In
          lightColor = Color.valueOf("C85C51")
          colorFrom = Color.valueOf("C85C51")
          colorTo = Color.valueOf("FF795E")
        })

        despawnEffect = Fx.none
        hitShake = 5f
        hitSound = ISounds.棱镜

        hitEffect = MultiEffect(Fx.hitBulletColor, ParticleEffect().apply {
          particles = 3
          lifetime = 25f
          sizeFrom = 3f
          sizeTo = 0f
          length = 12f
          interp = Interp.pow5Out
          sizeInterp = Interp.pow2In
          colorFrom = Color.valueOf("C85C51")
          colorTo = Color.valueOf("FF795E")
        }, WaveEffect().apply {
          lifetime = 15f
          sizeFrom = 0f
          sizeTo = 64f
          strokeFrom = 6f
          strokeTo = 0f
          colorFrom = Color.valueOf("C85C51")
          colorTo = Color.valueOf("FF795E")
        })

        bulletInterval = 3f
        intervalBullets = 2
        intervalSpread = 45f
        intervalRandomSpread = 0f

        intervalBullet = BasicBulletType(3f, 8f).apply {
          lifetime = 24f
          trailWidth = 1f
          trailLength = 6
          knockback = 4f
          status = StatusEffects.blasted
          splashDamage = 30f
          splashDamageRadius = 15f
          trailColor = Color.valueOf("FF795E")
          backColor = Color.valueOf("C85C51")
          frontColor = Color.valueOf("FF795E")
          hitSound = Sounds.shootLaser
          despawnEffect = Fx.none

          hitEffect = WaveEffect().apply {
            lifetime = 10f
            sizeTo = 6f
            strokeFrom = 4f
            colorFrom = Color.valueOf("C85C51")
            colorTo = Color.valueOf("FF795E")
          }
        }
      }
    }

    addAmmoType(IItems.铱板) {
      BasicBulletType(6f, 73f).apply {
        lifetime = 50f
        width = 9f
        height = 16f
        shrinkY = 0f
        hitColor = Color.valueOf("FFF8E8")
        trailColor = Color.valueOf("FFF8E8")
        trailLength = 12
        trailWidth = 2f
        trailChance = 1f

        trailEffect = ParticleEffect().apply {
          particles = 1
          lifetime = 20f
          length = 12f
          cone = 360f
          sizeFrom = 2.3f
          sizeTo = 0f
          interp = Interp.pow10Out
          sizeInterp = Interp.pow5In
          colorFrom = Color.valueOf("FFF8E8")
          colorTo = Color.valueOf("FFF8E8")
        }

        pierceCap = 3
        knockback = 8f
        status = IStatus.破甲I
        statusDuration = 60f
        pierceBuilding = true
        splashDamage = 27f
        splashDamageRadius = 24f
        ammoMultiplier = 4f
        shootEffect = Fx.shootSmokeSmite

        smokeEffect = MultiEffect(ParticleEffect().apply {
          particles = 20
          lifetime = 12f
          line = true
          strokeFrom = 2f
          strokeTo = 0f
          lenFrom = 16f
          lenTo = 8f
          length = 60f
          colorFrom = Color.valueOf("F9C27A")
          colorTo = Color.valueOf("FFF8E8")
          cone = 20f
        }, ParticleEffect().apply {
          particles = 12
          lifetime = 36f
          length = 60f
          sizeFrom = 4f
          cone = 20f
          interp = Interp.pow2Out
          sizeInterp = Interp.pow2In
          lightColor = Color.valueOf("F9C27A")
          colorFrom = Color.valueOf("F9C27A")
          colorTo = Color.valueOf("FFF8E8")
        })

        despawnEffect = Fx.none
        hitShake = 3f
        hitSound = ISounds.棱镜

        hitEffect = MultiEffect(Fx.hitBulletColor, ParticleEffect().apply {
          particles = 3
          lifetime = 25f
          sizeFrom = 3f
          sizeTo = 0f
          length = 12f
          interp = Interp.pow5Out
          sizeInterp = Interp.pow2In
          colorFrom = Color.valueOf("F9C27A")
          colorTo = Color.valueOf("FFF8E8")
        }, WaveEffect().apply {
          lifetime = 15f
          sizeFrom = 0f
          sizeTo = 64f
          strokeFrom = 6f
          strokeTo = 0f
          colorFrom = Color.valueOf("F9C27A")
          colorTo = Color.valueOf("FFF8E8")
        })

        bulletInterval = 3f
        intervalBullets = 2
        intervalSpread = 45f
        intervalRandomSpread = 0f

        intervalBullet = BasicBulletType(3f, 37f).apply {
          lifetime = 24f
          trailWidth = 1f
          trailLength = 6
          knockback = 4f
          status = IStatus.破甲I
          statusDuration = 30f
          splashDamage = 9f
          splashDamageRadius = 6f
          trailColor = Color.valueOf("FFF8E8")
          hitSound = Sounds.shootLaser
          despawnEffect = Fx.none

          hitEffect = WaveEffect().apply {
            lifetime = 10f
            sizeTo = 6f
            strokeFrom = 4f
            colorFrom = Color.valueOf("F9C27A")
            colorTo = Color.valueOf("FFF8E8")
          }
        }
      }
    }

    addAmmoType(IItems.低温化合物) {
      BasicBulletType(6f, 112f).apply {
        lifetime = 50f
        width = 9f
        height = 16f
        shrinkY = 0f
        backColor = Color.valueOf("87CEEB")
        frontColor = Color.valueOf("C0ECFF")
        hitColor = Color.valueOf("C0ECFF")
        trailColor = Color.valueOf("C0ECFF")
        trailLength = 12
        trailWidth = 2f
        trailChance = 1f

        trailEffect = ParticleEffect().apply {
          particles = 1
          lifetime = 20f
          length = 12f
          cone = 360f
          sizeFrom = 2.3f
          sizeTo = 0f
          interp = Interp.pow10Out
          sizeInterp = Interp.pow5In
          colorFrom = Color.valueOf("C0ECFF")
          colorTo = Color.valueOf("C0ECFF")
        }

        knockback = 8f
        status = IStatus.封冻
        statusDuration = 120f
        splashDamage = 143f
        splashDamageRadius = 18f
        ammoMultiplier = 3f
        reloadMultiplier = 0.4f
        shootEffect = Fx.shootSmokeSmite

        smokeEffect = MultiEffect(ParticleEffect().apply {
          particles = 20
          lifetime = 12f
          line = true
          strokeFrom = 2f
          strokeTo = 0f
          lenFrom = 16f
          lenTo = 8f
          length = 60f
          colorFrom = Color.valueOf("87CEEB")
          colorTo = Color.valueOf("C0ECFF")
          cone = 20f
        }, ParticleEffect().apply {
          particles = 12
          lifetime = 36f
          length = 60f
          sizeFrom = 4f
          cone = 20f
          interp = Interp.pow2Out
          sizeInterp = Interp.pow2In
          lightColor = Color.valueOf("87CEEB")
          colorFrom = Color.valueOf("87CEEB")
          colorTo = Color.valueOf("C0ECFF")
        })

        despawnEffect = Fx.none
        hitShake = 3f
        hitSound = ISounds.棱镜

        hitEffect = MultiEffect(Fx.hitBulletColor, ParticleEffect().apply {
          particles = 3
          lifetime = 25f
          sizeFrom = 3f
          sizeTo = 0f
          length = 12f
          interp = Interp.pow5Out
          sizeInterp = Interp.pow2In
          colorFrom = Color.valueOf("87CEEB")
          colorTo = Color.valueOf("C0ECFF")
        }, WaveEffect().apply {
          lifetime = 15f
          sizeFrom = 0f
          sizeTo = 64f
          strokeFrom = 6f
          strokeTo = 0f
          colorFrom = Color.valueOf("87CEEB")
          colorTo = Color.valueOf("C0ECFF")
        })

        bulletInterval = 3f
        intervalBullets = 2
        intervalSpread = 45f
        intervalRandomSpread = 0f

        intervalBullet = BasicBulletType(3f, 56f).apply {
          lifetime = 24f
          trailWidth = 1f
          trailLength = 6
          knockback = 4f
          status = IStatus.封冻
          statusDuration = 30f
          splashDamage = 48f
          splashDamageRadius = 4.5f
          trailColor = Color.valueOf("C0ECFF")
          backColor = Color.valueOf("87CEEB")
          frontColor = Color.valueOf("C0ECFF")
          hitSound = Sounds.shootLaser
          despawnEffect = Fx.none

          hitEffect = WaveEffect().apply {
            lifetime = 10f
            sizeTo = 6f
            strokeFrom = 4f
            colorFrom = Color.valueOf("87CEEB")
            colorTo = Color.valueOf("C0ECFF")
          }
        }
      }
    }

    addAmmoType(IItems.铈凝块) {
      BasicBulletType(6f, 42f).apply {
        lifetime = 50f
        width = 9f
        height = 16f
        shrinkY = 0f
        backColor = Color.valueOf("929DB5")
        frontColor = Color.valueOf("BFC8E2")
        hitColor = Color.valueOf("BFC8E2")
        trailColor = Color.valueOf("BFC8E2")
        trailLength = 12
        trailWidth = 2f
        trailChance = 1f

        trailEffect = ParticleEffect().apply {
          particles = 1
          lifetime = 20f
          length = 12f
          cone = 360f
          sizeFrom = 2.3f
          sizeTo = 0f
          interp = Interp.pow10Out
          sizeInterp = Interp.pow5In
          colorFrom = Color.valueOf("BFC8E2")
          colorTo = Color.valueOf("BFC8E2")
        }

        pierceCap = 2
        knockback = 8f
        status = IStatus.蚀骨
        statusDuration = 120f
        pierceBuilding = true
        splashDamage = 135f
        splashDamageRadius = 64f
        ammoMultiplier = 1f
        reloadMultiplier = 0.77f
        shootEffect = Fx.shootSmokeSmite

        smokeEffect = MultiEffect(ParticleEffect().apply {
          particles = 20
          lifetime = 12f
          line = true
          strokeFrom = 2f
          strokeTo = 0f
          lenFrom = 16f
          lenTo = 8f
          length = 60f
          colorFrom = Color.valueOf("929DB5")
          colorTo = Color.valueOf("BFC8E2")
          cone = 20f
        }, ParticleEffect().apply {
          particles = 12
          lifetime = 36f
          length = 60f
          sizeFrom = 4f
          cone = 20f
          interp = Interp.pow2Out
          sizeInterp = Interp.pow2In
          lightColor = Color.valueOf("929DB5")
          colorFrom = Color.valueOf("929DB5")
          colorTo = Color.valueOf("BFC8E2")
        })

        despawnEffect = Fx.none
        hitShake = 5f
        hitSound = ISounds.棱镜

        hitEffect = MultiEffect(Fx.hitBulletColor, ParticleEffect().apply {
          particles = 3
          lifetime = 25f
          sizeFrom = 3f
          sizeTo = 0f
          length = 12f
          interp = Interp.pow5Out
          sizeInterp = Interp.pow2In
          colorFrom = Color.valueOf("929DB5")
          colorTo = Color.valueOf("BFC8E2")
        }, WaveEffect().apply {
          lifetime = 15f
          sizeFrom = 0f
          sizeTo = 64f
          strokeFrom = 6f
          strokeTo = 0f
          colorFrom = Color.valueOf("929DB5")
          colorTo = Color.valueOf("BFC8E2")
        })

        bulletInterval = 3f
        intervalBullets = 2
        intervalSpread = 45f
        intervalRandomSpread = 0f

        intervalBullet = BasicBulletType(3f, 21f).apply {
          lifetime = 24f
          trailWidth = 1f
          trailLength = 6
          knockback = 4f
          status = IStatus.蚀骨
          statusDuration = 30f
          splashDamage = 45f
          splashDamageRadius = 16f
          trailColor = Color.valueOf("BFC8E2")
          backColor = Color.valueOf("929DB5")
          frontColor = Color.valueOf("BFC8E2")
          hitSound = Sounds.shootLaser
          despawnEffect = Fx.none

          hitEffect = WaveEffect().apply {
            lifetime = 10f
            sizeTo = 6f
            strokeFrom = 4f
            colorFrom = Color.valueOf("929DB5")
            colorTo = Color.valueOf("BFC8E2")
          }
        }
      }
    }
  }

  fun addAmmoType(item: Item, bulletType: () -> BasicBulletType) {
    val ammoTypes = bulletType.invoke()
    newAmmo(ammoTypes).setReloadAmount(ammoTypes.ammoMultiplier.toInt())
    consume?.apply {
      time(93f)
      item(item, 1)
      power(16.55f)
    }
  }
}
