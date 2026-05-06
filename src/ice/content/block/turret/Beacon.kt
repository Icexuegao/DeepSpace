package ice.content.block.turret

import arc.graphics.Color
import arc.graphics.g2d.Draw
import arc.graphics.g2d.Fill
import arc.math.Interp
import arc.math.Mathf
import arc.math.geom.Vec2
import arc.util.Time
import arc.util.Tmp
import ice.content.IItems
import ice.content.ILiquids
import ice.entities.bullet.BombBulletType
import ice.entities.bullet.PointBulletType
import ice.entities.effect.MultiEffect
import mindustry.content.Fx
import mindustry.entities.Effect
import mindustry.entities.effect.ParticleEffect
import mindustry.entities.effect.WaveEffect
import mindustry.entities.part.RegionPart
import mindustry.gen.Bullet
import mindustry.gen.Sounds
import mindustry.type.Category
import singularity.world.blocks.turrets.SglTurret
import singularity.world.draw.DrawSglTurret
import kotlin.math.sin

class Beacon :SglTurret("turret_beacon") {
  val randPosBullet = BombBulletType().apply {
    sprite = "star"
    damage = 0f
    lifetime = 150f
    speed = 4f
    spin = 6f
    shrinkX = 0.4f
    shrinkY = 0.4f
    width = 80f
    height = 80f
    trailLength = 32
    trailWidth = 6f
    trailSinScl = 2.25f
    trailSinMag = 0.75f
    trailInterp = Interp.swing
    splashDamage = 580f
    splashDamageRadius = 80f
    buildingDamageMultiplier = 0.05f
    backColor = Color.valueOf("FFA05C")
    frontColor = Color.valueOf("FFA05C")
    hitShake = 4f
    hitSound = Sounds.explosionPlasmaSmall
    hitEffect = MultiEffect().apply {
      effects = arrayOf(ParticleEffect().apply {
        lifetime = 30f
        particles = 13
        line = true
        strokeFrom = 10f
        strokeTo = 0f
        lenFrom = 45f
        lenTo = 0f
        cone = 360f
        length = 203f
        baseLength = 30f
        colorFrom = Color.valueOf("FFA05C")
        colorTo = Color.valueOf("FFA05C")
      }, ParticleEffect().apply {
        lifetime = 20f
        particles = 7
        line = true
        strokeFrom = 10f
        strokeTo = 8f
        lenFrom = 45f
        lenTo = 0f
        cone = 360f
        length = 273f
        baseLength = 30f
        interp = Interp.pow10Out
        colorFrom = Color.valueOf("FFA05C")
        colorTo = Color.valueOf("FFA05C")
      }, ParticleEffect().apply {
        lifetime = 90f
        particles = 13
        sizeFrom = 10f
        sizeTo = 0f
        cone = 360f
        length = 163f
        baseLength = 3f
        interp = Interp.pow5Out
        sizeInterp = Interp.pow10In
        colorFrom = Color.valueOf("FFA05C")
        colorTo = Color.valueOf("FFA05C")
      }, ParticleEffect().apply {
        lifetime = 90f
        particles = 1
        sizeFrom = 80f
        sizeTo = 0f
        cone = 360f
        length = 0f
        baseLength = 0f
        colorFrom = Color.valueOf("FFA05CC0")
        colorTo = Color.valueOf("FFA05C00")
      }, WaveEffect().apply {
        lifetime = 30f
        sizeFrom = 0f
        sizeTo = 160f
        strokeFrom = 6f
        strokeTo = 0f
        interp = Interp.slope
        colorFrom = Color.valueOf("FFA05C")
        colorTo = Color.valueOf("FFA05C")
      })
    }
    despawnEffect = WaveEffect().apply {
      lifetime = 20f
      sizeFrom = 8f
      sizeTo = 30f
      strokeFrom = 5f
      strokeTo = 0f
      colorFrom = Color.valueOf("FFA05C")
      colorTo = Color.valueOf("FFA05C")
    }
  }

  init {
    localization {
      zh_CN {
        localizedName = "信标"
        description = "持续引导聚爆能量弹坠向信标标记处,毁灭范围内所有敌方目标"
      }
    }
    requirementPairs(
      Category.turret,

      IItems.铜锭 to 4800,

      IItems.钴锭 to 3200,

      IItems.铱板 to 2100,

      IItems.导能回路 to 2160,

      IItems.陶钢 to 1600,

      IItems.暮光合金 to 1200,

      IItems.絮凝剂 to 850,

      IItems.肃正协议 to 1
    )
    health = 10800
    size = 7
    range = 1600f
    shake = 8f
    recoil = 5f
    warmupSpeed= 0.01f
    rotateSpeed = 0.5f
    recoilTime = 2400f
    cooldownTime = 2400f
    itemCapacity = 1
    liquidCapacity = 120f
    targetAir = false
    canOverdrive = false
    moveWhileCharging = false
    smokeEffect = Fx.bigShockwave
    chargeSound = Sounds.shootLaser
    shootSound = Sounds.explosionPlasmaSmall
    shoot.firstShotDelay = 75f
    shootEffect = ParticleEffect().apply {
      particles = 1
      sizeFrom = 20f
      sizeTo = 0f
      length = 0f
      baseLength = 0f
      lifetime = 95f
      colorFrom = Color.valueOf("D86E56FF")
      colorTo = Color.valueOf("FFA05C")
      cone = 0f
    }

    drawers = DrawSglTurret().apply {
      parts.add(RegionPart())
      parts.add(RegionPart().apply {
        suffix = "-barrel"
        under = true
        moveY = 11.5f
        heatColor = Color.valueOf("F03B0E")
      })
      parts.add(RegionPart().apply {
        suffix = "-side"
        mirror = true
        under = true
        moveX = 2.25f
        moveY = 0.75f
        heatColor = Color.valueOf("F03B0E")
        children.add(RegionPart().apply {
          suffix = "-top"
          mirror = true
          under = true
          moveX = 7.25f
          moveY = 7f
          moveRot = 90f
          heatColor = Color.valueOf("F03B0E")
        })
      })
    }
    setAmmo()
  }

