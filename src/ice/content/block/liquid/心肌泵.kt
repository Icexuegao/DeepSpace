package ice.content.block.liquid

import ice.content.IItems
import ice.ui.bundle.localization
import ice.world.content.blocks.abstractBlocks.IceBlock.Companion.requirements
import mindustry.type.Category
import mindustry.world.blocks.production.Pump

class 心肌泵 :Pump("myocardialPump") {
  init {
    localization {
      zh_CN {
        localizedName = "心肌泵"
        description = "急速泵送流体,需要电力"
      }
    }
    size = 4
    squareSprite = false
    pumpAmount = 0.625f
    liquidCapacity = 240f
    consumePower(8f)
    requirements(
      Category.liquid,
      IItems.石英玻璃,
      120,
      IItems.铱板,
      120,
      IItems.导能回路,
      85,
      IItems.陶钢,
      45,
      IItems.生物钢,
      15
    )
  }
}