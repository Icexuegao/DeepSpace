package ice.content.block.nuclear

import ice.content.IItems

import singularity.type.SglCategory
import singularity.world.blocks.nuclear.EnergyBuffer

class 中子缓冲矩阵:EnergyBuffer("neutron_matrix_buffer"){
  init {
    localization {
      zh_CN {
        this.localizedName = "中子缓冲矩阵"
        description = "超大型能量缓冲阵列,复合缓冲具备最大的缓冲容量,其具备从低压到超高压的全域调压范围"
      }
    }
    squareSprite = false
    requirements(
      SglCategory.nuclear,
      IItems.强化合金,
      120,
      IItems.FEX水晶,
      140,
      IItems.充能FEX水晶,
      100,
      IItems.铱锭,
      75,
      IItems.矩阵合金,
      80,
      IItems.絮凝剂,
      100,
      IItems.暮光合金,
      80

    )
    size = 5
    energyCapacity = 65536f
    minPotential = 1f
    maxPotential = 65536f
  }
}