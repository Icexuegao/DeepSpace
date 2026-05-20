package ice.content.block.logic

import ice.content.IItems
import ice.world.content.blocks.abstractBlocks.IceBlock.Companion.requirements
import mindustry.type.Category
import mindustry.world.blocks.logic.MemoryBlock
import universecore.ui.bundle.localization

class 内存元 :MemoryBlock("memory-cell") {
  init {
    localization {
      zh_CN {
        localizedName = "内存元"
        description = "存储处理器的信息"
      }
      en {
        localizedName = "Memory Cell"
        description = "Stores information for processors."
      }
    }
    requirements(Category.logic, IItems.高碳钢, 50, IItems.铜锭, 30, IItems.单晶硅, 30)
    health = 40
    memoryCapacity = 64
  }
}
