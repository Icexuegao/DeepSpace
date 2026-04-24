package ice.content.block.product

import ice.content.IItems
import mindustry.Vars
import mindustry.type.Category
import mindustry.type.ItemStack
import singularity.world.blocks.drills.MatrixMinerSector

class 采掘扇区 :MatrixMinerSector("matrix_miner_node") {
  init {
    localization {
      zh_CN {
        localizedName = "采掘扇区"
        description = "矩阵矿床的采掘工作组件,提供一个基础开采角度区间"
      }
    }
    requirements(
      Category.production, ItemStack.with(
        IItems.矩阵合金, 30, IItems.充能FEX水晶, 25, IItems.强化合金, 16, IItems.气凝胶, 20
      )
    )
    size = 3
    drillSize = 3
    squareSprite = false
    clipSize = (64 * Vars.tilesize).toFloat()
    energyMulti = 2f
  }
}