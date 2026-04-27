package ice.content.block.liquid

import ice.content.IItems
import ice.world.content.blocks.liquid.base.LiquidRouter
import mindustry.type.Category

class 流体容器 :LiquidRouter("liquidContainer") {
  init {
    localization {
      zh_CN {
        localizedName = "流体容器"
        description = "可以储存少量单一流体"
      }
    }
    size = 2
    solid = true
    health = 500
    squareSprite = false
    liquidPadding = 6f / 4f
    liquidCapacity = 800f
    requirements(Category.liquid, IItems.铜锭, 20, IItems.石英玻璃, 15)
  }
}