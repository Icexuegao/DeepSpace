package ice.content.block.product

import ice.content.IItems
import ice.world.content.blocks.liquid.SolidPump
import mindustry.type.Category

class 抽水机 :SolidPump("waterPump") {
  init {
    localization {
      zh_CN {
        localizedName = "抽水机"
        description = "抽取地下水资源,但无法抽取地表水资源"
      }
    }
    size = 2
    baseEfficiency = 1f
    pumpAmount = 0.2f
    liquidCapacity = 60f
    newConsume().apply {
      power(1.5f)
    }
    requirements(Category.production, IItems.石英玻璃, 25, IItems.高碳钢, 20, IItems.单晶硅, 10)
  }
}
