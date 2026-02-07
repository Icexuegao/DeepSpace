package ice.content.block.turret

import arc.math.Interp
import ice.content.IItems
import ice.entities.bullet.LaserBulletType
import ice.library.util.toColor
import ice.ui.bundle.BaseBundle
import ice.world.content.blocks.abstractBlocks.IceBlock.Companion.requirements
import mindustry.content.StatusEffects
import mindustry.entities.effect.ParticleEffect
import mindustry.entities.part.RegionPart
import mindustry.entities.pattern.ShootSpread
import mindustry.gen.Sounds
import mindustry.type.Category
import mindustry.world.blocks.defense.turrets.PowerTurret
import mindustry.world.consumers.ConsumeCoolant
import mindustry.world.draw.DrawTurret

class Spectral:PowerTurret("spectral") {
  init{
    health = 1380
    size = 3
    recoil = 2f
    shootY = 4f
    range = 256f
    reload = 120f
    shootCone = 3f
    rotateSpeed = 5f
    recoilTime = 210f
    cooldownTime = 210f
    minWarmup = 0.9f
    shootWarmupSpeed = 0.08f
    squareSprite = false
    shoot = ShootSpread().apply {
      shots = 3
      shotDelay = 15f
    }
    shake = 2f
    consumePower(14f)
    consume(ConsumeCoolant(0.3f))
    liquidCapacity = 40f
    coolantMultiplier = 3f
    shootSound = Sounds.shootLaser
    shootType = LaserBulletType(135f).apply {
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
      ammoMultiplier = 1f
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
    }
    drawer = DrawTurret().apply {
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
    BaseBundle.bundle {
      desc(zh_CN, "光谱", "中型能量炮塔,可以快速向敌人发射高热激光")
    }
  }
}