package ice.content.block.unit

import ice.content.IItems
import ice.world.content.blocks.abstractBlocks.IceBlock.Companion.requirements
import mindustry.type.Category
import mindustry.type.Item
import mindustry.type.ItemStack
import mindustry.world.blocks.units.UnitFactory
import universecore.ui.bundle.localization

class 装配厂 :UnitFactory("block_assemblyPlant") {
  infix fun Item.to(that: Int): ItemStack = ItemStack(this, that)

  init {
    localization {
      zh_CN {
        localizedName = "装配厂"
        description = "生成基础单位"
      }
    }
    size = 3
    health = 600
    requirements(Category.units, IItems.铬锭 to 300, IItems.铜锭 to 120, IItems.单晶硅 to 70, IItems.黄铜锭 to 30)
  }
}