package ice.content.block.effect

import ice.content.IItems
import ice.ui.bundle.bundle
import ice.ui.bundle.desc
import ice.world.content.blocks.abstractBlocks.IceBlock.Companion.requirements
import mindustry.type.Category
import mindustry.world.blocks.storage.StorageBlock

class 盒子 :StorageBlock("box") {
  init {
    size = 1
    health = 180
    itemCapacity = 60
    requirements(Category.effect, IItems.高碳钢, 30, IItems.低碳钢, 10, IItems.铜锭, 15)
    bundle {
      desc(zh_CN, "盒子", "微量存储各种类型的物品.可以用装卸器卸载物品", "经典回归之这个小盒就是你永远的家")
    }
  }
}