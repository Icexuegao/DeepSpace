package ice.content.block.nuclear

import ice.content.IItems

import singularity.type.SglCategory
import singularity.world.blocks.nuclear.EnergyBuffer

class 高压缓冲器:EnergyBuffer("high_voltage_buffer"){
  init {
    localization {
      zh_CN {
        this.localizedName = "高压缓冲器"
        description = "大型能量缓冲设施,更大的缓冲空间基本可以满足任何情况的能量缓冲,可用于进行高压区调压"
      }
    }
    squareSprite = false
    requirements(
      SglCategory.nuclear,
      IItems.强化合金,
      90,
      IItems.FEX水晶,
      120,
      IItems.充能FEX水晶,
      80,
      IItems.铱锭,
      50,
      IItems.单晶硅,
      125,
      IItems.絮凝剂,
      90,
      IItems.暮光合金,
      80

    )
    size = 4
    energyCapacity = 16384f
    minPotential = 2048f
    maxPotential = 16384f
  }
}