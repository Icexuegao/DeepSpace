package ice.content.block.liquid

import ice.content.IItems
import ice.ui.bundle.localization
import ice.world.content.blocks.abstractBlocks.IceBlock.Companion.requirements
import mindustry.type.Category
import mindustry.world.blocks.production.Pump

class 动力泵 :Pump("kineticPump") {
  init {
    localization {
      zh_CN {
        localizedName = "动力泵"
        description = "泵送流体"
      }
    }
    size = 1
    squareSprite = false
    pumpAmount = 0.2f
    liquidCapacity = 20f
    requirements(Category.liquid, IItems.高碳钢, 20, IItems.锌锭, 5)
  }
}