package ice.content.block.power

import arc.func.Cons
import arc.func.Floatf
import arc.func.Func
import arc.func.Prov
import arc.math.Angles
import arc.math.Mathf
import arc.math.geom.Vec2
import arc.util.Tmp
import ice.content.IItems
import ice.content.ILiquids
import mindustry.entities.Effect
import mindustry.gen.Sounds
import mindustry.graphics.Pal
import mindustry.type.Category
import mindustry.world.draw.DrawDefault
import mindustry.world.draw.DrawMulti
import singularity.world.SglFx
import singularity.world.blocks.product.NormalCrafter
import singularity.world.draw.DrawBottom
import singularity.world.draw.DrawExpandPlasma
import singularity.world.particles.SglParticleModels
import universecore.world.particles.models.MultiParticleModel
import universecore.world.particles.Particle
import universecore.world.particles.ParticleModel
import universecore.world.particles.models.*
import universecore.world.particles.models.TargetMoveParticle.Companion.dest
import universecore.world.particles.models.TargetMoveParticle.Companion.eff

class 核子冲击反应堆 :NormalCrafter("nuclear_impact_reactor") {
  val model: ParticleModel = MultiParticleModel(
    SizeVelRelatedParticle(), TargetMoveParticle().apply {
      target = Func { p: Particle -> p.dest }
      deflection = Floatf { p: Particle -> p.eff }
    }, RandDeflectParticle().apply {
      deflectAngle = 0f
      strength = 0.125f
    }, TrailFadeParticle().apply {
      trailFade = 0.04f
      fadeColor = Pal.lightishGray
      colorLerpSpeed = 0.03f
    }, ShapeParticle(), DrawDefaultTrailParticle()
  )

  init {
    localization {
      zh_CN {
        localizedName = "核子冲击反应堆"
        description = "定向约束核爆炸并推动压电转子发电"
      }
    }
    requirementPairs(
      Category.power,
      IItems.强化合金 to 260,
      IItems.气凝胶 to 240,
      IItems.铀238 to 300,
      IItems.钴钢 to 220,
      IItems.单晶硅 to 280,
      IItems.絮凝剂 to 160,
      IItems.暮光合金 to 200
    )
    size = 5
    itemCapacity = 30
    liquidCapacity = 120f

    craftEffect = SglFx.explodeImpWaveBig
    craftEffectColor = Pal.reactorPurple

    updateEffect = SglFx.impWave
    effectRange = 2f
    updateEffectChance = 0.025f
    ambientSound = Sounds.loopMachineSpin
    ambientSoundVolume = 0.55f
    craftedSound = Sounds.explosionPlasmaSmall
    craftedSoundVolume = 1f


    craftTrigger = Cons { e: NormalCrafterBuild ->
      for(particle in Particle.get { p -> p.x < e.x + 20 && p.x > e.x - 20 && p.y < e.y + 20 && p.y > e.y - 20 }) {
        particle!!.remove()
      }
      Effect.shake(4f, 18f, e.x, e.y)
      Angles.randLenVectors(System.nanoTime(), Mathf.random(5, 9), 4.75f, 6.25f) { x: Float, y: Float ->
        Tmp.v1.set(x, y).setLength(4f)
        val p: Particle = model.create(e.x + Tmp.v1.x, e.y + Tmp.v1.y, Pal.reactorPurple, x, y, Mathf.random(5f, 7f))
        p.dest = Vec2(e.x, e.y)
        p.eff = e.workEfficiency() * 0.15f
      }
    }
    crafting = Cons { e: NormalCrafterBuild ->
      if (Mathf.chanceDelta(0.02)) Angles.randLenVectors(
        System.nanoTime(), 1, 2f, 3.5f
      ) { x: Float, y: Float ->
        SglParticleModels.floatParticle.create(e.x, e.y, Pal.reactorPurple, x, y, Mathf.random(3.25f, 4f))
      }
    }

    warmupSpeed = 0.0008f

    newConsume().consValidCondition { e: NormalCrafterBuild -> e.power.status >= 0.99f }
    consume!!.item(IItems.浓缩铀235核燃料, 1)
    consume!!.power(80f)
    consume!!.liquid(ILiquids.急冻液, 0.6f)
    consume!!.time(180f)
    newProduce()
    produce!!.power(400f)

    newConsume().consValidCondition { e: NormalCrafterBuild -> e.power.status >= 0.99f }
    consume!!.item(IItems.浓缩钚239核燃料, 1)
    consume!!.power(80f)
    consume!!.liquid(ILiquids.急冻液, 0.6f)
    consume!!.time(150f)
    newProduce()
    produce!!.power(425f)

    drawers = DrawMulti(
      DrawBottom(), object :DrawExpandPlasma() {
        init {
          plasmas = 2
        }
      }, DrawDefault()
    )
    buildType = Prov(::NormalCraftDWD)
  }

  private inner class NormalCraftDWD :NormalCrafterBuild()
}