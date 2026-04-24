package ice.content.block.product

import arc.graphics.g2d.Draw
import arc.graphics.g2d.Fill
import arc.graphics.g2d.Lines
import arc.util.Time
import ice.content.IItems
import mindustry.Vars
import mindustry.gen.Building
import mindustry.graphics.Layer
import mindustry.graphics.Pal
import mindustry.type.Category
import mindustry.world.draw.DrawBlock
import mindustry.world.draw.DrawDefault
import mindustry.world.draw.DrawMulti
import singularity.graphic.SglDraw
import singularity.graphic.SglDrawConst
import singularity.world.blocks.drills.MatrixMinerComponent
import singularity.world.blocks.drills.MatrixMinerSector.MatrixMinerSectorBuild

class 谐振增压组件 :MatrixMinerComponent("matrix_miner_extend") {
  init {
    localization {
      zh_CN {
        localizedName = "谐振增压组件"
        description = "矩阵矿床的增幅组件,使矩阵矿床的采集范围增大,可以大幅提高钻头的采掘效率"
      }
    }
    requirements(
      Category.production,
      IItems.矩阵合金,
      40,
      IItems.充能FEX水晶,
      40,
      IItems.强化合金,
      60,
      IItems.铱锭,
      12,
      IItems.简并态中子聚合物,
      20
    )
    size = 3
    squareSprite = false
    drillSize = 5
    energyMulti = 4f
    clipSize = (64 * Vars.tilesize).toFloat()
    drawers = DrawMulti(
      DrawDefault(), object :DrawBlock() {
        override fun draw(build: Building) {
          if (build is MatrixMinerComponentBuild) {
            Draw.z(Layer.effect)
            Draw.color(SglDrawConst.matrixNet)
            Fill.circle(build.x, build.y, 2 * build.warmup)
            Draw.color(Pal.reactorPurple)
            Lines.stroke(2f * build.warmup)
            SglDraw.drawCornerTri(
              build.x, build.y, 20 * build.warmup, 4 * build.warmup, -Time.time * 1.5f, true
            )
            if (build.owner != null) {
              for(plugin in build.owner!!.plugins) {
                if (plugin is MatrixMinerSectorBuild) {
                  Lines.stroke(2f * build.warmup * plugin.warmup)
                  SglDraw.drawCornerTri(
                    plugin.drillPos!!.x,
                    plugin.drillPos!!.y,
                    36 * build.warmup * plugin.warmup,
                    8 * build.warmup * plugin.warmup,
                    -Time.time * 1.5f,
                    true
                  )
                }
              }
            }
          }
        }
      })
  }
}