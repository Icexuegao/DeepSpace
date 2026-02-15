package ice.content.block.turret

import arc.Core
import arc.func.Cons
import arc.graphics.g2d.Draw
import arc.graphics.g2d.Fill
import arc.graphics.g2d.Lines
import arc.math.Interp
import arc.math.Mathf
import arc.util.Strings
import arc.util.Time
import arc.util.Tmp
import ice.content.IItems
import ice.content.ILiquids
import ice.content.block.turret.TurretBullets.freezingField
import ice.entities.bullet.base.BulletType
import ice.entities.effect.MultiEffect
import ice.ui.bundle.BaseBundle.Companion.bundle
import mindustry.Vars
import mindustry.content.Fx
import mindustry.content.Items
import mindustry.content.Liquids
import mindustry.entities.effect.WaveEffect
import mindustry.entities.part.RegionPart
import mindustry.gen.Bullet
import mindustry.gen.Sounds
import mindustry.graphics.Drawf
import mindustry.graphics.Layer
import mindustry.graphics.Pal
import mindustry.type.Category
import mindustry.type.ItemStack
import mindustry.type.LiquidStack
import singularity.graphic.SglDraw
import singularity.graphic.SglDrawConst
import singularity.world.SglFx
import singularity.world.SglUnitSorts
import singularity.world.blocks.turrets.SglTurret
import singularity.world.draw.DrawSglTurret
import singularity.world.draw.part.CustomPart

