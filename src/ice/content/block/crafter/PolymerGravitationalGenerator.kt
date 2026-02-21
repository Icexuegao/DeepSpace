package ice.content.block.crafter

import arc.func.Floatf
import arc.func.Func
import arc.graphics.g2d.Draw
import arc.graphics.g2d.Lines
import arc.math.Mathf
import arc.math.geom.Geometry
import arc.scene.style.TextureRegionDrawable
import arc.util.Time
import arc.util.Tmp
import ice.content.IItems
import ice.ui.bundle.BaseBundle.Companion.bundle
import ice.world.content.blocks.abstractBlocks.IceBlock.Companion.requirements
import ice.world.draw.DrawMulti
import mindustry.Vars
import mindustry.gen.Building
import mindustry.graphics.Drawf
import mindustry.graphics.Layer
import mindustry.graphics.Pal
import mindustry.type.Category
import mindustry.type.ItemStack
import mindustry.world.draw.DrawBlock
import mindustry.world.draw.DrawDefault
import mindustry.world.draw.DrawRegion
import singularity.graphic.Distortion
import singularity.graphic.SglDraw
import singularity.graphic.SglDrawConst
import singularity.world.SglFx
import singularity.world.blocks.product.NormalCrafter
import singularity.world.draw.DrawBottom
import singularity.world.draw.DrawRegionDynamic

class PolymerGravitationalGenerator : NormalCrafter("polymer_gravitational_generator") {
  init {
    bundle {
      desc(zh_CN, "聚合引力发生器", "在真空仓内利用大量的能量制造一个引力漏斗,将物质紧密的挤压在一起至中子简并态,用负引力场外壳包裹为一份简并态中子聚合物")
    }
    requirements(
      Category.crafting, IItems.强化合金, 180, IItems.矩阵合金, 900, IItems.充能FEX水晶, 100, IItems.FEX水晶, 120, IItems.铱锭, 80, IItems.气凝胶, 100, IItems.暮光合金, 80
    )
    size = 5
    itemCapacity = 20

    energyCapacity = 4096f
    basicPotentialEnergy = 1024f

    warmupSpeed = 0.0075f


    newConsume()
    consume!!.energy(10f)
    consume!!.items(
      *ItemStack.with(
        IItems.充能FEX水晶, 1, IItems.矩阵合金, 2, IItems.气凝胶, 3, IItems.铱锭, 1
      )
    )
    consume!!.time(240f)
    newProduce()
    produce!!.item(IItems.简并态中子聚合物, 1)

    craftEffect = SglFx.polymerConstructed
    val timeId = timers++

    draw = DrawMulti(DrawBottom(), object : DrawRegionDynamic<NormalCrafterBuild?>("_liquid") {
      init {
        color = Func { e: NormalCrafterBuild? -> SglDrawConst.ion }
        alpha = Floatf { obj: NormalCrafterBuild? -> obj!!.workEfficiency() }
      }
    }, object : DrawRegion("_rotator") {
      init {
        rotateSpeed = 1.75f
        rotation = 45f
      }
    }, object : DrawRegion("_rotator") {
      init {
        rotateSpeed = -1.75f
      }
    }, DrawDefault(), object : DrawBlock() {
      val dist: Distortion = Distortion()
      val taskID: Int = SglDraw.nextTaskID()

      override fun draw(build: Building?) {

        val e = build as NormalCrafterBuild
        Draw.z(Layer.effect)
        Draw.color(Pal.reactorPurple)
        Lines.stroke(0.4f * e.workEfficiency())
        Lines.square(e.x, e.y, 3 + Mathf.random(-0.15f, 0.15f))
        Lines.square(e.x, e.y, 4 + Mathf.random(-0.15f, 0.15f), 45f)

        Draw.z(Layer.flyingUnit + 0.5f)
        dist.setStrength(-32 * e.workEfficiency() * Vars.renderer.scale)
        SglDraw.drawDistortion(taskID, e, dist) { b: NormalCrafterBuild ->
          Distortion.drawVoidDistortion(b.x, b.y, 24 + Mathf.absin(6f, 4f), 32 * b.workEfficiency())
        }

        SglDraw.drawBloomUponFlyUnit(e) { b: NormalCrafterBuild ->
          Draw.color(Pal.reactorPurple)
          Lines.stroke(3 * b.workEfficiency())
          Lines.circle(b.x, b.y, 24 + Mathf.absin(6f, 4f))

          for (p in Geometry.d4) {
            Tmp.v1.set(p.x.toFloat(), p.y.toFloat()).scl(28 + Mathf.absin(6f, 4f)).rotate(Time.time * 0.6f)
            Draw.rect(
              (SglDrawConst.matrixArrow as TextureRegionDrawable).getRegion(), b.x + Tmp.v1.x, b.y + Tmp.v1.y, 8 * b.workEfficiency(), 8 * b.workEfficiency(), Tmp.v1.angle() + 90
            )

            Tmp.v2.set(p.x.toFloat(), p.y.toFloat()).scl(24 + Mathf.absin(6f, 4f)).rotate(Time.time * 0.6f + 45)
            Drawf.tri(b.x + Tmp.v2.x, b.y + Tmp.v2.y, 4 * b.workEfficiency(), 4f, Tmp.v2.angle())
            Drawf.tri(b.x + Tmp.v2.x, b.y + Tmp.v2.y, 3 * b.workEfficiency(), 3f, Tmp.v2.angle() + 180)
          }
          Draw.reset()
        }

        if (e.timer(timeId, 15 / e.workEfficiency())) {
          SglFx.ploymerGravityField.at(e.x, e.y, 24 + Mathf.absin(6f, 4f), Pal.reactorPurple, e)
        }
      }
    })
  }
}