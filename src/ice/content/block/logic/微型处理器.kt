package ice.content.block.logic

import ice.content.IItems
import ice.world.content.blocks.abstractBlocks.IceBlock.Companion.requirements
import mindustry.type.Category
import mindustry.world.blocks.logic.LogicBlock
import universecore.ui.bundle.localization

class 微型处理器 :LogicBlock("micro-processor") {
  init {
    localization {
      zh_CN {
        localizedName = "微型处理器"
        description = "循环运行一系列逻辑指令,可用于控制单位和建筑物"
      }
      en {
        localizedName = "Micro Processor"
        description = "Runs a series of logic instructions in a loop. Can be used to control units and buildings."
      }
    }
    requirements(Category.logic, IItems.低碳钢, 50, IItems.高碳钢, 50, IItems.铜锭, 20, IItems.单晶硅, 20)
    health = 40
    instructionsPerTick = 3
    range = (8 * 15).toFloat()
    size = 1
  }
}
