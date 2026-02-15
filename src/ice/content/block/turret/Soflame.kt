package ice.content.block.turret

import arc.Core
import arc.graphics.Color
import arc.graphics.g2d.Draw
import arc.graphics.g2d.Fill
import arc.graphics.g2d.Lines
import arc.math.Mathf
import arc.util.Time
import arc.util.Tmp
import ice.content.IItems
import ice.entities.effect.MultiEffect
import ice.ui.bundle.BaseBundle.Companion.bundle
import mindustry.content.Fx
import mindustry.content.Items
import mindustry.entities.Mover
import mindustry.entities.effect.WaveEffect
import mindustry.entities.part.HaloPart
import mindustry.entities.part.RegionPart
import mindustry.entities.part.ShapePart
import mindustry.gen.Building
import mindustry.gen.Bullet
import mindustry.gen.Sounds
import mindustry.graphics.Layer
import mindustry.graphics.Pal
import mindustry.type.Category
import mindustry.type.ItemStack
import singularity.graphic.SglDraw
import singularity.ui.UIUtils
import singularity.util.MathTransform
import singularity.world.SglFx
import singularity.world.SglUnitSorts
import singularity.world.blocks.turrets.HeatBulletType
import singularity.world.blocks.turrets.SglTurret
import singularity.world.draw.DrawSglTurret
import singularity.world.draw.part.CustomPart
import singularity.world.particles.SglParticleModels

