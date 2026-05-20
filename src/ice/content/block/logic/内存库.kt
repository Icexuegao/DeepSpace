package ice.content.block.logic

import ice.content.IItems
import ice.world.content.blocks.abstractBlocks.IceBlock.Companion.requirements
import mindustry.type.Category
import mindustry.world.blocks.logic.MemoryBlock
import universecore.ui.bundle.localization

class 内存库 :MemoryBlock("memory-bank") {
  init {
    localization {
      zh_CN {
        localizedName = "内存库"
        description = "存储处理器的信息,容量更大"
      }
      en {
        localizedName = "Memory Bank"
        description = "Stores information for processors. Has a larger capacity."
      }
    }
    requirements(Category.logic, IItems.钴钢, 90, IItems.陶钢, 30, IItems.导能回路, 40, IItems.铪锭, 30)
    health = 200
    memoryCapacity = 512
    size = 2
  }
}
