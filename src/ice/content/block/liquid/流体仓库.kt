package ice.content.block.liquid

import ice.content.IItems
import ice.world.content.blocks.liquid.base.LiquidRouter
import mindustry.type.Category

class 流体仓库:LiquidRouter("liquidStorage")  {
  init {
    localization {
      zh_CN {
        localizedName = "流体仓库"
        description = "可以存储大量单一流体"
      }
    }
    size = 3
    solid = true
    health = 1000
    squareSprite = false
    liquidPadding = 6f / 4f
    liquidCapacity = 2000f
    requirements(Category.liquid, IItems.铜锭, 50, IItems.石英玻璃, 30)
  }
}