package ice.content.block.product

import arc.graphics.g2d.Draw
import arc.graphics.g2d.Fill
import arc.util.Time
import arc.util.Tmp
import ice.content.IItems
import ice.world.draw.DrawMulti
import mindustry.Vars
import mindustry.gen.Building
import mindustry.graphics.Layer
import mindustry.graphics.Pal
import mindustry.type.Category
import mindustry.world.draw.DrawBlock
import mindustry.world.draw.DrawDefault
import singularity.graphic.SglDrawConst
import singularity.util.MathTransform
import singularity.world.blocks.drills.MatrixMinerComponent
import singularity.world.blocks.drills.MatrixMinerSector.MatrixMinerSectorBuild

class 量子隧穿仪 :MatrixMinerComponent("matrix_miner_pierce") {
  init {

    localization {
      zh_CN {
        localizedName = "量子隧穿仪"
        description = "矩阵矿床的增幅组件,安装此组件后,矩阵矿床将能够透过建筑挖掘被建筑覆盖的矿石"
      }
    }
    requirements(
      Category.production,
      IItems.矩阵合金,
      40,
      IItems.充能FEX水晶,
      40,
      IItems.FEX水晶,
      50,
      IItems.强化合金,
      30,
      IItems.铱锭,
      20,
      IItems.絮凝剂,
      40
    )
    size = 3
    squareSprite = false
    pierceBuild = true
    energyMulti = 4f

    clipSize = (64 * Vars.tilesize).toFloat()

    drawers = DrawMulti(
      DrawDefault(), object :DrawBlock() {
        val param: FloatArray = FloatArray(9)
        val index = arrayOf<String?>("t1", "t2", "t3", "t4")
        val index2 = arrayOf<String?>("t11", "t12", "t13", "t14")
        val indexSelf = arrayOf<String?>("ts1", "ts2", "ts3")

        override fun draw(build: Building) {

          if (build is MatrixMinerComponentBuild) {
            rand.setSeed(build.id.toLong())

            Draw.z(Layer.effect)
            Draw.color(SglDrawConst.matrixNet)
            Fill.circle(build.x, build.y, 2 * build.warmup)
            Draw.color(Pal.reactorPurple)

            for(i in 0..2) {
              for(d in 0..2) {
                param[d * 3] = rand.random(2f, 4f) / (d + 1) * (if (i % 2 == 0) 1 else -1)
                param[d * 3 + 1] = rand.random(0f, 360f)
                param[d * 3 + 2] = rand.random(8f, 20f) / ((d + 1) * (d + 1))
              }
              val v = Tmp.v1.set(MathTransform.fourierSeries(Time.time, *param)).scl(build.warmup)
              Draw.color(Pal.reactorPurple)
              Fill.circle(build.x + v.x, build.y + v.y, build.warmup)

              //   var trail: Trail? = build.getVar(indexSelf[i])
              //    if (trail == null) build.setVar(indexSelf[i], Trail(60).also { trail = it })

              //   trail!!.update(build.x + v.x, build.y + v.y)

              //  trail.draw(Pal.reactorPurple, build.warmup)
            }

            if (build.owner != null) {
              for((ind, plugin) in build.owner!!.plugins.withIndex()) {
                if (plugin is MatrixMinerSectorBuild) {
                  val bool = rand.random(1) > 0.5f
                  for(d in 0..2) {
                    param[d * 3] = rand.random(0.5f, 3f) / (d + 1) * (if (bool != (d % 2 == 0)) 1 else -1)
                    param[d * 3 + 1] = rand.random(0f, 360f)
                    param[d * 3 + 2] = rand.random(16f, 40f) / ((d + 1) * (d + 1))
                  }
                  val v = Tmp.v1.set(MathTransform.fourierSeries(Time.time, *param))

                  for(d in 0..2) {
                    param[d * 3] = rand.random(0.5f, 3f) / (d + 1) * (if (bool != (d % 2 == 0)) -1 else 1)
                    param[d * 3 + 1] = rand.random(0f, 360f)
                    param[d * 3 + 2] = rand.random(12f, 30f) / ((d + 1) * (d + 1))
                  }
                  val v2 = Tmp.v2.set(MathTransform.fourierSeries(Time.time, *param))
                  Draw.color(Pal.reactorPurple)
                  Fill.circle(plugin.drillPos!!.x + v.x, plugin.drillPos!!.y + v.y, 1.5f * build.warmup * plugin.warmup)
                  Fill.circle(plugin.drillPos!!.x + v2.x, plugin.drillPos!!.y + v2.y, build.warmup * plugin.warmup)

                  //   var trail: Trail? = build.getVar(index[ind])
                  //     if (trail == null) build.setVar(index[ind], Trail(72).also { trail = it })
                  //     var trail2: Trail? = build.getVar(index2[ind])
                  //     if (trail2 == null) build.setVar(index2[ind], Trail(72).also { trail2 = it })

                  //   trail!!.draw(Pal.reactorPurple, 1.5f * build.warmup * plugin.warmup)
                  //    trail.update(plugin.drillPos.x + v.x, plugin.drillPos.y + v.y)

                  //    trail2!!.draw(Pal.reactorPurple, build.warmup * plugin.warmup)
                  //    trail2.update(plugin.drillPos.x + v2.x, plugin.drillPos.y + v2.y)
                }
              }
            }
          }
        }
      })
  }
}