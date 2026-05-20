package ice.content.block.logic

import ice.content.IItems
import ice.world.content.blocks.abstractBlocks.IceBlock.Companion.requirements
import mindustry.type.Category
import mindustry.world.blocks.logic.LogicBlock
import universecore.ui.bundle.localization

class 逻辑处理器 :LogicBlock("logic-processor") {
  init {
    localization {
      zh_CN {
        localizedName = "逻辑处理器"
        description = "循环运行一系列逻辑指令,可用于控制单位和建筑物,比微型处理器更快"
      }
      en {
        localizedName = "Logic Processor"
        description =
          "Runs a series of logic instructions in a loop. Can be used to control units and buildings. Faster than the micro processor."
      }
    }
    requirements(Category.logic, IItems.铝锭, 50, IItems.石墨烯, 50, IItems.金锭, 30, IItems.钴钢, 110, IItems.导能回路, 50)
    health = 200
    instructionsPerTick = 11
    range = (8 * 32).toFloat()
    size = 2
  }
}
