package ice.content.block.effect

import ice.content.IItems
import ice.ui.bundle.bundle
import ice.ui.bundle.desc
import ice.world.content.blocks.abstractBlocks.IceBlock.Companion.requirements
import mindustry.type.Category
import mindustry.world.blocks.storage.StorageBlock

class 仓库:StorageBlock("warehouse"){
  init{
    size = 3
    health = 1280
    itemCapacity = 5560
    requirements(Category.effect, IItems.高碳钢, 330, IItems.低碳钢, 120, IItems.铜锭, 65)
    bundle {
      desc(zh_CN, "仓库", "大量存储各种类型的物品.可以用装卸器卸载物品")
    }
  }
}