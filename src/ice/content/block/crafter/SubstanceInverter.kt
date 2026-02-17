package ice.content.block.crafter

import arc.func.Cons
import arc.graphics.g2d.Draw
import arc.math.Angles
import arc.math.Interp
import arc.math.Mathf
import arc.util.Time
import arc.util.noise.Noise
import ice.content.IItems
import ice.ui.bundle.BaseBundle.Companion.bundle
import ice.world.draw.DrawMulti
import mindustry.Vars
import mindustry.entities.Effect
import mindustry.gen.Building
import mindustry.gen.Sounds
import mindustry.graphics.Layer
import mindustry.graphics.Pal
import mindustry.type.Category
import mindustry.type.ItemStack
import mindustry.world.draw.DrawBlock
import mindustry.world.draw.DrawDefault
import singularity.graphic.SglDraw
import singularity.graphic.SglDrawConst
import singularity.world.SglFx
import singularity.world.blocks.product.MediumCrafter
import singularity.world.draw.DrawBottom
import singularity.world.particles.SglParticleModels
import universecore.graphics.lightnings.LightningContainer
import universecore.graphics.lightnings.generator.CircleGenerator
import universecore.graphics.lightnings.generator.LightningGenerator
import universecore.graphics.lightnings.generator.VectorLightningGenerator

class SubstanceInverter : MediumCrafter("substance_inverter") {
  init {
    bundle {
      desc(zh_CN, "物质逆化器", "将介质反向建立物质的设备,主动分离正粒子以制造反物质,并盛装到引力容器中")
    }
    requirements(Category.crafting, ItemStack.with())
    size = 5

    placeablePlayer = false

    itemCapacity = 20
    energyCapacity = 1024f

    newConsume()
    consume!!.item(IItems.简并态中子聚合物, 1)
    consume!!.energy(5f)
    consume!!.medium(2.25f)
    consume!!.time(120f)
    newProduce()
    produce!!.item(IItems.反物质, 1)

    craftEffect = SglFx.explodeImpWaveBig
    craftEffectColor = Pal.reactorPurple

    craftedSound = Sounds.explosion
    craftedSoundVolume = 1f

    clipSize = 150f
    val generator: LightningGenerator = CircleGenerator().apply {

      radius = 13.5f
      minInterval = 1.5f
      maxInterval = 3f
      maxSpread = 2.25f

    }
    initialed = Cons { e: SglBuilding ->
      e.lightningDrawer = object : LightningContainer() {
        init {
          minWidth = 0.8f
          maxWidth = minWidth
          lifeTime = 24f
        }
      }
      e.lightnings = object : LightningContainer() {
        init {
          lerp = Interp.pow2Out
        }
      }
      e.lightningGenerator = VectorLightningGenerator().apply {
        maxSpread = 8f
        minInterval = 5f
        maxInterval = 12f
      }
    }

    crafting = Cons { e: NormalCrafterBuild? ->

      if (SglDraw.clipDrawable(e!!.x, e.y, clipSize) && Mathf.chanceDelta((e.workEfficiency() * 0.1f).toDouble())) e.lightningDrawer!!.create(generator)
      if (Mathf.chanceDelta((e.workEfficiency() * 0.04f).toDouble())) SglFx.randomLightning.at(e.x, e.y, 0f, Pal.reactorPurple)
    }

    craftTrigger = Cons { e: NormalCrafterBuild? ->
      if (!SglDraw.clipDrawable(e!!.x, e.y, clipSize)) return@Cons
      val a = Mathf.random(1, 3)

      for (i in 0..<a) {
        val gen: VectorLightningGenerator = e.lightningGenerator!!
        gen.vector.rnd(Mathf.random(65, 100).toFloat())
        val amount = Mathf.random(3, 5)
        for (i1 in 0..<amount) {
          e.lightnings!!.create(gen)
        }

        if (Mathf.chance(0.25)) {
          SglFx.explodeImpWave.at(e.x + gen.vector.x, e.y + gen.vector.y, Pal.reactorPurple)
          Angles.randLenVectors(
            System.nanoTime(), Mathf.random(4, 7), 2f, 3.5f
          ) { x: Float, y: Float -> SglParticleModels.floatParticle.create(e.x + gen.vector.x, e.y + gen.vector.y, Pal.reactorPurple, x, y, Mathf.random(3.25f, 4f)) }
        } else {
          SglFx.spreadLightning.at(e.x + gen.vector.x, e.y + gen.vector.y, Pal.reactorPurple)
        }
      }

      Effect.shake(5.5f, 20f, e.x, e.y)
    }

    draw = DrawMulti(DrawBottom(), object : DrawBlock() {
      override fun draw(build: Building?) {

        val e = build as NormalCrafterBuild

        SglDraw.drawBloomUnderBlock(e) { b: NormalCrafterBuild ->
          val c: LightningContainer = b.lightningDrawer!!
          if (!Vars.state.isPaused) c.update()

          Draw.color(Pal.reactorPurple)
          Draw.alpha(e.workEfficiency())
          c.draw(b.x, b.y)
        }
        Draw.z(35f)
        Draw.color()
      }
    }, DrawDefault(), object : DrawBlock() {
      override fun draw(build: Building?) {

        val e = build as NormalCrafterBuild
        Draw.z(Layer.effect)
        Draw.color(Pal.reactorPurple)
        val c: LightningContainer = e.lightnings!!
        if (!Vars.state.isPaused) c.update()
        c.draw(e.x, e.y)
        val lerp = Noise.noise(Time.time, 0f, 3.5f, 1f)
        val offsetH = 6 * lerp
        val offsetW = 14 * lerp

        SglDraw.drawLightEdge(
          e.x, e.y, (35 + offsetH) * e.workEfficiency(), 2.25f * e.workEfficiency(), (145 + offsetW) * e.workEfficiency(), 4 * e.workEfficiency()
        )

        Draw.z(Layer.bullet - 10)
        Draw.alpha(0.2f * e.workEfficiency() + lerp * 0.25f)
        SglDraw.gradientCircle(e.x, e.y, 72 * e.workEfficiency(), 6 + 5 * e.workEfficiency() + 2.3f * lerp, SglDrawConst.transColor)
        Draw.alpha(0.3f * e.workEfficiency() + lerp * 0.25f)
        SglDraw.gradientCircle(e.x, e.y, 41 * e.workEfficiency(), -6 * e.workEfficiency() - 2f * lerp, SglDrawConst.transColor)
        Draw.alpha(0.55f * e.workEfficiency() + lerp * 0.25f)
        SglDraw.gradientCircle(e.x, e.y, 18 * e.workEfficiency(), -3 * e.workEfficiency() - lerp, SglDrawConst.transColor)
        Draw.alpha(1f)
        SglDraw.drawLightEdge(
          e.x, e.y, (60 + offsetH) * e.workEfficiency(), 2.25f * e.workEfficiency(), 0f, 0.55f, (180 + offsetW) * e.workEfficiency(), 4 * e.workEfficiency(), 0f, 0.55f
        )
      }
    })
  }
}