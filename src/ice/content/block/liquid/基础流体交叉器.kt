package ice.content.block.liquid

import ice.content.IItems
import ice.world.content.blocks.liquid.LiquidJunction
import mindustry.type.Category

class 基础流体交叉器:LiquidJunction("baseLiquidJunction") {
  init  {
    localization {
      zh_CN {
        localizedName = "基础流体交叉器"
        description = "让两条流体管线交叉通过而互不干扰"
      }
    }
    size = 1
    health = 80
    requirements(Category.liquid, IItems.黄铜锭, 5, IItems.石英玻璃, 5)
  }
}