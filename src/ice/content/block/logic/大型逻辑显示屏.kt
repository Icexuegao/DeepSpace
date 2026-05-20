package ice.content.block.logic

import ice.content.IItems
import ice.world.content.blocks.abstractBlocks.IceBlock.Companion.requirements
import mindustry.type.Category
import mindustry.world.blocks.logic.LogicDisplay
import universecore.ui.bundle.localization

class 大型逻辑显示屏 :LogicDisplay("large-logic-display") {
  init {
    localization {
      zh_CN {
        localizedName = "大型逻辑显示屏"
        description = "显示处理器中绘制的各种图像"
      }
      en {
        localizedName = "Large Logic Display"
        description = "Displays various images drawn by processors."
      }
    }
    requirements(Category.logic, IItems.石英玻璃, 60, IItems.单晶硅, 60, IItems.金锭, 40, IItems.铬锭, 30, IItems.电子元件, 30)
    size = 6
    displaySize = 176
  }
}
