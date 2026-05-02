package ice.content.block.liquid

import ice.content.IItems
import ice.content.block.LiquidBlocks
import ice.game.EventType.addContentInitEvent
import ice.world.content.blocks.liquid.Conduit
import mindustry.type.Category

class 流金导管:Conduit("fluxGoldConduit")  {
  init {
    localization {
      zh_CN {
        localizedName = "流金导管"
        description = "向前快速传输流体"
      }
    }
    health = 100
    liquidCapacity = 40f
    liquidPressure = 1.025f
    requirements(Category.liquid, IItems.金锭, 2, IItems.锌锭, 1, IItems.石英玻璃, 1)
    addContentInitEvent {
      bridgeReplacement = LiquidBlocks.基础导管桥
      junctionReplacement = LiquidBlocks.基础流体交叉器
    }
  }
}
