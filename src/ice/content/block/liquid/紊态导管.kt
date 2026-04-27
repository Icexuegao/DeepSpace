package ice.content.block.liquid

import ice.content.IItems
import ice.content.block.LiquidBlocks
import ice.library.EventType.addContentInitEvent
import ice.world.content.blocks.liquid.ArmoredConduit
import mindustry.type.Category

class 紊态导管:ArmoredConduit("disorderedConduit")  {
  init {
    localization {
      zh_CN {
        localizedName = "紊态导管"
        description = "向前快速传输流体并且不接受侧面输出,同时阻止流体泄露"
      }
    }
    leaks = false
    liquidCapacity = 40f
    liquidPressure = 1.025f
    requirements(Category.liquid, IItems.钴钢, 1, IItems.铅锭, 2, IItems.石英玻璃, 1)
    addContentInitEvent {
      bridgeReplacement = LiquidBlocks.导管桥
      junctionReplacement = LiquidBlocks.基础流体交叉器
    }
  }
}