package ice.content.block.unit

import ice.content.IItems
import ice.world.content.blocks.abstractBlocks.IceBlock.Companion.requirements
import mindustry.content.UnitTypes
import mindustry.type.Category
import mindustry.type.Item
import mindustry.type.ItemStack
import mindustry.world.blocks.units.Reconstructor
import universecore.ui.bundle.localization

class 构建仓 :Reconstructor("block_buildWarehouses") {
  infix fun Item.to(that: Int): ItemStack = ItemStack(this, that)

  init {
    localization {
      zh_CN {
        localizedName = "构建仓"
        description = "升级基础单位"
      }
    }
    size = 3
    health = 400
    requirements(Category.units, IItems.钴锭 to 200, IItems.铅锭 to 220, IItems.铪锭 to 170, IItems.黄铜锭 to 20)

    upgrades.addAll(arrayOf(UnitTypes.nova, UnitTypes.pulsar))
  }
}