   fun setAmmo() {
    newAmmo(PointBulletType().apply {
      damage = 0f
      lifetime = 16f
      speed = 100f
      smokeEffect = Fx.smokeCloud
      chargeEffect = MultiEffect().apply {
        lifetime = 80f
        effects = arrayOf(ParticleEffect().apply {
          particles = 1
          lenFrom = 25f
          lenTo = 0f
          strokeFrom = 4f
          strokeTo = 4f
          line = true
          length = 150f
          baseLength = -150f
          lifetime = 70f
          colorFrom = Color.valueOf("FFA05C")
          colorTo = Color.valueOf("FFA05C")
          cone = 360f
        }, ParticleEffect().apply {
          particles = 1
          sizeFrom = 0f
          sizeTo = 30f
          length = 0f
          baseLength = 0f
          lifetime = 76f
          colorFrom = Color.valueOf("FFA05C")
          colorTo = Color.valueOf("FFA05C")
          cone = 360f
        })
      }
      trailSpacing = 10f
      trailInterval = 30f
      trailEffect = ParticleEffect().apply {
        particles = 1
        line = true
        lifetime = 90f
        length = 1f
        baseLength = 1f
        lenFrom = 10f
        lenTo = 10f
        strokeFrom = 16f
        strokeTo = 0f
        colorFrom = Color.valueOf("FFA05C")
        colorTo = Color.valueOf("FFA05C")
        cone = 0f
      }
      despawnEffect = ParticleEffect().apply {
        particles = 1
        sizeFrom = 50f
        sizeTo = 0f
        length = 0f
        baseLength = 0f
        lifetime = 140f
        colorFrom = Color.valueOf("FFA05C")
        colorTo = Color.valueOf("FFA05C")
        cone = 0f
      }
      hitEffect = WaveEffect().apply {
        lifetime = 135f
        sizeFrom = 150f
        sizeTo = 0f
        strokeFrom = 5f
        strokeTo = 0f
        interp = Interp.fastSlow
        colorFrom = Color.valueOf("FFA05C")
        colorTo = Color.valueOf("FFA05C")
      }
      fragBullets = 1
      fragBullet = object :BombBulletType(1250f, 120f, "star") {
        fun randomPointInCircle(centerX: Float, centerY: Float, radius: Float): Vec2 {
          val angle = Mathf.random(0f, Mathf.PI2)
          val len = Mathf.sqrt(Mathf.random(1f)) * radius
          return Tmp.v1.set(
            centerX + Mathf.cos(angle) * len, centerY + Mathf.sin(angle) * len
          )
        }

        override fun updateBulletInterval(b: Bullet) {
          if (intervalBullet != null && b.time >= intervalDelay && b.timer.get(2, bulletInterval)) {
            val ang = b.rotation()
            for(i in 0..<intervalBullets) {
              val randomPointInCircle = randomPointInCircle(b.x, b.y, 480f)
              intervalBullet.create(
                b,
                randomPointInCircle.x,
                randomPointInCircle.y,
                ang + Mathf.range(intervalRandomSpread) + intervalAngle + ((i - (intervalBullets - 1f) / 2f) * intervalSpread)
              )
            }
          }
        }

        val colorFrom2 = Color.valueOf("FF000020")
        val colorFrom1 = Color.valueOf("FF000020").sub(0.4f, 0.4f, 0.4f)
        override fun draw(b: Bullet) {
          super.draw(b)
          Draw.color(colorFrom1, colorFrom2, sin(Time.time / 15f))
          Draw.alpha(0.3f)
          Fill.circle(b.x, b.y, 480f)
          Draw.reset()
        }
      }.apply {
        drawSize = 960f
        damage = 0f
        lifetime = 1800f
        speed = 0f
        height = 60f
        width = 60f
        spin = 2f
        shrinkY = 0f
        shrinkX = 0f
        backColor = Color.valueOf("FFA05C")
        frontColor = Color.valueOf("FFA05C")
        collides = false
        hittable = false
        absorbable = false
        reflectable = false
        bulletInterval = 6f
        intervalBullets = 2
        intervalBullet = randPosBullet
        hitEffect = Effect(1800f) {

        }.apply {

        }
      }
    }).setReloadAmount(1)
    consume?.apply {
      time(2700f)
      power(27f)
      liquid(ILiquids.急冻液, 1f)
    }
  }
}