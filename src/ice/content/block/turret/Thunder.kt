package ice.content.block.turret

import arc.func.Cons
import arc.func.Func2
import arc.graphics.g2d.Draw
import arc.math.Interp
import arc.math.Mathf
import arc.util.Time
import arc.util.Tmp
import arc.util.pooling.Pools
import ice.content.IItems
import ice.content.ILiquids
import ice.content.block.turret.TurretBullets.branch
import ice.content.block.turret.TurretBullets.lightning
import ice.entities.bullet.base.BulletType
import ice.entities.effect.MultiEffect
import ice.ui.bundle.BaseBundle.Companion.bundle
import mindustry.content.Fx
import mindustry.content.Items
import mindustry.entities.Damage
import mindustry.entities.Effect
import mindustry.entities.Units
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
import mindustry.world.draw.DrawBlock
import mindustry.world.draw.DrawMulti
import singularity.graphic.SglDraw
import singularity.world.SglFx
import singularity.world.blocks.turrets.SglTurret
import singularity.world.draw.DrawSglTurret
import universecore.graphics.lightnings.LightningContainer
import universecore.graphics.lightnings.LightningVertex
import universecore.graphics.lightnings.generator.CircleGenerator
import universecore.graphics.lightnings.generator.RandomGenerator
import universecore.graphics.lightnings.generator.VectorLightningGenerator

