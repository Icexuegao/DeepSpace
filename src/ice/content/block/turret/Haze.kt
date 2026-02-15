package ice.content.block.turret

import arc.Core
import arc.func.Cons
import arc.func.Func2
import arc.func.Func3
import arc.graphics.Color
import arc.graphics.g2d.Draw
import arc.graphics.g2d.Fill
import arc.graphics.g2d.TextureRegion
import arc.math.Angles
import arc.math.Interp
import arc.math.Mathf
import arc.util.Strings
import arc.util.Tmp
import arc.util.pooling.Pools
import ice.content.IItems
import ice.content.IStatus
import ice.content.block.turret.TurretBullets.rand
import ice.entities.bullet.base.BulletType
import ice.entities.effect.MultiEffect
import ice.ui.bundle.BaseBundle.Companion.bundle
import mindustry.audio.SoundLoop
import mindustry.content.Fx
import mindustry.content.Items
import mindustry.entities.Damage
import mindustry.entities.Units
import mindustry.entities.part.RegionPart
import mindustry.gen.Bullet
import mindustry.gen.Sounds
import mindustry.gen.Unit
import mindustry.graphics.Layer
import mindustry.graphics.Pal
import mindustry.type.Category
import mindustry.type.ItemStack
import mindustry.world.meta.StatUnit
import singularity.Sgl
import singularity.Singularity
import singularity.graphic.SglDraw
import singularity.world.SglFx
import singularity.world.blocks.turrets.EmpBulletType
import singularity.world.blocks.turrets.SglTurret
import singularity.world.draw.DrawSglTurret
import universecore.graphics.lightnings.LightningContainer
import universecore.graphics.lightnings.LightningVertex
import universecore.graphics.lightnings.generator.RandomGenerator
import kotlin.math.min

