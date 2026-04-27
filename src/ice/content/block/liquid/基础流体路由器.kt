package ice.content.block.liquid

import ice.content.IItems
import ice.world.content.blocks.liquid.base.LiquidRouter
import mindustry.type.Category

class 基础流体路由器:LiquidRouter("baseLiquidRouter")  {
  init {
    localization {
      zh_CN {
        localizedName = "基础流体路由器"
        description = "将一个方向的流体平均输出到其他3个方向,可以储存一定量的流体"
      }
    }
    liquidCapacity = 50f
    size = 1
    health = 100
    requirements(Category.liquid, IItems.铜锭, 4, IItems.石英玻璃, 2)
  }
}