package ice.content.block.nuclear

import arc.func.Cons
import arc.func.Floatf
import arc.func.Func
import arc.graphics.Color
import arc.graphics.g2d.Draw
import arc.graphics.g2d.Fill
import arc.graphics.g2d.Lines
import arc.math.Angles
import arc.math.Mathf
import arc.util.Tmp
import ice.content.IItems
import ice.content.ILiquids

import mindustry.gen.Building
import mindustry.gen.Sounds
import mindustry.graphics.Layer
import mindustry.graphics.Pal
import mindustry.type.ItemStack
import mindustry.world.draw.*
import singularity.graphic.SglDraw
import singularity.type.SglCategory
import singularity.world.blocks.nuclear.NuclearReactor
import singularity.world.draw.DrawBottom
import singularity.world.draw.DrawReactorHeat
import singularity.world.draw.DrawRegionDynamic
import singularity.world.particles.SglParticleModels
import kotlin.math.max

class 超核临界反应堆 : NuclearReactor("overrun_reactor") {
  init {

    localization {
      zh_CN {
        this.localizedName = "超核临界反应堆"
        description = "先进的特大型反应堆,内部力场进一步压缩燃料使反应更加剧烈,具有极高的产能效率,且不会产生核废料\n需要特殊的冷却手段控制堆温,反应堆温度超过限制温度时会造成堆芯熔毁,引发大范围毁灭性[red]核爆[]"
      }
    }
    requirements(
      SglCategory.nuclear,
      IItems.强化合金,
      400,
      IItems.FEX水晶,
      260,
      IItems.充能FEX水晶,
      280,
      IItems.简并态中子聚合物,
      100,
      IItems.铀238,
      320,
      IItems.暮光合金,
      375,
      IItems.絮凝剂,
      240

    )
    size = 6
    hasLiquids = true
    itemCapacity = 50
    liquidCapacity = 400f
    energyCapacity = 16384f

    explosionDamageBase = 580
    explosionRadius = 32

    explosionSoundVolume = 5f
    explosionSoundPitch = 0.4f

    productHeat = 0.35f

    warmupSpeed = 0.0015f
    ambientSound = Sounds.loopPulse
    ambientSoundVolume = 0.6f

    newReact(IItems.浓缩铀235核燃料, 240f, 22f, false)
    newReact(IItems.浓缩钚239核燃料, 210f, 25f, false)

    addTransfer(ItemStack(IItems.氢聚变燃料, 1))
    consume!!.time(120f)
    consume!!.item(IItems.相位封装氢单元, 1)

    addTransfer(ItemStack(IItems.氦聚变燃料, 1))
    consume!!.time(120f)
    consume!!.item(IItems.相位封装氦单元, 1)

    addCoolant(0.4f)
    consume!!.liquid(ILiquids.相位态FEX流体, 0.4f)

    crafting = Cons { e: NormalCrafterBuild? ->
      if (Mathf.chanceDelta((0.06f * e!!.workEfficiency()).toDouble())) Angles.randVectors(
        System.nanoTime(),
        1,
        15f
      ) { x: Float, y: Float ->
        val iff = Mathf.random(0.4f, max(0.4f, e.workEfficiency()))
        Tmp.v1.set(x, y).scl(0.5f * iff / 2)
        SglParticleModels.floatParticle.create(e.x + x, e.y + y, Pal.reactorPurple, Tmp.v1.x, Tmp.v1.y, iff * 6.5f * e.workEfficiency())
      }
    }

    drawers = DrawMulti(DrawBottom(), object : DrawPlasma() {
      init {
        suffix = "_plasma_"
        plasma1 = Pal.reactorPurple
        plasma2 = Pal.reactorPurple2
      }
    }, object : DrawRegionDynamic<NormalCrafterBuild>("_liquid") {
      init {
        alpha = Floatf { e: NormalCrafterBuild -> e.liquids.currentAmount() / e.block.liquidCapacity }
        color = Func { _: NormalCrafterBuild -> Tmp.c1.set(ILiquids.相位态FEX流体.color).lerp(Color.white, 0.3f) }
      }

      override fun draw(build: Building) {
        SglDraw.drawBloomUnderBlock(build) {
          super.draw(build)
        }
        Draw.z(35f)
      }
    }, object : DrawRegion("_rotator_0") {
      init {
        rotateSpeed = 5f
      }
    }, object : DrawRegion("_rotator_1") {
      init {
        rotateSpeed = -5f
      }
    }, DrawDefault(), DrawReactorHeat(), object : DrawBlock() {
      override fun draw(build: Building) {
        val e = build as NuclearReactor.NuclearReactorBuild
        Draw.z(Layer.effect)
        Draw.color(Pal.reactorPurple)
        val shake = Mathf.random(-0.3f, 0.3f) * e.workEfficiency()
        Tmp.v1.set(19 + shake, 0f).rotate(e.totalProgress * 2)
        Tmp.v2.set(0f, 19 + shake).rotate(e.totalProgress * 2)
        Fill.poly(e.x + Tmp.v1.x, e.y + Tmp.v1.y, 3, 3f, e.totalProgress * 2)
        Fill.poly(e.x + Tmp.v2.x, e.y + Tmp.v2.y, 3, 3f, e.totalProgress * 2 + 90)
        Fill.poly(e.x - Tmp.v1.x, e.y - Tmp.v1.y, 3, 3f, e.totalProgress * 2 + 180)
        Fill.poly(e.x - Tmp.v2.x, e.y - Tmp.v2.y, 3, 3f, e.totalProgress * 2 + 270)

        Tmp.v1.set(16f, 0f).rotate(-e.totalProgress * 2)
        Tmp.v2.set(0f, 16f).rotate(-e.totalProgress * 2)
        Fill.poly(e.x + Tmp.v1.x, e.y + Tmp.v1.y, 3, 3f, -e.totalProgress * 2 - 180)
        Fill.poly(e.x + Tmp.v2.x, e.y + Tmp.v2.y, 3, 3f, -e.totalProgress * 2 - 90)
        Fill.poly(e.x - Tmp.v1.x, e.y - Tmp.v1.y, 3, 3f, -e.totalProgress * 2)
        Fill.poly(e.x - Tmp.v2.x, e.y - Tmp.v2.y, 3, 3f, -e.totalProgress * 2 + 90)

        Lines.stroke(1.8f * e.workEfficiency())
        Lines.circle(e.x, e.y, 18 + shake)
      }

    })
  }
}
