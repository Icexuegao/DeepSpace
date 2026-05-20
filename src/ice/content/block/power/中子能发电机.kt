package ice.content.block.power

import ice.content.IItems
import mindustry.graphics.Pal
import mindustry.type.Category
import mindustry.world.draw.DrawDefault
import mindustry.world.draw.DrawMulti
import mindustry.world.draw.DrawPlasma
import mindustry.world.draw.DrawRegion
import singularity.world.blocks.product.NormalCrafter
import singularity.world.draw.DrawBottom

class 中子能发电机 :NormalCrafter("neutron_generator") {
  init {
    localization {
      zh_CN {
        this.localizedName = "中子能发电机"
        description = "分解中子生产大量电力"
      }
    }
    requirements(
      Category.power, IItems.强化合金, 100, IItems.充能FEX水晶, 80, IItems.铀238, 75, IItems.絮凝剂, 70, IItems.气凝胶, 90
    )
    size = 3

    energyCapacity = 1024f
    basicPotentialEnergy = 256f
    warmupSpeed = 0.0075f

    newConsume()
    consume!!.energy(4f)
    newProduce()
    produce!!.power(50f)

    drawers = DrawMulti(
      DrawBottom(), DrawDefault(), object :DrawPlasma() {
        init {
          suffix = "_plasma_"
          plasma1 = Pal.reactorPurple
          plasma2 = Pal.reactorPurple2
        }
      }, DrawRegion("_top")
    )
  }
}