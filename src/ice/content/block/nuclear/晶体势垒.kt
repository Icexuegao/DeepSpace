package ice.content.block.nuclear

import ice.content.IItems
import ice.ui.bundle.bundle
import singularity.type.SglCategory
import singularity.world.blocks.nuclear.EnergyBuffer

class 晶体势垒: EnergyBuffer("crystal_buffer"){
  init{
    bundle {
      desc(zh_CN, "晶体势垒", "中型能量缓冲设施,具有更大的能量缓冲空间,可进行中压区调压")
    }
    squareSprite = false
    requirements(
      SglCategory.nuclear, IItems.强化合金, 60, IItems.FEX水晶, 75, IItems.气凝胶, 50, IItems.单晶硅, 75, IItems.絮凝剂, 80
    )
    size = 3
    energyCapacity = 4096f
    minPotential = 512f
    maxPotential = 4096f
  }
}