package ice.content.block.liquid

import ice.content.IItems
import ice.content.block.LiquidBlocks
import ice.game.EventType.addContentInitEvent
import ice.world.content.blocks.liquid.Conduit
import mindustry.type.Category

class 动脉导管: Conduit("arteryConduit") {
  init  {
    localization {
      zh_CN {
        localizedName = "动脉导管"
        description = "向前急速传输流体并且不接受侧面输出,同时阻止流体泄露"
      }
    }
    healAmount = 30f
    health = 600
    armor = 2f
    leaks = false
    liquidCapacity = 60f
    liquidPressure = 1.1f
    placeableLiquid = true
    requirements(Category.liquid, IItems.石英玻璃, 1, IItems.铱板, 2, IItems.陶钢, 1, IItems.生物钢, 1)
    addContentInitEvent {
      bridgeReplacement = LiquidBlocks.动脉导管桥
      junctionReplacement = LiquidBlocks.基础流体交叉器
    }
  }
}