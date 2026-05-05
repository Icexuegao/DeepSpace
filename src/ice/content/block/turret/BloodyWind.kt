package ice.content.block.turret

import ice.audio.ISounds
import ice.content.IItems
import ice.content.IStatus
import ice.entities.bullet.base.BasicBulletType
import ice.entities.effect.MultiEffect
import mindustry.content.Fx
import mindustry.entities.effect.ParticleEffect
import mindustry.entities.effect.WaveEffect
import mindustry.entities.part.DrawPart
import mindustry.entities.part.HoverPart
import mindustry.entities.part.RegionPart
import mindustry.entities.pattern.ShootBarrel
import mindustry.type.Category
import mindustry.type.Liquid
import singularity.world.blocks.turrets.SglTurret
import singularity.world.draw.DrawSglTurret
import universecore.util.toColor

class BloodyWind :SglTurret("turret_bloodyWind") {
  init {
    localization {
      zh_CN {
        localizedName = "腥风"
        description = "改进型四联速射粒子炮,向敌人发射高热的粒子束\n为了更强的电热转换回路拆除了部分气冷系统,使用液体时冷却效果更佳"
      }
    }
    squareSprite = false
    health = 6400
    size = 6
    armor = 8f
    range = 672f
    shake = 3f
    recoil = 4f
    recoils = 4
    shootY = 20f
    recoilTime = 6f
    inaccuracy = 3f
    shootCone = 60f
    rotateSpeed = 4f
    targetInterval = 1f
    cooldownTime = 20f
    shootSound = ISounds.速射
    liquidCapacity = 30f
    shoot = ShootBarrel().apply {
      barrels = floatArrayOf(-5.5f, 0f, 0f, 5.5f, 0f, 0f, -16f, 0f, 0f, 16f, 0f, 0f)
    }
    requirements(Category.turret, IItems.铬锭, 2200, IItems.石英玻璃, 570, IItems.铱板, 1200, IItems.导能回路, 625, IItems.钴钢, 825)
    drawers = DrawSglTurret().apply {
      parts.add(RegionPart())
      parts.addAll(RegionPart("-l").apply {
        under = true
        recoilIndex = 0
        heatColor = "F03B0E".toColor()
        heatProgress = DrawPart.PartProgress.recoil
        moves.add(DrawPart.PartMove().apply {
          progress = DrawPart.PartProgress.recoil
          y = -4f
        })
      }, RegionPart("-r").apply {
        under = true
        recoilIndex = 1
        heatColor = "F03B0E".toColor()
        heatProgress = DrawPart.PartProgress.recoil
        moves.add(DrawPart.PartMove().apply {
          progress = DrawPart.PartProgress.recoil
          y = -4f
        })
      }, RegionPart("-ll").apply {
        under = true
        recoilIndex = 2
        heatColor = "F03B0E".toColor()
        heatProgress = DrawPart.PartProgress.recoil
        moves.add(DrawPart.PartMove().apply {
          progress = DrawPart.PartProgress.recoil
          y = -4f
        })
      }, RegionPart("-rr").apply {
        under = true
        recoilIndex = 3
        heatColor = "F03B0E".toColor()
        heatProgress = DrawPart.PartProgress.recoil
        moves.add(DrawPart.PartMove().apply {
          progress = DrawPart.PartProgress.recoil
          y = -4f
        })
      }, HoverPart().apply {
        color = "FF5845".toColor()
        phase = 120f
        circles = 3
        stroke = 1f
        layer = 100f
      })
    }
    newCoolant(1f, 0.4f, { l: Liquid? -> l!!.heatCapacity >= 0.4f && l.temperature <= 0.5f }, 0.25f, 20f)
    setAmmo()
  }

  override fun setAmmo() {
    newAmmo(BasicBulletType().apply {
      damage = 121f
      lifetime = 33.6f
      speed = 20f
      width = 5f
      height = 75f
      pierce = true
      knockback = 1f
      status = IStatus.熔融
      statusDuration = 120f
      ammoMultiplier = 1f
      trailLength = 2
      trailWidth = 1.2f
      trailColor = "FF5845".toColor()
      frontColor = "FF8663".toColor()
      backColor = "FF5845".toColor()
      hitColor = "FF8663".toColor()

      shootEffect = WaveEffect().apply {
        lifetime = 6f
        sizeFrom = 1f
        sizeTo = 10f
        strokeFrom = 1.3f
        colorFrom = "FF5845".toColor()
        colorTo = "FF8663".toColor()
      }

      smokeEffect = Fx.shootBigSmoke

      hitEffect = MultiEffect(WaveEffect().apply {
        lifetime = 10f
        sizeTo = 6f
        strokeFrom = 4f
        colorFrom = "FF5845".toColor()
        colorTo = "FF8663".toColor()
      }, ParticleEffect().apply {
        line = true
        particles = 8
        lifetime = 10f
        length = 50f
        strokeFrom = 3f
        lenFrom = 10f
        lenTo = 4f
        cone = 60f
        colorFrom = "FF5845".toColor()
        colorTo = "FF8663".toColor()
      })

      despawnEffect = WaveEffect().apply {
        lifetime = 10f
        sizeTo = 6f
        strokeFrom = 4f
        colorFrom = "FF5845".toColor()
        colorTo = "FF8663".toColor()
      }
    })
    consume?.apply {
      time(1.5f)
      power(53f)
    }
  }
}