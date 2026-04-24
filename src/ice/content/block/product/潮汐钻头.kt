package ice.content.block.product

import arc.graphics.g2d.Draw
import arc.graphics.g2d.Fill
import ice.content.IItems
import ice.content.ILiquids
import mindustry.gen.Building
import mindustry.graphics.Layer
import mindustry.graphics.Pal
import mindustry.type.Category
import mindustry.world.draw.DrawBlock
import mindustry.world.draw.DrawDefault
import mindustry.world.draw.DrawMulti
import mindustry.world.draw.DrawRegion
import singularity.graphic.SglDraw
import singularity.world.blocks.drills.ExtendableDrill
import singularity.world.draw.DrawBottom
import singularity.world.draw.DrawExpandPlasma
import kotlin.math.pow

class 潮汐钻头 :ExtendableDrill("tidal_drill") {
  init {
    localization {
      zh_CN {
        localizedName = "潮汐钻井"
        description =
          "高级钻井,使用最前沿力场控制技术制造的高级钻头,以粒子束冲击破坏挖掘物的物质结构后通过控制引力场震荡完成矿石解体和采集的过程"
      }
    }
    requirements(
      Category.production, IItems.简并态中子聚合物, 50, IItems.强化合金, 120,

      IItems.气凝胶, 90, IItems.充能FEX水晶, 75, IItems.铱锭, 40, IItems.絮凝剂, 60
    )
    size = 4
    squareSprite = false
    energyCapacity = 1024f
    basicPotentialEnergy = 1024f

    itemCapacity = 50
    liquidCapacity = 30f

    bitHardness = 10
    drillTime = 180f

    newConsume()
    consume!!.energy(1.25f)

    newBooster(4.2f)
    consume!!.liquid(ILiquids.相位态FEX流体, 0.15f)
    newBooster(3.1f)
    consume!!.liquid(ILiquids.FEX流体, 0.12f)

    drawers = DrawMulti(
      DrawBottom(), object :DrawExpandPlasma() {
        init {
          plasmas = 2
          plasma1 = Pal.reactorPurple
          plasma2 = Pal.reactorPurple2
        }
      }, DrawDefault(), object :DrawBlock() {
        override fun draw(build: Building) {
          val e = build as ExtendableDrillBuild
          val z = Draw.z()
          Draw.z(Layer.bullet)
          Draw.color(Pal.reactorPurple)
          val lerp = (-2.2 * e.warmup.toDouble().pow(2.0) + 3.2 * e.warmup).toFloat()
          Fill.circle(e.x, e.y, 3 * e.warmup)
          SglDraw.drawLightEdge(
            e.x, e.y, 26 * lerp, 2.5f * lerp, e.rotatorAngle, 1f, 16 * lerp, 2f * lerp, -e.rotatorAngle, 1f
          )
          Draw.z(z)
          Draw.color()
        }
      }, DrawRegion("_top")
    )
  }
}