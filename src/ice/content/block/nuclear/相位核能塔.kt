package ice.content.block.nuclear

import ice.content.IItems
import ice.ui.bundle.bundle
import ice.ui.bundle.desc
import singularity.type.SglCategory
import singularity.world.blocks.nuclear.NuclearNode

class 相位核能塔:NuclearNode("phase_pipe_node"){
  init{
    bundle {
      desc(zh_CN, "相位能量塔", "大型中子能运输传输设备,可以承载更高的能量负载和更多的链接数量")
    }
    requirements(SglCategory.nuclear, IItems.强化合金, 24, IItems.FEX水晶, 16, IItems.絮凝剂, 15)
    size = 3
    squareSprite = false
    maxLinks = 18
    linkRange = 22f

    energyCapacity = 16384f
  }
}