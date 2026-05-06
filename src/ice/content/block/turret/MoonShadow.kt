package ice.content.block.turret

import arc.func.Func
import arc.graphics.Blending
import arc.graphics.Color
import arc.graphics.g2d.Draw
import arc.graphics.g2d.Fill
import arc.math.Angles
import arc.math.Interp
import arc.math.Interp.PowIn
import arc.math.Mathf
import arc.math.geom.Vec2
import arc.util.Time
import ice.audio.ISounds
import ice.content.IItems
import ice.content.IStatus
import ice.entities.bullet.base.BasicBulletType
import ice.entities.effect.MultiEffect
import mindustry.content.Fx
import mindustry.entities.bullet.ShrapnelBulletType
import mindustry.entities.effect.ExplosionEffect
import mindustry.entities.effect.ParticleEffect
import mindustry.entities.part.DrawPart
import mindustry.entities.part.RegionPart
import mindustry.gen.Building
import mindustry.type.Category
import mindustry.world.draw.DrawBlock
import singularity.world.blocks.turrets.SglTurret
import singularity.world.draw.DrawSglTurret
import universecore.world.draw.DrawMulti

class MoonShadow :SglTurret("moonShadow") {
  init {
    localization {
      zh_CN {
        localizedName = "月隐"
        description = "中型能量炮塔,可以快速向敌人发射闪电能量团"
      }
    }
    requirements(
      Category.turret, IItems.铬锭, 200, IItems.钍锭, 125, IItems.单晶硅, 160, IItems.钴钢, 85
    )
    size = 3
    range = 275f
    recoil = 2f
    shootY = 4f
    health = 1470
    shootCone = 0.3f
    cooldownTime = 210f
    heatColor = Color.valueOf("517D9D")

    rotateSpeed = 5f
    warmupSpeed = 0.08f
    shoot.apply {
      firstShotDelay = 55f
    }
    shake = 5f
    liquidCapacity = 40f
    moveWhileCharging = false
    chargeSound = ISounds.月隐蓄力
    shootSound = ISounds.月隐发射
    drawers = DrawMulti(DrawSglTurret().apply {
      parts.add(RegionPart())
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
      val vec2 = Vec2(5f,0f)
      vecl= Func { build ->
        vec2.setAngle(build.rotationu)
      }
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
    setAmmo()
  }

   fun setAmmo() {
    newAmmo(BasicBulletType(sprite = "circle-bullet").apply {
      damage = 240f
      lifetime = 30f
      speed = 9f
      height = 13f
      width = 13f
      shrinkX = 0f
      shrinkY = 0f
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
      val effect = ExplosionEffect().apply {
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
      hitEffect = effect
      despawnEffect = effect
      fragBullet = ShrapnelBulletType().apply {
        damage = 45f
        status = IStatus.电链
        statusDuration = 60f
        width = 4f
        lifetime = 18f
        length = 32f
        fromColor = Color.valueOf("83C1ED")
        toColor = Color.valueOf("517D9D")
        val particleEffect = ParticleEffect().apply {
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
        shootEffect = particleEffect
        hitEffect = particleEffect
      }
    })
    consume?.apply {
      time(120f)
      power(17f)
    }
  }

  class DrawParticles :DrawBlock() {
    var color: Color = Color.valueOf("f2d585")

    var sides: Int = 12
    var x: Float = 0f
    var y: Float = 0f
    var alpha: Float = 0.5f
    var particles: Int = 30
    var particleRotation: Float = 0f
    var particleLife: Float = 70f
    var particleRad: Float = 7f
    var particleSize: Float = 3f
    var fadeMargin: Float = 0.4f
    var rotateScl: Float = 3f
    var reverse: Boolean = false
    var poly: Boolean = false
    var particleInterp: Interp = PowIn(1.5f)
    var particleSizeInterp: Interp = Interp.slope
    var blending: Blending = Blending.normal
    var vecl: Func<SglTurretBuild, Vec2>?=null

    override fun draw(build: Building) {
      build as SglTurretBuild
      if (build.charging()) {
        val a = alpha * build.warmup()

        Draw.blend(blending)
        Draw.color(color)

        val base = Time.time / particleLife
        rand.setSeed(build.id.toLong())
        for(i in 0..<particles) {
          var fin = (rand.random(2f) + base) % 1f
          if (reverse) fin = 1f - fin
          val fout = 1f - fin
          val angle = rand.random(360f) + (Time.time / rotateScl) % 360f
          val len = particleRad * particleInterp.apply(fout)

          Draw.alpha(a * (1f - Mathf.curve(fin, 1f - fadeMargin)))
          if (poly) {
            Fill.poly(
              build.x + vecl!!.get(build).x + Angles.trnsx(angle, len),
              build.y + vecl!!.get(build).y + Angles.trnsy(angle, len),
              sides,
              particleSize * particleSizeInterp.apply(fin) * build.warmup(),
              particleRotation
            )
          } else {
            Fill.circle(
              build.x + vecl!!.get(build).x + Angles.trnsx(angle, len),
              build.y + vecl!!.get(build).y + Angles.trnsy(angle, len),
              particleSize * particleSizeInterp.apply(fin) * build.warmup()
            )
          }
        }

        Draw.blend()
        Draw.reset()
      }
    }
  }
}