class Haze : SglTurret("haze") {
  init {
    bundle {
      desc(zh_CN, "阴霾", "大型石墨导弹发射器,发射一枚电磁脉冲核弹,包裹的巨量石墨会产生一片巨大的石墨云传导电磁脉冲,造成严重的电子损伤")
    }
    requirements(
      Category.turret, ItemStack.with(
        IItems.强化合金, 180, IItems.气凝胶, 180, IItems.矩阵合金, 120, IItems.铀238, 100, Items.surgeAlloy, 140, Items.graphite, 200
      )
    )
    size = 5

    accurateDelay = false
    accurateSpeed = false
    itemCapacity = 36
    range = 580f
    minRange = 100f
    shake = 7.5f
    recoil = 2f
    recoilTime = 150f
    cooldownTime = 150f

    shootSound = Sounds.shootMissile

    rotateSpeed = 1.25f

    shootY = 4f

    warmupSpeed = 0.015f
    fireWarmupThreshold = 0.94f
    linearWarmup = false

    scaledHealth = 200f
    val type: Func3<Float, Float, Float, EmpBulletType> = Func3 { dam: Float?, empD: Float?, r: Float? ->
      object : EmpBulletType() {
        init {
          lifetime = 180f
          splashDamage = dam!!
          splashDamageRadius = r!!

          damage = 0f
          empDamage = empD!!
          empRange = r

          hitSize = 5f

          hitShake = 16f
          despawnHit = true

          hitEffect = MultiEffect(
            Fx.shockwave, Fx.bigShockwave, SglFx.explodeImpWaveLarge, SglFx.spreadLightning
          )

          homingPower = 0.02f
          homingRange = 240f

          shootEffect = Fx.shootBig
          smokeEffect = Fx.shootSmokeMissile
          trailColor = Pal.redLight
          trailEffect = SglFx.shootSmokeMissileSmall
          trailInterval = 1f
          trailRotation = true
          hitColor = Items.graphite.color

          trailWidth = 3f
          trailLength = 28

          hitSound = Sounds.explosion
          hitSoundVolume = 1.2f

          speed = 0.1f

          fragOnHit = true
          fragBullets = 1
          fragVelocityMin = 0f
          fragVelocityMax = 0f
          fragBullet = object : BulletType(0f, 0f) {
            init {
              lifetime = 450f
              collides = false
              pierce = true
              hittable = false
              absorbable = false
              hitEffect = Fx.none
              shootEffect = Fx.none
              despawnEffect = Fx.none
              smokeEffect = Fx.none
              drawSize = r * 1.2f
            }

            val branch: RandomGenerator = RandomGenerator()
            val generator: RandomGenerator = RandomGenerator().apply {
              maxLength = 100f
              maxDeflect = 55f

              branchChance = 0.2f
              minBranchStrength = 0.8f
              maxBranchStrength = 1f
              branchMaker = Func2 { vert: LightningVertex?, strength: Float? ->
                branch.maxLength = 60 * strength!!
                branch.originAngle = vert!!.angle + Mathf.random(-90, 90)
                branch
              }
            }

            override fun init(b: Bullet) {
              super.init(b)
              val c = Pools.obtain(LightningContainer.PoolLightningContainer::class.java) { LightningContainer.PoolLightningContainer() }
              b.data = c
              c.maxWidth = 6f
              c.lerp = Interp.linear
              c.minWidth = 4f
              c.lifeTime = 60f
              c.time = 30f
            }

            override fun update(b: Bullet) {
              super.update(b)
              Units.nearbyEnemies(b.team, b.x, b.y, r, Cons { u: Unit? -> Sgl.empHealth.empDamage(u, 0.8f, false) })
              if (b.timer(0, 6f)) {
                Damage.status(b.team, b.x, b.y, r, IStatus.电子干扰, min(450 - b.time, 120f), true, true)
              }
              val data = b.data
              if (data is LightningContainer) {
                if (b.timer(2, 15 / Mathf.clamp((b.fout() - 0.15f) * 4))) {
                  generator.setOffset(Mathf.random(-45f, 45f), Mathf.random(-45f, 45f))
                  generator.originAngle = Mathf.random(0f, 360f)
                  data.create(generator)
                }
                data.update()
              }
            }

            override fun draw(e: Bullet) {
              Draw.z(Layer.bullet - 5)
              Draw.color(Pal.stoneGray)
              Draw.alpha(0.6f)
              rand.setSeed(e.id.toLong())
              Angles.randLenVectors(e.id.toLong(), 8 + 70, r * 1.2f) { x: Float, y: Float ->
                val size = rand.random(14, 20).toFloat()
                val i = e.fin(Interp.pow3Out)
                Fill.circle(e.x + x * i, e.y + y * i, size * e.fout(Interp.pow5Out))
              }

              Draw.color(Items.graphite.color)
              Draw.z(Layer.effect)
              val data = e.data
              if (data is LightningContainer) {
                data.draw(e.x, e.y)
              }
            }

            override fun removed(b: Bullet) {
              val data = b.data
              if (data is LightningContainer) {
                Pools.free(data)
              }
              super.removed(b)
            }
          }
        }

        var regionOutline: TextureRegion? = null

        override fun init(b: Bullet) {
          super.init(b)
          b.data = SoundLoop(Sounds.loopMissileTrail, 0.65f)
        }

        override fun update(b: Bullet) {
          super.update(b)
          Tmp.v1.set(b.vel).setLength(28f)
          b.vel.approachDelta(Tmp.v1, 0.06f * Mathf.clamp((b.fin() - 0.10f) * 5f))
          val data = b.data
          if (data is SoundLoop) {
            data.update(b.x, b.y, true)
          }
        }

        override fun removed(b: Bullet) {
          super.removed(b)
          val data = b.data
          if (data is SoundLoop) {
            data.stop()
          }
        }

        override fun draw(b: Bullet) {
          drawTrail(b)
          Draw.z(Layer.effect + 1)
          Draw.rect(regionOutline, b.x, b.y, b.rotation() - 90)

          SglDraw.drawTransform(b.x, b.y, 0f, 4 * b.fin(), b.rotation() - 90) { x: Float, y: Float, r: Float ->
            Draw.rect(regionOutline, x, y, 4f, 10.5f, r)
          }
          SglDraw.drawTransform(b.x, b.y, 0f, -4f, b.rotation() - 90) { x: Float, y: Float, r: Float ->
            Draw.color(hitColor, 0.75f)
            Fill.circle(x, y, 2.5f)
            Draw.color(Color.white)
            Fill.circle(x, y, 1.5f)
          }
        }

        override fun load() {
          super.load()
          val r = Singularity.getModAtlas("haze_missile")
          /////val p = Core.atlas.getPixmap(r)
          regionOutline = Singularity.getModAtlas("haze_missile") //TextureRegion(Texture(Pixmaps.outline(p, Pal.darkOutline, 3)))
        }
      }
    }

    newAmmo(type.get(480f, 500f, 120f)) { t, b: mindustry.entities.bullet.BulletType? ->
      t!!.add(Core.bundle.get("infos.graphiteEmpAmmo"))
      t.row()
      t.table { table ->
        table!!.add(Core.bundle.format("bullet.empDamage", Strings.autoFixed(0.8f * 60, 1) + "/" + StatUnit.seconds.localized(), ""))
        table.row()
        table.add(IStatus.电子干扰.emoji() + "[stat]" + IStatus.电子干扰.localizedName + "[lightgray] ~ [stat]7.5[lightgray] " + Core.bundle.get("unit.seconds"))
      }.padLeft(15f)
    }
    consume!!.items(
      *ItemStack.with(
        Items.graphite, 12, IItems.浓缩铀235核燃料, 1
      )
    )
    consume!!.time(480f)

    newAmmo(type.get(600f, 550f, 145f)) { t, _ ->
      t!!.add(Core.bundle.get("infos.graphiteEmpAmmo"))
      t.row()
      t.table { table ->
        table!!.add(Core.bundle.format("bullet.empDamage", Strings.autoFixed(0.5f * 60, 1) + "/" + StatUnit.seconds.localized(), ""))
        table.row()
        table.add(IStatus.电子干扰.emoji() + "[stat]" + IStatus.电子干扰.localizedName + "[lightgray] ~ [stat]7.5[lightgray] " + Core.bundle.get("unit.seconds"))
      }.padLeft(15f)
    }
    consume!!.items(
      *ItemStack.with(
        Items.graphite, 12, IItems.浓缩钚239核燃料, 1
      )
    )
    consume!!.time(510f)

    draw = DrawSglTurret(object : RegionPart("_missile") {
      init {
        progress = PartProgress.warmup.mul(PartProgress.reload.inv())
        x = 0f
        y = -4f
        moveY = 8f
      }
    }, object : RegionPart("_side") {
      init {
        progress = PartProgress.warmup
        mirror = true
        moveX = 4f
        moveY = 2f
        moveRot = -35f

        under = true
        layerOffset = -0.3f
        turretHeatLayer = Layer.turret - 0.2f

        moves.add(PartMove(PartProgress.recoil, 0f, -2f, -10f))
      }
    }, object : RegionPart("_spine") {
      init {
        progress = PartProgress.warmup
        heatProgress = PartProgress.warmup
        mirror = true
        outline = false

        heatColor = Items.graphite.color
        heatLayerOffset = 0f

        xScl = 1.5f
        yScl = 1.5f

        x = 3.3f
        y = 7.3f
        moveX = 10f
        moveY = 5f
        moveRot = -30f

        under = true
        layerOffset = -0.3f
        turretHeatLayer = Layer.turret - 0.2f

        moves.add(PartMove(PartProgress.recoil.delay(0.8f), -1.33f, 0f, 16f))
      }
    }, object : RegionPart("_spine") {
      init {
        progress = PartProgress.warmup
        heatProgress = PartProgress.warmup
        mirror = true
        outline = false

        heatColor = Items.graphite.color
        heatLayerOffset = 0f

        xScl = 1.5f
        yScl = 1.5f

        x = 3.3f
        y = 7.3f
        moveX = 12.3f
        moveY = -2.6f
        moveRot = -45f

        under = true
        layerOffset = -0.3f
        turretHeatLayer = Layer.turret - 0.2f

        moves.add(PartMove(PartProgress.recoil.delay(0.4f), -1.33f, 0f, 24f))
      }
    }, object : RegionPart("_spine") {
      init {
        progress = PartProgress.warmup
        heatProgress = PartProgress.warmup
        mirror = true
        outline = false

        heatColor = Items.graphite.color
        heatLayerOffset = 0f

        xScl = 1.5f
        yScl = 1.5f

        x = 3.3f
        y = 7.3f
        moveX = 13f
        moveY = -9.2f
        moveRot = -60f

        under = true
        layerOffset = -0.3f
        turretHeatLayer = Layer.turret - 0.2f

        moves.add(PartMove(PartProgress.recoil, -1.33f, 0f, 30f))
      }
    }, object : RegionPart("_blade") {
      init {
        progress = PartProgress.warmup
        mirror = true
        moveX = 2.5f

        heatProgress = PartProgress.warmup
        heatColor = Items.graphite.color

        moves.add(PartMove(PartProgress.recoil, 0f, -2f, 0f))
      }
    }, object : RegionPart("_body") {
      init {
        mirror = false
        heatProgress = PartProgress.warmup
        heatColor = Items.graphite.color
      }
    })
  }
}