package ice.content.block.turret

import arc.graphics.Color
import arc.graphics.g2d.Draw
import arc.graphics.g2d.Fill
import arc.graphics.g2d.Lines
import arc.math.Mathf
import arc.util.Time
import arc.util.Tmp
import ice.content.IItems
import ice.content.IStatus
import ice.content.block.turret.TurretBullets.crushedIce
import ice.content.block.turret.TurretBullets.rand
import ice.entities.bullet.base.BulletType
import ice.ui.bundle.BaseBundle.Companion.bundle
import mindustry.content.Fx
import mindustry.content.Items
import mindustry.content.Liquids
import mindustry.entities.part.RegionPart
import mindustry.gen.Bullet
import mindustry.gen.Hitboxc
import mindustry.gen.Sounds
import mindustry.gen.Statusc
import mindustry.graphics.Drawf
import mindustry.graphics.Layer
import mindustry.type.Category
import mindustry.type.ItemStack
import singularity.graphic.MathRenderer
import singularity.graphic.SglDraw
import singularity.graphic.SglDrawConst
import singularity.world.SglFx
import singularity.world.blocks.turrets.LaserTurret
import singularity.world.draw.DrawSglTurret

class Fubuki: LaserTurret("fubuki") {
  init{
    bundle {
      desc(zh_CN, "吹雪", "向前喷发凛冽的冰霜风暴,凛冽的风雪足以将敌人冻结成冰雕")
    }
    requirements(
      Category.turret, ItemStack.with(
        IItems.强化合金, 100, IItems.铝锭, 140, IItems.充能FEX水晶, 60, IItems.气凝胶, 80, IItems.铱锭, 30, Items.phaseFabric, 60
      )
    )
    size = 4
    scaledHealth = 400f
    rotateSpeed = 2.4f
    warmupSpeed = 0.01f
    fireWarmupThreshold = 0f
    linearWarmup = false
    range = 300f
    targetGround = true
    targetAir = true

    energyCapacity = 1024f
    basicPotentialEnergy = 256f

    shootY = 12f

    needCooldown = false
    shootingConsume = true

    shootSound = Sounds.none



    newAmmo(object : BulletType() {
      val ice: mindustry.entities.bullet.BulletType = crushedIce.copy()
      val shootBullets = arrayOf(ice, object : BulletType() {
        init {
          damage = 26f
          speed = 8f
          lifetime = 37.5f
          hitColor = Color.white
          despawnEffect = SglFx.cloudGradient

          trailWidth = 1.5f
          trailColor = Color.white
          trailLength = 18

          trailEffect = SglFx.iceParticle
          trailRotation = true
          trailChance = 0.07f

          knockback = 2f
        }

        override fun draw(b: Bullet) {
          super.draw(b)
          Draw.color(hitColor, 0f)

          Draw.z(Layer.flyingUnit + 1)
          SglDraw.gradientCircle(b.x, b.y, 14f, 0.6f)
          SglDraw.drawBloomUponFlyUnit(b) { e: Bullet ->
            Draw.color(hitColor)
            SglDraw.drawDiamond(e.x, e.y, 14f, 6 + Mathf.absin(1f, 2f), e.rotation())
          }
        }

        override fun hitEntity(b: Bullet?, entity: Hitboxc?, health: Float) {
          super.hitEntity(b, entity, health)
          if (entity is Statusc) {
            entity.apply(IStatus.冻结, entity.getDuration(IStatus.冻结) + 10f)
          }
        }
      }, object : BulletType() {
        init {
          damage = 36f
          speed = 6f
          lifetime = 50f
          hitColor = SglDrawConst.frost
          despawnEffect = SglFx.cloudGradient

          trailWidth = 2f
          trailColor = Color.white
          trailLength = 22

          trailEffect = SglFx.particleSpread
          trailRotation = true
          trailChance = 0.06f

          knockback = 4f
        }

        override fun draw(b: Bullet) {
          super.draw(b)
          Draw.color(hitColor, 0f)
          Draw.z(Layer.flyingUnit + 1)
          SglDraw.gradientCircle(b.x, b.y, 14f, 0.6f)

          SglDraw.drawBloomUponFlyUnit(b) { e: Bullet ->
            Draw.color(Color.white)
            Fill.circle(e.x, e.y, 2f)
            Lines.stroke(1f, hitColor)
            Lines.circle(e.x, e.y, 4f)
            val step = 360f / 6
            for (i in 0..5) {
              SglDraw.drawTransform(e.x, e.y, 6f, 0f, step * i + Time.time * 2) { x: Float, y: Float, r: Float ->
                Drawf.tri(x, y, 2.5f, 2.5f, r)
                Drawf.tri(x, y, 2.5f, 6f, r + 180)
              }
            }
            Draw.reset()
          }
        }

        override fun hitEntity(b: Bullet?, entity: Hitboxc?, health: Float) {
          super.hitEntity(b, entity, health)
          if (entity is Statusc) {
            entity.apply(IStatus.冻结, entity.getDuration(IStatus.冻结) + 12f)
          }
        }
      })

      init {
        ice.speed = 10f
        ice.lifetime = 30f
        ice.trailWidth = 1f
        ice.trailLength = 18
        ice.trailColor = SglDrawConst.frost
        ice.knockback = 1f

        speed = 0f
        lifetime = 10f
        rangeOverride = 300f
        despawnEffect = Fx.none
        hittable = false
        collides = false
        absorbable = false
      }

      val trans: Color? = Color.white.cpy().a(0f)

      override fun continuousDamage(): Float {
        var res = 0f
        for (i in shootBullets.indices) {
          res += shootBullets[i].damage * (1f / (i + 1))
        }
        return res * 4
      }

      override fun update(b: Bullet) {
        super.update(b)
        val owner = b.owner
        if (owner is SglTurretBuild && owner.isAdded) {
          b.keepAlive = owner.warmup > 0.01f

          owner.warmup = Mathf.lerpDelta(owner.warmup, (if (owner.wasShooting() && owner.shootValid()) 1 else 0).toFloat(), warmupSpeed)
          owner.reloadCounter = 0f

          if (b.timer(5, if (owner.warmup <= 0.01) Float.MAX_VALUE else 3 / owner.warmup)) {
            for (i in shootBullets.indices) {
              val bu = shootBullets[i]

              if (Mathf.chance((1f / (i + 1)).toDouble())) {
                bu.create(b, b.x, b.y, b.rotation() + Mathf.range(12 * owner.warmup))
              }
            }
          }
        } else b.remove()
      }

      override fun draw(b: Bullet) {
        super.draw(b)
        val owner = b.owner
        if (owner is SglTurretBuild) {
          Draw.color(SglDrawConst.frost)
          Fill.circle(b.x, b.y, 3 * owner.warmup)
          Lines.stroke(0.7f * owner.warmup)
          SglDraw.dashCircle(b.x, b.y, 4f, Time.time * 1.5f)

          Draw.draw(Draw.z()) {
            rand.setSeed(owner.id.toLong())
            MathRenderer.setDispersion(0.2f * owner.warmup)
            MathRenderer.setThreshold(0.3f, 0.6f)
            MathRenderer.drawOval(
              b.x, b.y, 8 * owner.warmup, 3 * owner.warmup, Time.time * rand.random(1.5f, 3f)
            )
            MathRenderer.drawOval(
              b.x, b.y, 9 * owner.warmup, 4f * owner.warmup, -Time.time * rand.random(1.5f, 3f)
            )
          }

          Tmp.v1.set(range, 0f).setAngle(owner.rotationu).scl(owner.warmup)
          Tmp.v2.set(Tmp.v1).rotate(owner.warmup * 15)
          Tmp.v1.rotate(-owner.warmup * 15)

          Draw.z(Layer.flyingUnit)
          SglDraw.gradientLine(b.x, b.y, b.x + Tmp.v1.x, b.y + Tmp.v1.y, SglDrawConst.frost, trans, 0)
          SglDraw.gradientLine(b.x, b.y, b.x + Tmp.v2.x, b.y + Tmp.v2.y, SglDrawConst.frost, trans, 0)
        }
      }
    })
    consume!!.time(1f)
    consume!!.showTime = false
    consume!!.energy(3.2f)
    consume!!.liquid(Liquids.cryofluid, 0.2f)

    draw = DrawSglTurret(object : RegionPart("_blade") {
      init {
        progress = PartProgress.warmup
        heatProgress = PartProgress.warmup

        heatColor = SglDrawConst.frost

        moveX = 2f
        moveY = -6f

        mirror = true
      }
    }, object : RegionPart("_body") {
      init {
        progress = PartProgress.warmup
        heatProgress = PartProgress.warmup
        heatColor = SglDrawConst.frost

        moveY = -4f
      }
    })
  }
}