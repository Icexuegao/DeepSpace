package ice.content.block.liquid

import ice.content.IItems
import ice.world.content.blocks.liquid.base.LiquidRouter
import mindustry.type.Category

class 装甲流体路由器:LiquidRouter("armoredLiquidRouter")  {
  init {
    localization {
      zh_CN {
        localizedName = "装甲流体路由器"
        description = "将一个方向的流体平均输出到其他3个方向,可以储存一定量的流体.拥有更厚的装甲"
      }
    }
    armor = 4f
    liquidCapacity = 80f
    liquidPressure = 1.1f
    solid = false
    underBullets = true
    placeableLiquid = true
    requirements(Category.liquid, IItems.石英玻璃, 2, IItems.陶钢, 1, IItems.铱板, 3)
  }
}