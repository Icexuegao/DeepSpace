package ice.content.block.turret

import arc.math.Interp
import ice.content.IItems
import ice.entities.bullet.LaserBulletType
import mindustry.content.StatusEffects
import mindustry.entities.effect.ParticleEffect
import mindustry.entities.part.RegionPart
import mindustry.entities.pattern.ShootSpread
import mindustry.gen.Sounds
import mindustry.type.Category
import mindustry.type.Liquid
import singularity.world.blocks.turrets.SglTurret
import singularity.world.draw.DrawSglTurret
import universecore.util.toColor

class Spectral :SglTurret("turret_spectral") {
  init {
    localization {
      zh_CN {
        localizedName = "光谱"
        description = "中型能量炮塔,可以快速向敌人发射高热激光"
      }
    }
    health = 1380
    size = 3
    recoil = 2f
    shootY = 4f
    range = 256f
    shootCone = 3f
    rotateSpeed = 5f
    recoilTime = 210f
    cooldownTime = 210f
    squareSprite = false
    shoot = ShootSpread().apply {
      shots = 3
      shotDelay = 15f
    }
    shake = 2f
    liquidCapacity = 40f
    shootSound = Sounds.shootLaser
    drawers = DrawSglTurret().apply {
      parts.add(RegionPart("-side").apply {
        mirror = true
        moveX = 1f
        children.add(RegionPart("-top").apply {
          mirror = true
          moveX = 0.25f
          moveY = 1.75f
        })
      })
    }
    requirements(Category.turret, IItems.铜锭, 120, IItems.铬锭, 140, IItems.钍锭, 60, IItems.单晶硅, 120)
    setAmmo()
    newCoolant(1f, 0.3f, { l: Liquid? -> l!!.heatCapacity >= 0.4f && l.temperature <= 0.5f }, 0.25f, 20f)
  }

   fun setAmmo() {
    newAmmo(LaserBulletType(135f).apply {
      length = 256f
      shootEffect = ParticleEffect().apply {
        line = true
        particles = 12
        lifetime = 20f
        length = 45f
        cone = 30f
        lenFrom = 6f
        lenTo = 6f
        strokeFrom = 3f
        interp = Interp.fastSlow
        colorFrom = "FFDCD8".toColor()
        colorTo = "FF5845".toColor()
      }
      colors = arrayOf("D75B6E".toColor(), "E78F92".toColor(), "FFF0F0".toColor())
      status = StatusEffects.melting
      statusDuration = 30f
      hitEffect = ParticleEffect().apply {
        line = true
        particles = 10
        lifetime = 20f
        length = 75f
        cone = -360f
        lenFrom = 6f
        lenTo = 6f
        strokeFrom = 3f
        strokeTo = 0f
        lightColor = "FF5845".toColor()
        colorFrom = "FFDCD8".toColor()
        colorTo = "FF5845".toColor()
      }
    })
    consume?.apply {
      time(120f)
      power(14f)
    }
  }
}