class Soflame : SglTurret("soflame") {
  init {
    bundle {
      desc(zh_CN, "阳炎", "将能量聚集到“太阳分子”上,直到能量足够高时发射出去,极热的物质云会留下灼热的轨迹,并在碰撞时爆炸,将目标化为灰烬")
    }
    requirements(
      Category.turret, ItemStack.with(
        IItems.强化合金, 150, IItems.铝锭, 180, IItems.FEX水晶, 140, IItems.充能FEX水晶, 120, IItems.气凝胶, 180, IItems.铱锭, 60, Items.surgeAlloy, 120, Items.phaseFabric, 100
      )
    )
    size = 5
    recoil = 4f
    recoilTime = 120f
    rotateSpeed = 1.5f
    shootCone = 3f
    warmupSpeed = 0.018f
    fireWarmupThreshold = 0.9f
    linearWarmup = false
    range = 360f
    shootY = 8f
    shake = 8f

    energyCapacity = 4096f
    basicPotentialEnergy = 2048f

    shootEffect = SglFx.shootRail
    shootSound = Sounds.shootSmite
    smokeEffect = Fx.shootSmokeSmite

    unitSort = SglUnitSorts.denser
    val subBullet: mindustry.entities.bullet.BulletType = object : HeatBulletType() {
      init {
        speed = 4f
        lifetime = 90f

        damage = 0f
        splashDamage = 90f
        splashDamageRadius = 8f

        meltDownTime = 30f
        melDamageScl = 0.5f
        maxExDamage = 150f

        trailColor = Pal.lighterOrange
        hitColor = Pal.lighterOrange
        trailEffect = SglFx.glowParticle
        trailChance = 0.1f
        trailRotation = true

        hitEffect = MultiEffect(
          object : WaveEffect() {
            init {
              colorFrom = Pal.lighterOrange
              colorTo = Color.white
              lifetime = 12f
              sizeTo = 28f
              strokeFrom = 6f
              strokeTo = 0.3f
            }
          }, Fx.circleColorSpark
        )
        despawnEffect = Fx.absorb
        despawnHit = true

        trailWidth = 2f
        trailLength = 24
      }

      override fun draw(b: Bullet) {
        super.draw(b)
        Draw.color(hitColor)
        Fill.circle(b.x, b.y, 3f)
      }
    }
    newAmmo(object : HeatBulletType() {
      init {
        damage = 260f
        splashDamage = 540f
        splashDamageRadius = 32f
        hitSize = 5f
        speed = 4f
        lifetime = 90f

        hitShake = 14f

        hitColor = Pal.lighterOrange
        trailColor = Pal.lighterOrange

        hitSound = Sounds.explosion
        hitSoundVolume = 4f

        trailEffect = SglFx.trailParticle
        trailChance = 0.1f

        hitEffect = MultiEffect(
          object : WaveEffect() {
            init {
              colorTo = Pal.lighterOrange
              colorFrom = colorTo
              lifetime = 12f
              sizeTo = 50f
              strokeFrom = 7f
              strokeTo = 0.3f
            }
          }, SglFx.explodeImpWaveLarge, SglFx.impactBubble
        )

        meltDownTime = 90f
        melDamageScl = 0.3f
      }

      override fun init(b: Bullet) {
        super.init(b)
        val p = SglParticleModels.heatBulletTrail.create(b.x, b.y, Pal.lighterOrange, 0f, 0f, 5f)
        p.owner = b
        p.bullet = SglParticleModels.defHeatTrailHitter.create(b, b.x, b.y, b.rotation())

        Tmp.v1.set(1f, 0f).setAngle(b.rotation())
        for (i in 0..3) {
          val off = Mathf.random(0f, Mathf.PI2)
          val scl = Mathf.random(3f, 6f)
          val x = b.x
          val y = b.y
          Time.run((i * 5).toFloat()) {
            for (sign in Mathf.signs) {
              subBullet.create(b, x, y, b.rotation()).mover = Mover { e: Bullet? -> e!!.moveRelative(0f, Mathf.sin(e.time + off, scl, ((1 + i) * sign).toFloat())) }
            }
          }
        }
      }
    }) { t, b: mindustry.entities.bullet.BulletType? ->
      t!!.table { child ->
        child!!.left().add(Core.bundle.format("infos.shots", 6)).color(Color.lightGray).left()
        UIUtils.buildAmmo(child, subBullet)
      }.padLeft(15f)
    }
    consume!!.time(180f)
    consume!!.energy(5f)

    draw = object : DrawSglTurret(object : RegionPart("_blade") {
      init {
        progress = PartProgress.warmup
        heatProgress = PartProgress.warmup
        mirror = true
        moveX = 4f
        heatColor = Pal.lightishOrange

        moves.add(PartMove(PartProgress.recoil, 0f, -2f, 0f))
      }
    }, object : RegionPart("_body") {
      init {
        mirror = false
        heatProgress = PartProgress.warmup
        heatColor = Pal.lightishOrange
      }
    }, object : ShapePart() {
      init {
        progress = PartProgress.warmup
        y = shootY
        circle = true
        radius = 0f
        radiusTo = 4f
        layer = Layer.effect
      }
    }, object : CustomPart() {
      init {
        progress = PartProgress.warmup
        y = shootY
        layer = Layer.effect
        draw = Drawer { x: Float, y: Float, r: Float, p: Float ->
          Lines.stroke(0.8f * p, Pal.lighterOrange)
          SglDraw.dashCircle(x, y, 6 * p, Time.time * 1.7f)
        }
      }
    }, object : ShapePart() {
      init {
        progress = PartProgress.warmup
        color = Pal.lighterOrange
        layer = Layer.effect
        circle = true
        y = -18f
        radius = 0f
        radiusTo = 4f
      }
    }, object : ShapePart() {
      init {
        progress = PartProgress.warmup
        color = Pal.lighterOrange
        layer = Layer.effect
        circle = true
        hollow = true
        y = -18f
        stroke = 0f
        strokeTo = 2f
        radius = 0f
        radiusTo = 10f
      }
    }, object : HaloPart() {
      init {
        progress = PartProgress.warmup
        color = Pal.lighterOrange
        layer = Layer.effect
        tri = true
        y = -18f
        haloRadius = 10f
        haloRotateSpeed = 1f
        shapes = 4
        radius = 4f
        triLength = 0f
        triLengthTo = 8f
      }
    }, object : HaloPart() {
      init {
        progress = PartProgress.warmup
        color = Pal.lighterOrange
        layer = Layer.effect
        tri = true
        y = -18f
        haloRadius = 10f
        haloRotateSpeed = 1f
        shapes = 4
        radius = 4f
        triLength = 0f
        triLengthTo = 4f
        shapeRotation = 180f
      }
    }, object : HaloPart() {
      init {
        progress = PartProgress.warmup
        color = Pal.lighterOrange
        layer = Layer.effect
        y = -18f
        tri = true
        shapes = 2
        haloRadius = 10f
        haloRotation = 90f
        radius = 5f
        triLength = 0f
        triLengthTo = 30f
        shapeRotation = 0f
      }
    }, object : HaloPart() {
      init {
        progress = PartProgress.warmup
        color = Pal.lighterOrange
        layer = Layer.effect
        y = -18f
        tri = true
        shapes = 2
        haloRadius = 10f
        haloRotation = 90f
        radius = 5f
        triLength = 0f
        triLengthTo = 5f
        shapeRotation = 180f
      }
    }, object : HaloPart() {
      init {
        progress = PartProgress.warmup.delay(0.2f)
        color = Pal.lighterOrange
        layer = Layer.effect
        y = 0f
        tri = true
        shapes = 2
        haloRadius = 18f
        haloRotation = 90f
        radius = 4f
        triLength = 0f
        triLengthTo = 20f
        shapeRotation = 0f
      }
    }, object : HaloPart() {
      init {
        progress = PartProgress.warmup.delay(0.2f)
        color = Pal.lighterOrange
        layer = Layer.effect
        y = 0f
        tri = true
        shapes = 2
        haloRadius = 18f
        haloRotation = 90f
        radius = 4f
        triLength = 0f
        triLengthTo = 4f
        shapeRotation = 180f
      }
    }, object : HaloPart() {
      init {
        progress = PartProgress.warmup.delay(0.4f)
        color = Pal.lighterOrange
        layer = Layer.effect
        y = 8f
        tri = true
        shapes = 2
        haloRadius = 15f
        haloRotation = 90f
        radius = 4f
        triLength = 0f
        triLengthTo = 16f
        shapeRotation = 0f
      }
    }, object : HaloPart() {
      init {
        progress = PartProgress.warmup.delay(0.4f)
        color = Pal.lighterOrange
        layer = Layer.effect
        y = 8f
        tri = true
        shapes = 2
        haloRadius = 15f
        haloRotation = 90f
        radius = 4f
        triLength = 0f
        triLengthTo = 4f
        shapeRotation = 180f
      }
    }, object : HaloPart() {
      init {
        progress = PartProgress.warmup.delay(0.6f)
        color = Pal.lighterOrange
        layer = Layer.effect
        y = 16f
        tri = true
        shapes = 2
        haloRadius = 12f
        haloRotation = 90f
        radius = 4f
        triLength = 0f
        triLengthTo = 12f
        shapeRotation = 0f
      }
    }, object : HaloPart() {
      init {
        progress = PartProgress.warmup.delay(0.6f)
        color = Pal.lighterOrange
        layer = Layer.effect
        y = 16f
        tri = true
        shapes = 2
        haloRadius = 12f
        haloRotation = 90f
        radius = 4f
        triLength = 0f
        triLengthTo = 4f
        shapeRotation = 180f
      }
    }) {
      val param: FloatArray = FloatArray(9)

      override fun draw(build: Building) {
        super.draw(build)

        Draw.z(Layer.effect)
        rand.setSeed(build.id.toLong())
        SglDraw.drawTransform(build.x, build.y, shootX, shootY, build.drawrot()) { ox: Float, oy: Float, _: Float ->
          for (i in 0..2) {
            val bool = rand.random(1f) > 0.5f
            for (d in 0..2) {
              param[d * 3] = rand.random(4f) / (d + 1) * (if (bool != (d % 2 == 0)) -1 else 1)
              param[d * 3 + 1] = rand.random(360f)
              param[d * 3 + 2] = rand.random(6f) / ((d + 1) * (d + 1))
            }
            val v = MathTransform.fourierSeries(Time.time, *param)

            v.add(ox, oy)
            Draw.color(Pal.lighterOrange)
            Fill.circle(v.x, v.y, 1.3f * build.warmup())
          }
        }
      }
    }
  }
}