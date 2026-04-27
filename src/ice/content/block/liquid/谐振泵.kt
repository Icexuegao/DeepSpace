package ice.content.block.liquid

import ice.content.IItems
import ice.ui.bundle.localization
import ice.world.content.blocks.abstractBlocks.IceBlock.Companion.requirements
import mindustry.type.Category
import mindustry.world.blocks.production.Pump

class 谐振泵:Pump("resonancePump") {
  init  {
    localization {
      zh_CN {
        localizedName = "谐振泵"
        description = "快速泵送流体"
      }
    }
    size = 2
    squareSprite = false
    pumpAmount = 0.3f
    liquidCapacity = 80f
    requirements(Category.liquid, IItems.高碳钢, 20, IItems.锌锭, 10, IItems.黄铜锭, 5, IItems.石英玻璃, 10, IItems.铬锭, 10)
  }
}