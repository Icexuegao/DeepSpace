package ice.content.block.effect

import ice.content.IItems
import ice.world.content.blocks.abstractBlocks.IceBlock.Companion.requirements
import mindustry.type.Category
import mindustry.world.blocks.storage.StorageBlock
import universecore.ui.bundle.localization

class 仓库 :StorageBlock("warehouse") {
  init {
    localization {
      zh_CN {
        localizedName = "仓库"
        description = "大量存储各种类型的物品.可以用装卸器卸载物品"
      }
      en {
        localizedName = "Warehouse"
        description = "Stores a large amount of various items. Can be unloaded with unloaders."
      }
    }
    size = 3
    health = 1280
    itemCapacity = 5560
    requirements(Category.effect, IItems.高碳钢, 330, IItems.低碳钢, 120, IItems.铜锭, 65)
  }
}