package ice.content.block.product

import ice.content.IItems
import mindustry.type.Category
import singularity.world.blocks.drills.MatrixMiner

class 矩阵矿床 :MatrixMiner("matrix_miner") {
  init {
    localization {
      zh_CN {
        localizedName = "矩阵矿床"
        description = "矩阵矿床的控制中心,四面可安装矿床的工作组件以进行开采工作"
      }
    }
    requirements(
      Category.production,
      IItems.矩阵合金,
      130,
      IItems.充能FEX水晶,
      80,
      IItems.强化合金,
      90,
      IItems.气凝胶,
      90,
      IItems.絮凝剂,
      65,
      IItems.锌锭,
      90,
      IItems.铱锭,
      45
    )
    size = 5
    matrixEnergyUse = 0.6f
    squareSprite = false
    baseRange = 32
  }
}