package ice.content.block.effect

import ice.content.IItems
import ice.world.content.blocks.abstractBlocks.IceBlock.Companion.requirements
import mindustry.type.Category
import mindustry.world.blocks.storage.StorageBlock
import universecore.ui.bundle.localization

class 压缩存储器 :StorageBlock("compressorMemory") {
  init {
    localization {
      zh_CN {
        localizedName = "压缩存储器"
        description = "存储各种类型的物品.可以用装卸器卸载物品"
      }
      en {
        localizedName = "Compressed Storage"
        description = "Stores various types of items. Can be unloaded with unloaders."
      }
    }
    size = 2
    armor = 4f
    itemCapacity = 1200
    requirements(Category.effect, IItems.钴锭, 250, IItems.铱板, 150, IItems.钴钢, 100)
  }
}