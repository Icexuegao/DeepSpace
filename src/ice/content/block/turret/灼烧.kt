package ice.content.block.turret

import arc.graphics.Color
import arc.math.Interp
import ice.audio.ISounds
import ice.content.IItems
import ice.content.IStatus
import ice.core.IFiles.appendModName
import ice.entities.bullet.LaserBulletType
import ice.entities.effect.MultiEffect
import mindustry.entities.bullet.ShrapnelBulletType
import mindustry.entities.effect.ParticleEffect
import mindustry.entities.effect.WaveEffect
import mindustry.gen.Sounds
import mindustry.type.Category
import mindustry.type.Liquid
import singularity.world.blocks.turrets.SglTurret

class 灼烧 :SglTurret("turret_scorch") {
  init {
    localization {
      zh_CN {
        localizedName = "灼烧"
        description = "大型激光炮塔,大量质能反应使能量到达临界点,然后以毁灭性激光的形式释放而出"
      }
    }

    size = 4
    health = 2160
    range = 500f
    shake = 4f
    shootY = 4f
    recoil = 4f
    recoilTime = 180f
    rotateSpeed = 3f
    cooldownTime = 420f
    outlineColor = Color.valueOf("313131")
    moveWhileCharging = false
    itemCapacity = 1

    shootSound = ISounds.灼烧
    chargeSound = Sounds.chargeLancer

    newCoolant(1f, 0.4f, { l: Liquid? -> l!!.heatCapacity >= 0.4f && l.temperature <= 0.5f }, 0.25f, 20f)

    requirements(
      Category.turret, IItems.铬锭, 1200, IItems.铱板, 625, IItems.钴锭, 425, IItems.导能回路, 325, IItems.陶钢, 225
    )

    setAmmo()
  }

  fun setAmmo() {
    newAmmo(object :LaserBulletType(1750f) {
      init {
        lifetime = 30f
        length = 500f
        width = 70f
        ammoMultiplier = 1f

        colors = arrayOf(
          Color.valueOf("FF5845"), Color.valueOf("FFC1BB"), Color.valueOf("FFDCD8")
        )

        sideAngle = 30f
        sideWidth = 1.5f
        sideLength = 100f

        lightningSpacing = 30f
        lightningLength = 10
        lightningDelay = 0.6f
        lightningLengthRand = 1
        lightningDamage = 25f
        lightningAngleRand = 0.1f
        lightningColor = Color.valueOf("FF5845")
        lightColor = Color.valueOf("FF5845")

        status = IStatus.熔融
        statusDuration = 300f
        largeHit = true

        chargeEffect = MultiEffect().apply {
          this.lifetime = 80f
          effects = arrayOf(ParticleEffect().apply {
            particles = 1
            sizeFrom = 2f
            sizeTo = 38f
            length = 0f
            spin = 11f
            this.lifetime = 80f
            layer = 109f
            region = "star".appendModName()
            colorFrom = Color.valueOf("FF5845")
            colorTo = Color.valueOf("FF5845")
            cone = 360f
          }, WaveEffect().apply {
            this.lifetime = 70f
            sizeFrom = 90f
            sizeTo = 0f
            interp = Interp.pow5In
            strokeFrom = 0f
            strokeTo = 8f
            lightColor = Color.valueOf("FFDCD8")
            colorFrom = Color.valueOf("FFDCD8")
            colorTo = Color.valueOf("FF5845")
          }, WaveEffect().apply {
            this.lifetime = 70f
            sizeFrom = 70f
            sizeTo = 0f
            interp = Interp.pow5In
            strokeFrom = 0f
            strokeTo = 8f
            lightColor = Color.valueOf("FFDCD8")
            colorFrom = Color.valueOf("FFDCD8")
            colorTo = Color.valueOf("FF5845")
          })
        }

        shootEffect = MultiEffect().apply {
          effects = arrayOf(ParticleEffect().apply {
            line = true
            particles = 15
            offset = 20f
            this.lifetime = 30f
            length = 100f
            baseLength = -15f
            cone = -360f
            lenFrom = 10f
            lenTo = 10f
            colorFrom = Color.valueOf("FF5845")
            colorTo = Color.valueOf("FFDCD8")
          }, WaveEffect().apply {
            this.lifetime = 25f
            sizeFrom = 0f
            sizeTo = 75f
            strokeFrom = 4f
            strokeTo = 0f
            lightColor = Color.valueOf("FF5845")
            colorFrom = Color.valueOf("FF5845")
            colorTo = Color.valueOf("FFDCD8")
          }, ParticleEffect().apply {
            particles = 1
            sizeFrom = 35f
            sizeTo = 0f
            length = 0f
            interp = Interp.swingIn
            this.lifetime = 25f
            region = "star".appendModName()
            lightColor = Color.valueOf("FF5845")
            colorFrom = Color.valueOf("FF5845")
            colorTo = Color.valueOf("FFDCD8")
          }, ParticleEffect().apply {
            particles = 1
            sizeFrom = 12f
            sizeTo = 0f
            length = 0f
            interp = Interp.swingIn
            this.lifetime = 25f
            region = "star".appendModName()
            lightColor = Color.valueOf("FF5845")
            colorFrom = Color.valueOf("FF5845")
            colorTo = Color.valueOf("FFC1BB")
          })
        }

        lightningType = ShrapnelBulletType().apply {
          damage = 225f
          width = 6.5f
          this.lifetime = 20f
          length = 10f
          serrations = 0
          fromColor = Color.valueOf("FF5845")
          toColor = Color.valueOf("FF5845")

          hitEffect = ParticleEffect().apply {
            line = true
            particles = 6
            offset = 20f
            this.lifetime = 30f
            length = 65f
            baseLength = -15f
            cone = -360f
            lenFrom = 5f
            lenTo = 0f
            colorFrom = Color.valueOf("FF5845")
            colorTo = Color.valueOf("DE4136")
          }

          status = IStatus.熔融
          statusDuration = 90f
        }

        hitEffect = ParticleEffect().apply {
          line = true
          particles = 15
          offset = 20f
          this.lifetime = 30f
          length = 65f
          baseLength = -15f
          cone = -360f
          lenFrom = 9f
          lenTo = 0f
          colorFrom = Color.valueOf("FF5845")
          colorTo = Color.valueOf("DE4136")
        }
      }
    }).apply {
      consume?.apply {
        time(360f)
        power(33.7f)
      }
    }
  }
}
