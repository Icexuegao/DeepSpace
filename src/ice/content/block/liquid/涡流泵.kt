package ice.content.block.liquid

import ice.content.IItems
import ice.ui.bundle.localization
import ice.world.content.blocks.abstractBlocks.IceBlock.Companion.requirements
import mindustry.type.Category
import mindustry.world.blocks.production.Pump
import mindustry.world.draw.DrawDefault
import mindustry.world.draw.DrawMulti
import mindustry.world.draw.DrawPumpLiquid
import mindustry.world.draw.DrawRegion

class 涡流泵:Pump("vortexPump") {
  init  {
    localization {
      zh_CN {
        localizedName = "涡流泵"
        description = "急速泵送流体"
      }
    }
    size = 3
    squareSprite = false
    pumpAmount = 0.5f
    liquidCapacity = 180f
    drawer = DrawMulti(DrawRegion("-bottom"), DrawPumpLiquid(), DrawDefault())
    requirements(Category.liquid, IItems.铬锭, 50, IItems.锌锭, 40, IItems.黄铜锭, 25, IItems.石英玻璃, 30, IItems.钴锭, 30)
  }
}