class Thunder : SglTurret("thunder") {
  init {
    bundle {
      desc(zh_CN, "惊蛰", "大功率电离轰击武器,它会用耀眼的闪电将敌人化为灰烬", "这座庞然大物凭借其如同雷鸣般的声响和能够与雷电平齐的杀伤力")
    }
    requirements(
      Category.turret, ItemStack.with(
        IItems.强化合金, 180, IItems.气凝胶, 150, Items.surgeAlloy, 120, IItems.矩阵合金, 100, IItems.FEX水晶, 100, IItems.充能FEX水晶, 80, IItems.铱锭, 80
      )
    )
    size = 5
    scaledHealth = 320f
    range = 400f
    val shootRan: Float = range
    warmupSpeed = 0.016f
    linearWarmup = false
    fireWarmupThreshold = 0.8f
    rotateSpeed = 1.6f
    cooldownTime = 90f
    recoil = 3.4f

    energyCapacity = 4096f
    basicPotentialEnergy = 2048f

    shootY = 22f

    shake = 4f
    shootSound = Sounds.shootCollaris

    newAmmo(object : BulletType() {
      init {
        speed = 0f
        lifetime = 60f
        collides = false
        hittable = false
        absorbable = false
        splashDamage = 1460f
        splashDamageRadius = 46f
        damage = 0f
        drawSize = shootRan

        hitColor = Pal.reactorPurple
        shootEffect = MultiEffect(SglFx.impactBubble, SglFx.shootRecoilWave, object : WaveEffect() {
          init {
            colorTo = Pal.reactorPurple
            colorFrom = colorTo
            lifetime = 12f
            sizeTo = 40f
            strokeFrom = 6f
            strokeTo = 0.3f
          }
        })

        hitEffect = Fx.none
        despawnEffect = Fx.none
        smokeEffect = Fx.none
        val g: RandomGenerator = RandomGenerator().apply {
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

        fragBullet = lightning(82f, 25f, 42f, 4.8f, Pal.reactorPurple) { b: Bullet? ->
          val u = Units.closest(b!!.team, b.x, b.y, 80f) { e -> true }
          g.originAngle = if (u == null) b.rotation() else b.angleTo(u)
          g
        }
        fragSpread = 25f
        fragOnHit = false
      }

      val generator: VectorLightningGenerator = VectorLightningGenerator().apply {
        maxSpread = 14f
        minInterval = 8f
        maxInterval = 20f

        branchChance = 0.1f
        minBranchStrength = 0.5f
        maxBranchStrength = 0.8f
        branchMaker = Func2 { vert, strength ->
          branch.maxLength = (60 * strength)
          branch.originAngle = vert.angle + Mathf.random(-90, 90)
          branch
        }
      }

      override fun init(b: Bullet) {
        super.init(b)
        val container = Pools.obtain(LightningContainer.PoolLightningContainer::class.java) { LightningContainer.PoolLightningContainer() }
        container.lifeTime = lifetime
        container.minWidth = 5f
        container.maxWidth = 8f
        container.time = 6f
        container.lerp = Interp.linear
        b.data = container

        Tmp.v1.set(b.aimX - b.originX, b.aimY - b.originY)
        val scl = Mathf.clamp(Tmp.v1.len() / shootRan)
        Tmp.v1.setLength(shootRan).scl(scl)
        val shX: Float
        val shY: Float
        val absorber = Damage.findAbsorber(b.team, b.originX, b.originY, b.originX + Tmp.v1.x, b.originY + Tmp.v1.y)
        if (absorber != null) {
          shX = absorber.x
          shY = absorber.y
        } else {
          shX = b.x + Tmp.v1.x
          shY = b.y + Tmp.v1.y
        }

        generator.vector.set(
          shX - b.originX, shY - b.originY
        )
        val amount = Mathf.random(5, 7)
        for (i in 0..<amount) {
          container.create(generator)
        }

        Time.run(6f) {
          SglFx.lightningBoltWave.at(shX, shY, Pal.reactorPurple)
          createFrags(b, shX, shY)
          Effect.shake(6f, 6f, shX, shY)
          Sounds.explosion.at(shX, shY, hitSoundPitch, hitSoundVolume)
          Damage.damage(b.team, shX, shY, splashDamageRadius, splashDamage)
        }
      }

      override fun update(b: Bullet) {
        super.update(b)
        (b.data as LightningContainer).update()
      }

      override fun draw(b: Bullet) {
        val container: LightningContainer = b.data as LightningContainer
        Draw.z(Layer.bullet)
        Draw.color(Pal.reactorPurple)
        container.draw(b.x, b.y)
      }

      override fun createSplashDamage(b: Bullet, x: Float, y: Float) {}

      override fun despawned(b: Bullet) {}

      override fun removed(b: Bullet) {
        super.removed(b)
        val data = b.data
        if (data is LightningContainer.PoolLightningContainer) {
          Pools.free(data)
        }
      }
    })
    consume!!.item(IItems.充能FEX水晶, 2)
    consume!!.energy(2.2f)
    consume!!.time(180f)
    val generator = CircleGenerator().apply {
      radius = 8f
      maxSpread = 2.5f
      minInterval = 2f
      maxInterval = 2.5f
    }

    initialed = Cons { e: SglBuilding ->
      e.CONTAINER = object : LightningContainer() {
        init {
          lifeTime = 45f
          maxWidth = 2f
          lerp = Interp.linear
          time = 0f
        }
      }
    }
    val timeId = timers++
    updating = Cons { e: SglBuilding ->
      e.CONTAINER?.update()
      val turret = e as SglTurretBuild
      if (turret.warmup > 0 && e.timer(timeId, 25 / turret.warmup)) {
        e.CONTAINER?.create(generator)
      }
      if (Mathf.chanceDelta((0.03f * turret.warmup).toDouble())) {
        Tmp.v1.set(0f, -16f).rotate(turret.drawrot())
        SglFx.randomLightning.at(e.x + Tmp.v1.x, e.y + Tmp.v1.y, Pal.reactorPurple)
      }
    }

    newCoolant(1.45f, 20f)
    consume!!.liquid(ILiquids.相位态FEX流体, 0.25f)

    draw = DrawMulti(DrawSglTurret(object : RegionPart("_center") {
      init {
        moveY = 8f
        progress = PartProgress.warmup
        heatColor = Pal.reactorPurple
        heatProgress = PartProgress.warmup.delay(0.25f)

        moves.add(PartMove(PartProgress.recoil, 0f, -4f, 0f))
      }
    }, object : RegionPart("_body") {
      init {
        heatColor = Pal.reactorPurple
        heatProgress = PartProgress.warmup.delay(0.25f)
      }
    }, object : RegionPart("_side") {
      init {
        mirror = true
        moveX = 5f
        moveY = -5f
        progress = PartProgress.warmup
        heatColor = Pal.reactorPurple
        heatProgress = PartProgress.warmup.delay(0.25f)
      }
    }, object : ShapePart() {
      init {
        color = Pal.reactorPurple
        circle = true
        hollow = true
        stroke = 0f
        strokeTo = 2f
        y = -16f
        radius = 0f
        radiusTo = 10f
        progress = PartProgress.warmup
        layer = Layer.effect
      }
    }, object : ShapePart() {
      init {
        circle = true
        y = -16f
        radius = 0f
        radiusTo = 3.5f
        color = Pal.reactorPurple
        layer = Layer.effect
        progress = PartProgress.warmup
      }
    }, object : HaloPart() {
      init {
        progress = PartProgress.warmup
        color = Pal.reactorPurple
        layer = Layer.effect
        y = -16f
        haloRotation = 90f
        shapes = 2
        triLength = 0f
        triLengthTo = 30f
        haloRadius = 10f
        tri = true
        radius = 4f
      }
    }, object : HaloPart() {
      init {
        progress = PartProgress.warmup
        color = Pal.reactorPurple
        layer = Layer.effect
        y = -16f
        haloRotation = 90f
        shapes = 2
        triLength = 0f
        triLengthTo = 6f
        haloRadius = 10f
        tri = true
        radius = 4f
        shapeRotation = 180f
      }
    }, object : ShapePart() {
      init {
        circle = true
        y = 22f
        radius = 0f
        radiusTo = 5f
        color = Pal.reactorPurple
        layer = Layer.effect
        progress = PartProgress.warmup
      }
    }, object : ShapePart() {
      init {
        color = Pal.reactorPurple
        circle = true
        hollow = true
        stroke = 0f
        strokeTo = 1.5f
        y = 22f
        radius = 0f
        radiusTo = 8f
        progress = PartProgress.warmup
        layer = Layer.effect
      }
    }), object : DrawBlock() {
      override fun draw(build: Building) {
        rand.setSeed(build.id.toLong())
        val turret = build as SglTurretBuild
        Draw.z(Layer.effect)
        Draw.color(Pal.reactorPurple)
        Tmp.v1.set(1f, 0f).setAngle(turret.rotationu)
        val sclX = Tmp.v1.x
        val sclY = Tmp.v1.y
        turret.CONTAINER?.draw(turret.x + sclX * 22, turret.y + sclY * 22)
        val step = 45 / 16f
        if (turret.warmup < 0.001f) return
        for (i in 0..15) {
          val x = turret.x + (step * i) * sclX * turret.warmup + 14 * sclX
          val y = turret.y + (step * i) * sclY * turret.warmup + 14 * sclY
          SglDraw.drawRectAsCylindrical(
            x, y, rand.random(2, 18) * turret.warmup, rand.random(1.5f, 10f), (10 + i * 0.75f + rand.random(8)) * turret.warmup, (Time.time * rand.random(0.8f, 2f) + rand.random(360)) * (if (rand.random(1f) < 0.5) -1 else 1), turret.drawrot(), Pal.reactorPurple, Pal.reactorPurple2, Layer.bullet - 0.5f, Layer.effect
          )
        }
      }
    })
  }
}