class Winter : SglTurret("winter"){
  init {
    bundle {
      desc(zh_CN, "冬至", "它用力场,将周围的物质分子的移动牢牢的限制,在极寒领域展开的瞬间,有如时间也被冻结一般,一切都停了下来,并破碎成无数微小的碎片")
    }
    requirements(
      Category.turret, ItemStack.with(
        IItems.强化合金, 210, IItems.简并态中子聚合物, 80, Items.phaseFabric, 180, IItems.铱锭, 100, IItems.气凝胶, 200, IItems.铝锭, 220, IItems.矩阵合金, 160, IItems.充能FEX水晶, 180
      )
    )
    size = 6
    scaledHealth = 410f
    recoil = 3.6f
    rotateSpeed = 1.75f
    warmupSpeed = 0.015f
    shake = 6f
    fireWarmupThreshold = 0.925f
    linearWarmup = false
    range = 560f
    targetGround = true
    targetAir = true
    shootEffect = MultiEffect(
      SglFx.winterShooting, SglFx.shootRecoilWave, object : WaveEffect() {
        init {
          colorTo = Pal.reactorPurple
          colorFrom = colorTo
          lifetime = 12f
          sizeTo = 40f
          strokeFrom = 6f
          strokeTo = 0.3f
        }
      })
    moveWhileCharging = true
    shootY = 4f

    unitSort = SglUnitSorts.denser

    energyCapacity = 4096f
    basicPotentialEnergy = 4096f

    shoot.firstShotDelay = 120f
    chargeSound = Sounds.chargeLancer
    chargeSoundPitch = 0.9f

    shootSound = Sounds.explosionReactor
    shootSoundPitch = 0.6f
    shootSoundVolume = 2f

    soundPitchRange = 0.05f

    newAmmo(object : BulletType() {
      init {
        lifetime = 20f
        speed = 28f
        collides = false
        absorbable = false
        scaleLife = true
        drawSize = 80f
        fragBullet = object : BulletType() {
          init {
            lifetime = 120f
            speed = 0.6f
            collides = false
            hittable = true
            absorbable = false
            despawnHit = true
            splashDamage = 2180f
            splashDamageRadius = 84f
            hitShake = 12f

            trailEffect = SglFx.particleSpread
            trailInterval = 10f
            trailColor = SglDrawConst.winter

            hitEffect = SglFx.iceExplode
            hitColor = SglDrawConst.winter

            hitSound = Sounds.explosionAfflict
            hitSoundPitch = 0.6f
            hitSoundVolume = 2.5f

            fragBullet = freezingField
            fragOnHit = false
            fragBullets = 1
            fragVelocityMin = 0f
            fragVelocityMax = 0f
          }

          override fun draw(b: Bullet) {
            super.draw(b)
            Draw.color(SglDrawConst.winter)

            SglDraw.drawBloomUponFlyUnit(b) { e: Bullet ->
              val rot = e.fin(Interp.pow2Out) * 3600
              SglDraw.drawCrystal(
                e.x, e.y, 30f, 14f, 8f, 0f, 0f, 0.8f, Layer.effect, Layer.bullet, rot, e.rotation(), SglDrawConst.frost, SglDrawConst.winter
              )

              Draw.alpha(1f)
              Fill.circle(e.x, e.y, 18 * e.fin(Interp.pow3In))
              Draw.reset()
            }
          }

          override fun update(b: Bullet) {
            super.update(b)
            Vars.control.sound.loop(Sounds.loopPulse, b, 2f)
          }
        }
        fragBullets = 1
        fragSpread = 0f
        fragRandomSpread = 0f
        fragAngle = 0f
        fragOnHit = false
        hitColor = SglDrawConst.winter

        hitEffect = Fx.none
        despawnEffect = Fx.none
        smokeEffect = Fx.none

        trailEffect = MultiEffect(
          SglFx.glowParticle, SglFx.railShootRecoil
        )
        trailRotation = true
        trailChance = 1f

        trailLength = 75
        trailWidth = 7f
        trailColor = SglDrawConst.winter

        chargeEffect = SglFx.shrinkIceParticleSmall
      }

      override fun draw(b: Bullet) {
        super.draw(b)
        Draw.z(Layer.bullet)
        Draw.color(SglDrawConst.winter)
        val rot = b.fin() * 3600

        SglDraw.drawCrystal(
          b.x, b.y, 30f, 14f, 8f, 0f, 0f, 0.8f, Layer.effect, Layer.bullet, rot, b.rotation(), SglDrawConst.frost, SglDrawConst.winter
        )
      }
    }, true) { bt, ammo: mindustry.entities.bullet.BulletType? ->
      bt!!.add(Core.bundle.format("bullet.splashdamage", ammo!!.fragBullet.splashDamage.toInt(), Strings.fixed(ammo.fragBullet.splashDamageRadius / Vars.tilesize, 1)))
      bt.row()
      bt.add(Core.bundle.get("infos.winterAmmo"))
    }
    consume!!.time(720f)
    consume!!.energy(1.1f)
    consume!!.liquids(
      *LiquidStack.with(
        ILiquids.相位态FEX流体, 0.2f, Liquids.cryofluid, 0.2f
      )
    )

    updating = Cons { e: SglBuilding? ->
      val t = e as SglTurretBuild?
      if (Mathf.chanceDelta((0.06f * t!!.warmup).toDouble())) {
        Tmp.v1.set(36f, 0f).setAngle(t.rotationu + 90 * Mathf.randomSign()).rotate(Mathf.random(-30, 30).toFloat())
        SglFx.iceParticle.at(e.x + Tmp.v1.x, e.y + Tmp.v1.y, Tmp.v1.angle(), SglDrawConst.frost)
      }
    }

    draw = DrawSglTurret(object : CustomPart() {
      init {
        progress = PartProgress.warmup
        draw = Drawer { x: Float, y: Float, r: Float, p: Float ->
          Draw.color(SglDrawConst.winter)
          SglDraw.gradientTri(x, y, 70 + 120 * p, 92 * p, r, 0f)
          SglDraw.gradientTri(x, y, 40 + 68 * p, 92 * p, r + 180, 0f)
          Draw.color()
        }
      }
    }, object : RegionPart("_blade") {
      init {
        mirror = true
        heatColor = SglDrawConst.winter
        heatProgress = PartProgress.warmup.delay(0.3f)
        moveX = 5f
        moveY = 4f
        moveRot = -15f
        progress = PartProgress.warmup

        moves.add(PartMove(PartProgress.recoil, 0f, -2f, 0f))
      }
    }, object : RegionPart("_side") {
      init {
        mirror = true
        heatColor = SglDrawConst.winter
        heatProgress = PartProgress.warmup.delay(0.3f)
        moveX = 8f
        moveRot = -30f
        progress = PartProgress.warmup

        moves.add(PartMove(PartProgress.recoil, 0f, -2f, -5f))
      }
    }, object : RegionPart("_bot") {
      init {
        mirror = true
        heatColor = SglDrawConst.winter
        heatProgress = PartProgress.warmup.delay(0.3f)
        moveX = 6f
        moveY = 2f
        moveRot = -25f
        progress = PartProgress.warmup

        moves.add(PartMove(PartProgress.recoil, 0f, -2f, 0f))
      }
    }, object : RegionPart("_body") {
      init {
        heatColor = SglDrawConst.winter
        heatProgress = PartProgress.warmup.delay(0.3f)
      }
    }, object : CustomPart() {
      init {
        mirror = true
        x = 20f
        drawRadius = 0f
        drawRadiusTo = 20f
        rotation = -30f
        layer = Layer.effect
        progress = PartProgress.warmup
        draw = Drawer { x: Float, y: Float, r: Float, p: Float ->
          SglDraw.drawCrystal(
            x, y, 8 + 8 * p, 6 * p, 4 * p, 0f, 0f, 0.4f * p, Layer.effect, Layer.bullet - 1, Time.time * 1.24f, r, Tmp.c1.set(SglDrawConst.frost).a(0.65f), SglDrawConst.winter
          )
        }
      }
    }, object : CustomPart() {
      init {
        mirror = true
        x = 20f
        drawRadius = 0f
        drawRadiusTo = 28f
        rotation = -65f
        layer = Layer.effect
        progress = PartProgress.warmup.delay(0.15f)
        draw = Drawer { x: Float, y: Float, r: Float, p: Float ->
          SglDraw.drawCrystal(
            x, y, 16 + 21 * p, 12 * p, 8 * p, 0f, 0f, 0.7f * p, Layer.effect, Layer.bullet - 1, Time.time * 1.24f + 45, r, Tmp.c1.set(SglDrawConst.frost).a(0.65f), SglDrawConst.winter
          )
        }
      }
    }, object : CustomPart() {
      init {
        mirror = true
        x = 20f
        drawRadius = 0f
        drawRadiusTo = 24f
        rotation = -105f
        layer = Layer.effect
        progress = PartProgress.warmup.delay(0.3f)
        draw = Drawer { x: Float, y: Float, r: Float, p: Float ->
          SglDraw.drawCrystal(
            x, y, 12 + 14 * p, 10 * p, 6 * p, 0f, 0f, 0.6f * p, Layer.effect, Layer.bullet - 1, Time.time * 1.24f + 90, r, Tmp.c1.set(SglDrawConst.frost).a(0.65f), SglDrawConst.winter
          )
        }
      }
    }, object : CustomPart() {
      init {
        mirror = true
        x = 20f
        drawRadius = 0f
        drawRadiusTo = 20f
        rotation = -135f
        layer = Layer.effect
        progress = PartProgress.warmup.delay(0.45f)
        draw = Drawer { x: Float, y: Float, r: Float, p: Float ->
          SglDraw.drawCrystal(
            x, y, 9 + 12 * p, 8 * p, 5 * p, 0f, 0f, 0.65f * p, Layer.effect, Layer.bullet - 1, Time.time * 1.24f + 135, r, Tmp.c1.set(SglDrawConst.frost).a(0.65f), SglDrawConst.winter
          )
        }
      }
    }, object : CustomPart() {
      init {
        progress = PartProgress.charge
        y = 4f
        layer = Layer.effect
        draw = Drawer { x: Float, y: Float, r: Float, p: Float ->
          Draw.color(SglDrawConst.winter)
          Drawf.tri(x, y, 10 * p, 12 * p, r)
          Drawf.tri(x, y, 10 * p, 8 * p, r + 180)
          Draw.color(SglDrawConst.frost)
          SglDraw.gradientCircle(x, y, 4 + 12 * p, -7 * p, 0f)
        }
      }
    }, object : CustomPart() {
      init {
        progress = PartProgress.warmup
        y = -18f
        layer = Layer.effect
        draw = Drawer { x: Float, y: Float, r: Float, p: Float ->
          Draw.color(SglDrawConst.frost)
          Lines.stroke(1.8f * p)
          Lines.circle(x, y, 3.5f)
          Draw.alpha(0.7f)

          for (i in 0..5) {
            SglDraw.drawTransform(x, y, 14 * p, 0f, r + Time.time * 1.5f + i * 60) { dx: Float, dy: Float, dr: Float ->
              Drawf.tri(dx, dy, 4 * p, 4f, dr)
              Drawf.tri(dx, dy, 4 * p, 14f, dr + 180f)
            }
          }

          Draw.color(SglDrawConst.winter)
          val pl = Mathf.clamp((p - 0.3f) / 0.7f)
          for (i in 0..3) {
            SglDraw.drawTransform(x, y, 16 * pl, 0f, r - Time.time + i * 90) { dx: Float, dy: Float, dr: Float ->
              SglDraw.drawCrystal(
                dx, dy, 12f, 8 * pl, 8 * pl, 0f, 0f, 0.5f * pl, Layer.effect, Layer.bullet - 1, Time.time, dr, Tmp.c1.set(SglDrawConst.frost).a(0.65f), SglDrawConst.winter
              )
            }
          }
        }
      }
    })
  }
}