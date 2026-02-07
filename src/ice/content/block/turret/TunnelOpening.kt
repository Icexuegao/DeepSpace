package ice.content.block.turret

import ice.content.IItems
import ice.content.IStatus
import ice.content.block.turret.TurretBullets.addAmmoType
import ice.entities.bullet.base.BasicBulletType
import ice.entities.bullet.base.BulletType
import ice.library.util.toColor
import ice.ui.bundle.BaseBundle
import ice.world.content.blocks.abstractBlocks.IceBlock.Companion.requirements
import mindustry.content.Fx
import mindustry.content.StatusEffects
import mindustry.entities.UnitSorts
import mindustry.entities.effect.ParticleEffect
import mindustry.entities.part.DrawPart
import mindustry.entities.part.HaloPart
import mindustry.entities.part.RegionPart
import mindustry.entities.pattern.ShootSpread
import mindustry.gen.Sounds
import mindustry.type.Category
import mindustry.world.blocks.defense.turrets.ItemTurret
import mindustry.world.consumers.ConsumeCoolant
import mindustry.world.draw.DrawTurret

class TunnelOpening:ItemTurret("tunnelOpening") {
  init{
    squareSprite = false
    health = 3450
    size = 5
    recoil = 3f
    shootY = 10f
    shake = 3.5f
    range = 264f
    reload = 378f
    shootCone = 15f
    maxAmmo = 40
    recoilTime = 192f
    cooldownTime = 270f
    rotateSpeed = 2f
    ammoPerShot = 10
    liquidCapacity = 40f
    coolantMultiplier = 0.4f
    minWarmup = 0.96f
    shootWarmupSpeed = 0.08f
    warmupMaintainTime = 300f
    shootSound = Sounds.shootScathe
    unitSort = UnitSorts.farthest
    shootEffect = Fx.bigShockwave
    ammoUseEffect = Fx.casing3Double
    consumePower(17f)
    consume(ConsumeCoolant(1.5f))
    drawer = DrawTurret().apply {
      parts.addAll(RegionPart("-shot").apply {
        progress = DrawPart.PartProgress.recoil
        moveY = -4.5f
        children.add(RegionPart("-shot-glow").apply {
          heatProgress = DrawPart.PartProgress.warmup
          drawRegion = false
          heatColor = "F03B0E".toColor()
        })
      }, RegionPart("-glow").apply {
        heatProgress = DrawPart.PartProgress.warmup
        drawRegion = false
        heatColor = "F03B0E".toColor()
      }, HaloPart().apply {
        tri = true
        y = -20f
        radius = 27f
        triLength = 0f
        triLengthTo = 3f
        haloRadius = 10f
        haloRotateSpeed = 0.5f
        color = "FF5845".toColor()
        layer = 110f
      }, HaloPart().apply {
        tri = true
        y = -20f
        radius = 3f
        triLength = 0f
        triLengthTo = 12f
        haloRadius = 11.5f
        shapeRotation = 150f
        haloRotateSpeed = 0.5f
        haloRotation = 30f
        color = "FF5845".toColor()
        layer = 110f
      }, HaloPart().apply {
        tri = true
        y = -20f
        radius = 3f
        triLength = 0f
        triLengthTo = 18f
        haloRadius = 12f
        shapeRotation = -15f
        haloRotateSpeed = 0.5f
        haloRotation = -135f
        color = "FF5845".toColor()
        layer = 110f
      })
    }
    requirements(Category.turret, IItems.铜锭, 1120, IItems.钴锭, 470, IItems.钍锭, 390, IItems.铬锭, 280, IItems.铱板, 225, IItems.爆炸化合物, 65)
    BaseBundle.bundle {
      desc(zh_CN, "隧穿", "向指定方位发射三道强劲的定向爆破束,并在到达极限距离后原路返回")
    }
    shoot = ShootSpread().apply {
      shots = 3
      spread = 15f
    }
    addAmmoType(IItems.铬锭) {
      BasicBulletType().apply {
        damage = 135f
        lifetime = 120f
        speed = 30f
        drag = 0.1f
        width = 14f
        height = 25f
        pierce = true
        collides = false
        pierceBuilding = true
        bulletInterval = 1f

        intervalBullet = BulletType().apply {
          damage = 0f
          hitShake = 2f
          despawnShake = 1f
          splashDamage = 45f
          splashDamageRadius = 20f
          scaledSplashDamage = true
          instantDisappear = true
          hitEffect = Fx.flakExplosion
          despawnEffect = Fx.flakExplosion
        }

        fragBullets = 1
        fragAngle = 180f
        fragVelocityMin = 1f
        fragRandomSpread = 0f

        fragBullet = BasicBulletType().apply {
          damage = 135f
          lifetime = 120f
          speed = 0.1f
          drag = -0.041f
          width = 14f
          height = 25f
          pierce = true
          collides = false
          pierceBuilding = true
          bulletInterval = 1f

          intervalBullet = BulletType().apply {
            damage = 0f
            hitShake = 2f
            despawnShake = 1f
            splashDamage = 45f
            splashDamageRadius = 20f
            scaledSplashDamage = true
            instantDisappear = true
            hitEffect = Fx.flakExplosion
            despawnEffect = Fx.flakExplosion
          }

          despawnSound = Sounds.shootPulsar

          despawnEffect = ParticleEffect().apply {
            particles = 15
            line = true
            strokeFrom = 3f
            strokeTo = 0f
            lenFrom = 10f
            lenTo = 0f
            length = 50f
            lifetime = 10f
            colorFrom = "FFE176".toColor()
            colorTo = "FFFFFF".toColor()
            cone = 60f
          }
        }
      }
    }
    addAmmoType(IItems.钴锭) {
      BasicBulletType().apply {
        damage = 185f
        lifetime = 120f
        speed = 30f
        drag = 0.1f
        width = 14f
        height = 25f
        ammoMultiplier = 1f
        reloadMultiplier = 1.4f
        pierce = true
        collides = false
        pierceBuilding = true
        bulletInterval = 1f

        intervalBullet = BulletType().apply {
          damage = 0f
          hitShake = 2f
          despawnShake = 1f
          splashDamage = 62f
          splashDamageRadius = 20f
          scaledSplashDamage = true
          instantDisappear = true
          hitEffect = Fx.flakExplosion
          despawnEffect = Fx.flakExplosion
        }

        fragBullets = 1
        fragAngle = 180f
        fragVelocityMin = 1f
        fragRandomSpread = 0f

        fragBullet = BasicBulletType().apply {
          damage = 185f
          lifetime = 120f
          speed = 0.1f
          drag = -0.041f
          width = 14f
          height = 25f
          pierce = true
          collides = false
          pierceBuilding = true
          bulletInterval = 1f

          intervalBullet = BulletType().apply {
            damage = 0f
            hitShake = 2f
            despawnShake = 1f
            splashDamage = 62f
            splashDamageRadius = 20f
            scaledSplashDamage = true
            instantDisappear = true
            hitEffect = Fx.flakExplosion
            despawnEffect = Fx.flakExplosion
          }

          despawnSound = Sounds.loopPulse

          despawnEffect = ParticleEffect().apply {
            particles = 15
            line = true
            strokeFrom = 3f
            strokeTo = 0f
            lenFrom = 10f
            lenTo = 0f
            length = 50f
            lifetime = 10f
            colorFrom = "FFE176".toColor()
            colorTo = "FFFFFF".toColor()
            cone = 60f
          }
        }
      }
    }
    addAmmoType(IItems.钍锭) {
      BasicBulletType().apply {
        damage = 235f
        lifetime = 120f
        speed = 30f
        drag = 0.085f
        width = 16f
        height = 25f
        pierce = true
        collides = false
        pierceBuilding = true
        rangeChange = 56f
        bulletInterval = 1f

        intervalBullet = BulletType().apply {
          damage = 0f
          hitShake = 2f
          despawnShake = 1f
          status = IStatus.衰变
          splashDamage = 78f
          splashDamageRadius = 20f
          scaledSplashDamage = true
          instantDisappear = true
          hitEffect = Fx.flakExplosion
          despawnEffect = Fx.flakExplosion
        }

        fragBullets = 1
        fragAngle = 180f
        fragVelocityMin = 1f
        fragRandomSpread = 0f

        fragBullet = BasicBulletType().apply {
          damage = 235f
          lifetime = 120f
          speed = 0.1f
          drag = -0.043f
          width = 16f
          height = 25f
          pierce = true
          collides = false
          pierceBuilding = true
          bulletInterval = 1f

          intervalBullet = BulletType().apply {
            damage = 0f
            hitShake = 2f
            despawnShake = 1f
            status = IStatus.衰变
            splashDamage = 78f
            splashDamageRadius = 20f
            scaledSplashDamage = true
            instantDisappear = true
            hitEffect = Fx.flakExplosion
            despawnEffect = Fx.flakExplosion
          }

          despawnSound = Sounds.shootPulsar

          despawnEffect = ParticleEffect().apply {
            particles = 15
            line = true
            strokeFrom = 4f
            strokeTo = 0f
            lenFrom = 10f
            lenTo = 0f
            length = 70f
            baseLength = 0f
            lifetime = 10f
            colorFrom = "FFE176".toColor()
            colorTo = "FFFFFF".toColor()
            cone = 60f
          }
        }
      }
    }
    addAmmoType(IItems.金锭) {
      BasicBulletType().apply {
        damage = 480f
        lifetime = 120f
        speed = 30f
        drag = 0.067f
        width = 10f
        height = 25f
        pierce = true
        collides = false
        pierceBuilding = true
        rangeChange = 144f
        ammoMultiplier = 1f
        bulletInterval = 1f

        intervalBullet = BulletType().apply {
          damage = 0f
          status = StatusEffects.shocked
          splashDamage = 160f
          splashDamageRadius = 20f
          scaledSplashDamage = true
          lightning = 3
          lightningLength = 4
          lightningDamage = 16f
          instantDisappear = true
          hitEffect = Fx.flakExplosion
          despawnEffect = Fx.flakExplosion
        }

        fragBullets = 1
        fragAngle = 180f
        fragVelocityMin = 1f
        fragRandomSpread = 0f

        fragBullet = BasicBulletType().apply {
          damage = 480f
          lifetime = 120f
          speed = 0.1f
          drag = -0.045f
          width = 10f
          height = 25f
          pierce = true
          collides = false
          pierceBuilding = true
          bulletInterval = 1f

          intervalBullet = BulletType().apply {
            damage = 0f
            status = StatusEffects.shocked
            splashDamage = 160f
            splashDamageRadius = 20f
            scaledSplashDamage = true
            lightning = 3
            lightningLength = 4
            lightningDamage = 16f
            instantDisappear = true
            hitEffect = Fx.flakExplosion
            despawnEffect = Fx.flakExplosion
          }

          despawnSound = Sounds.shootPulsar

          despawnEffect = ParticleEffect().apply {
            particles = 15
            line = true
            strokeFrom = 5f
            strokeTo = 0f
            lenFrom = 16f
            lenTo = 0f
            length = 100f
            lifetime = 10f
            colorFrom = "F3E979".toColor()
            colorTo = "FFFFFF".toColor()
            cone = 60f
          }
        }
      }
    }
    addAmmoType(IItems.铱板) {
      BasicBulletType().apply {
        damage = 420f
        lifetime = 120f
        speed = 30f
        drag = 0.075f
        width = 10f
        height = 25f
        pierce = true
        collides = false
        pierceBuilding = true
        rangeChange = 96f
        ammoMultiplier = 3f
        bulletInterval = 1f

        intervalBullet = BulletType().apply {
          damage = 0f
          hitShake = 2f
          despawnShake = 1f
          status = IStatus.衰变
          statusDuration = 60f
          splashDamage = 140f
          splashDamageRadius = 20f
          scaledSplashDamage = true
          instantDisappear = true
          hitEffect = Fx.flakExplosion
          despawnEffect = Fx.flakExplosion
        }

        fragBullets = 1
        fragAngle = 180f
        fragVelocityMin = 1f
        fragRandomSpread = 0f

        fragBullet = BasicBulletType().apply {
          damage = 420f
          lifetime = 120f
          speed = 0.1f
          drag = -0.044f
          width = 10f
          height = 25f
          pierce = true
          collides = false
          pierceBuilding = true
          bulletInterval = 1f

          intervalBullet = BulletType().apply {
            damage = 0f
            hitShake = 2f
            despawnShake = 1f
            status = IStatus.衰变
            statusDuration = 60f
            splashDamage = 140f
            splashDamageRadius = 20f
            scaledSplashDamage = true
            instantDisappear = true
            hitEffect = Fx.flakExplosion
            despawnEffect = Fx.flakExplosion
          }

          despawnSound = Sounds.shootPulsar

          despawnEffect = ParticleEffect().apply {
            particles = 15
            line = true
            strokeFrom = 5f
            strokeTo = 0f
            lenFrom = 16f
            lenTo = 0f
            length = 100f
            lifetime = 10f
            colorFrom = "FFE176".toColor()
            colorTo = "FFFFFF".toColor()
            cone = 60f
          }
        }
      }
    }
  }
}