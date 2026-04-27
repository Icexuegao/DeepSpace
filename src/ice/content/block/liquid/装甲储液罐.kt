package ice.content.block.liquid

import ice.content.IItems
import ice.world.content.blocks.liquid.base.LiquidRouter
import mindustry.type.Category

class 装甲储液罐 :LiquidRouter("armorLiquidStorage") {
  init {
    localization {
      zh_CN {
        localizedName = "装甲储液罐"
        description = "可以存储大量单一流体.拥有更厚的装甲"
      }
    }
    healAmount = 120f
    health = 3200
    armor = 8f
    size = 4
    liquidPadding = 4f
    liquidCapacity = 6400f
    placeableLiquid = true
    requirements(Category.liquid, IItems.铱板, 85, IItems.陶钢, 55, IItems.石英玻璃, 35)
  }
}