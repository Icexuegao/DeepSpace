package ice.content.block.effect

import ice.content.IItems
import ice.world.content.blocks.abstractBlocks.IceBlock.Companion.requirements
import mindustry.type.Category
import mindustry.world.blocks.storage.StorageBlock
import universecore.ui.bundle.localization

class 盒子 :StorageBlock("box") {
  init {
    localization {
      zh_CN {
        localizedName = "盒子"
        description = "微量存储各种类型的物品.可以用装卸器卸载物品"
        details = "经典回归之这个小盒就是你永远的家"
      }
      en {
        localizedName = "Box"
        description = "Stores a small amount of various items. Can be unloaded with unloaders."
        details = "A classic return - this little box is your forever home."
      }
    }
    size = 1
    health = 180
    itemCapacity = 60
    requirements(Category.effect, IItems.高碳钢, 30, IItems.低碳钢, 10, IItems.铜锭, 15)
  }
}