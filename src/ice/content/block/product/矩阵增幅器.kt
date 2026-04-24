package ice.content.block.product

import arc.graphics.g2d.Draw
import arc.graphics.g2d.Fill
import arc.graphics.g2d.Lines
import arc.util.Time
import ice.content.IItems
import mindustry.Vars
import mindustry.content.Items
import mindustry.gen.Building
import mindustry.graphics.Layer
import mindustry.graphics.Pal
import mindustry.type.Category
import mindustry.type.Liquid
import mindustry.world.draw.DrawBlock
import mindustry.world.draw.DrawDefault
import mindustry.world.draw.DrawMulti
import singularity.graphic.SglDraw
import singularity.graphic.SglDrawConst
import singularity.world.blocks.drills.MatrixMinerComponent

class 矩阵增幅器 :MatrixMinerComponent("matrix_miner_overdrive") {
  init {
    localization {
      zh_CN {
        localizedName = "矩阵增幅器"
        description = "矩阵矿床的增幅组件,提高矩阵矿床的最大范围,并消耗液体增加矩阵矿床的工作效率"
      }
    }
    requirements(
      Category.production,
      IItems.矩阵合金,
      40,
      IItems.充能FEX水晶,
      50,
      IItems.强化合金,
      40,
      IItems.气凝胶,
      40,
      IItems.铱锭,
      15,
      IItems.絮凝剂,
      60
    )
    size = 3
    range = 16
    drillMoveMulti = 2f
    energyMulti = 2f
    squareSprite = false
    clipSize = (10 * Vars.tilesize).toFloat()

    liquidCapacity = 40f

    newConsume()
    consume!!.time(180f)
    consume!!.item(Items.phaseFabric, 1)

    newBoost(1f, 0.6f, { l: Liquid? -> l!!.heatCapacity >= 0.4f && l.temperature <= 0.5f }, 0.3f)

    drawers = DrawMulti(
      DrawDefault(), object :DrawBlock() {
        override fun draw(build: Building?) {

          if (build is MatrixMinerComponentBuild) {
            Draw.z(Layer.effect)
            Draw.color(SglDrawConst.matrixNet)
            Fill.circle(build.x, build.y, 2 * build.warmup)

            Lines.stroke(1.4f * build.warmup, Pal.reactorPurple)
            SglDraw.dashCircle(build.x, build.y, 10f, 5, 180f, Time.time)

            if (build.owner != null) {
              Lines.stroke(1.6f * build.warmup, Pal.reactorPurple)
              SglDraw.dashCircle(build.owner!!.x, build.owner!!.y, 18f, 6, 180f, -Time.time)
            }
          }
        }
      })
  }
}