package ice.content.block.product

import arc.graphics.g2d.Draw
import arc.graphics.g2d.Fill
import arc.graphics.g2d.Lines
import arc.util.Time
import ice.content.IItems
import ice.world.draw.DrawBuild
import mindustry.Vars
import mindustry.graphics.Layer
import mindustry.graphics.Pal
import mindustry.type.Category
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
    requirementPairs(
      Category.production,
      IItems.矩阵合金 to 40,
      IItems.充能FEX水晶 to 50,
      IItems.强化合金 to 40,
      IItems.气凝胶 to 40,
      IItems.铱锭 to 15,
      IItems.絮凝剂 to 60
    )
    size = 3
    range = 16
    drillMoveMulti = 2f
    energyMulti = 2f
    squareSprite = false
    clipSize = (10 * Vars.tilesize).toFloat()

    liquidCapacity = 40f

    newConsume().apply {
      time(180f)
      item(IItems.铪锭, 1)
    }

    newBoost(1f, 0.6f, { l -> l.heatCapacity >= 0.4f && l.temperature <= 0.5f }, 0.3f)

    drawers = DrawMulti(DrawDefault(), DrawBuild<MatrixMinerComponentBuild> {
      Draw.z(Layer.effect)
      Draw.color(SglDrawConst.matrixNet)
      Fill.circle(x, y, 2 * warmup)

      Lines.stroke(1.4f * warmup, Pal.reactorPurple)
      SglDraw.dashCircle(x, y, 10f, 5, 180f, Time.time)

      owner?.let {
        Lines.stroke(1.6f * warmup, Pal.reactorPurple)
        SglDraw.dashCircle(it.x, it.y, 18f, 6, 180f, -Time.time)
      }
    })
  }
}