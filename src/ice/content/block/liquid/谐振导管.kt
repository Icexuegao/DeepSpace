package ice.content.block.liquid

import ice.content.IItems
import ice.content.block.LiquidBlocks
import ice.game.EventType.addContentInitEvent
import ice.world.content.blocks.liquid.Conduit
import mindustry.type.Category

class 谐振导管:Conduit("resonanceConduit")  {
  init {
    localization {
      zh_CN {
        localizedName = "谐振导管"
        description = "向前传输流体"
      }
    }
    liquidCapacity = 20f
    requirements(Category.liquid, IItems.高碳钢, 1, IItems.锌锭, 1, IItems.石英玻璃, 1)
    addContentInitEvent {
      bridgeReplacement = LiquidBlocks.基础导管桥
      junctionReplacement = LiquidBlocks.基础流体交叉器
    }
  }
}