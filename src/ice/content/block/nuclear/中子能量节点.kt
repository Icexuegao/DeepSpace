package ice.content.block.nuclear

import ice.content.IItems

import singularity.type.SglCategory
import singularity.world.blocks.nuclear.NuclearNode

class 中子能量节点:NuclearNode("nuclear_pipe_node"){
  init{
    localization {
      zh_CN {
        name = "中子能量节点"
        description = "中子能传输节点,用于传输核能量,以链接多个节点的方式构建核能运输网络"
      }
    }
    requirements(SglCategory.nuclear, IItems.强化合金, 8, IItems.FEX水晶, 4)
    size = 2
    squareSprite = false
    energyCapacity = 4096f
  }
}