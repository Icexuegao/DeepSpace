package ice.content.block.nuclear

import ice.content.IItems

import singularity.type.SglCategory
import singularity.world.blocks.nuclear.EnergyBuffer

class 中子缓冲器:EnergyBuffer("energy_buffer"){
  init{
    localization {
      zh_CN {
        name = "中子缓冲器"
        description = "小型能量缓冲设施,用于稳定能量水平和能量升降压,可进行低能区调压"
      }
    }
    requirements(SglCategory.nuclear, IItems.强化合金, 40, IItems.FEX水晶, 50, IItems.气凝胶, 40, IItems.单晶硅, 60)
    squareSprite = false
    size = 2
    energyCapacity = 1024f
    minPotential = 128f
    maxPotential = 1024f
  }
}