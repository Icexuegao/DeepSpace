package ice.content.block.product

import ice.content.IItems
import ice.world.content.blocks.liquid.SolidPump
import mindustry.type.Category

class 大型抽水机 :SolidPump("largeWaterPump") {
  init {
    localization {
      zh_CN {
        localizedName = "大型抽水机"
        description = "高效抽取地下水资源,但无法抽取地表水资源"
      }
    }
    size = 3
    baseEfficiency = 1f
    pumpAmount = 0.6f
    liquidCapacity = 120f
    newConsume().apply {
      power(6f)
    }
    requirements(Category.production, IItems.石英玻璃, 75, IItems.高碳钢, 40, IItems.铬锭, 70, IItems.单晶硅, 60)
  }
}