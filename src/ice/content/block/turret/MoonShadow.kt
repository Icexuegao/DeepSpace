package ice.content.block.turret

import arc.graphics.Blending
import arc.graphics.Color
import ice.audio.ISounds
import ice.content.IItems
import ice.content.IStatus
import ice.entities.bullet.base.BasicBulletType
import ice.entities.effect.MultiEffect
import ice.ui.bundle.BaseBundle
import ice.world.content.blocks.abstractBlocks.IceBlock.Companion.requirements
import ice.world.draw.DrawMulti
import mindustry.content.Fx
import mindustry.entities.bullet.ShrapnelBulletType
import mindustry.entities.effect.ExplosionEffect
import mindustry.entities.effect.ParticleEffect
import mindustry.entities.part.DrawPart
import mindustry.entities.part.RegionPart
import mindustry.type.Category
import mindustry.world.blocks.defense.turrets.PowerTurret
import mindustry.world.draw.DrawParticles
import mindustry.world.draw.DrawTurret

class MoonShadow : PowerTurret("moonShadow") {
  init {
    BaseBundle.bundle {
      desc(zh_CN, "月隐", "中型能量炮塔,可以快速向敌人发射闪电能量团")
    }
    requirements(
      Category.turret, IItems.铬锭, 200, IItems.钍锭, 125, IItems.单晶硅, 160, IItems.钴钢, 85
    )
    health = 1470
    size = 3
    range = 275f
    reload = 120f
    cooldownTime = 210f
    heatColor = Color.valueOf("517D9D")
    recoil = 2f
    recoilTime = 210f
    shootY = 4f
    shootCone = 0.3f
    rotateSpeed = 5f
    minWarmup = 0.9f
    shootWarmupSpeed = 0.08f
    shoot.apply {
      firstShotDelay = 55f
    }
    consumePower(17f)
    consumeCoolant(0.3f)
    shake = 5f
    liquidCapacity = 40f
    coolantMultiplier = 5f
    moveWhileCharging = false
    chargeSound = ISounds.月隐蓄力
    shootSound = ISounds.月隐发射
    drawer = DrawMulti(DrawTurret().apply {
      parts.add(RegionPart().apply {
        suffix = "-side"
        mirror = true
        under = true
        moveX = 0.8f
        moveY = -0.5f
      })
      parts.add(RegionPart().apply {
        suffix = "-blade"
        mirror = true
        under = true
        moveY = -1f
        moveRot = -4f
        moves.add(DrawPart.PartMove().apply {
          progress = DrawPart.PartProgress.recoil
          rot = -8f
        })
        heatColor = Color.valueOf("517D9D")
      })
    }, DrawParticles().apply {
      color = Color.valueOf("517D9D")
      alpha = 0.6f
      particles = 30
      particleLife = 60f
      particleRad = 30f
      particleSize = 2f
      fadeMargin = 0.5f
      rotateScl = 0.5f
      blending = Blending.additive
    })
    shootType = BasicBulletType().apply {
      sprite = "circle-bullet"
      damage = 240f
      lifetime = 30f
      speed = 9f
      height = 13f
      width = 13f
      shrinkX = 0f
      shrinkY = 0f
      ammoMultiplier = 1f
      shootEffect = Fx.lancerLaserShoot
      frontColor = Color.valueOf("83C1ED")
      backColor = Color.valueOf("517D9D")
      trailColor = Color.valueOf("83C1ED")
      trailLength = 9
      trailWidth = 5f
      trailInterval = 10f
      trailRotation = true
      trailEffect = Fx.trailFade
      pierceCap = 2
      status = IStatus.湍能
      statusDuration = 120f
      splashDamage = 90f
      splashDamageRadius = 24f
      fragBullets = 8
      fragSpread = 45f
      fragRandomSpread = 0f
      chargeEffect = MultiEffect(
        Fx.lancerLaserCharge, Fx.lancerLaserChargeBegin
      )
      hitEffect = ExplosionEffect().apply {
        sparkColor = Color.valueOf("83C1ED")
        smokeColor = Color.valueOf("83C1ED")
        waveColor = Color.valueOf("83C1ED")
        waveStroke = 4f
        waveRad = 16f
        waveLife = 15f
        sparks = 5
        sparkRad = 16f
        sparkLen = 5f
        sparkStroke = 4f
      }
      despawnEffect = ExplosionEffect().apply {
        sparkColor = Color.valueOf("83C1ED")
        smokeColor = Color.valueOf("83C1ED")
        waveColor = Color.valueOf("83C1ED")
        waveStroke = 4f
        waveRad = 16f
        waveLife = 15f
        sparks = 5
        sparkRad = 16f
        sparkLen = 5f
        sparkStroke = 4f
      }
      fragBullet = ShrapnelBulletType().apply {
        damage = 45f
        status = IStatus.电链
        statusDuration = 60f
        width = 4f
        lifetime = 18f
        length = 32f
        fromColor = Color.valueOf("83C1ED")
        toColor = Color.valueOf("517D9D")
        shootEffect = ParticleEffect().apply {
          line = true
          particles = 10
          lifetime = 20f
          length = 75f
          cone = -360f
          lenFrom = 6f
          lenTo = 6f
          lightColor = Color.valueOf("517D9D")
          strokeFrom = 3f
          strokeTo = 0f
          colorFrom = Color.valueOf("83C1ED")
          colorTo = Color.valueOf("517D9D")
        }
        hitEffect = ParticleEffect().apply {
          line = true
          particles = 10
          lifetime = 20f
          length = 75f
          cone = -360f
          lenFrom = 6f
          lenTo = 6f
          lightColor = Color.valueOf("517D9D")
          strokeFrom = 3f
          strokeTo = 0f
          colorFrom = Color.valueOf("83C1ED")
          colorTo = Color.valueOf("517D9D")
        }
      }
    }
  }
}