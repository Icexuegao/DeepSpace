package ice.content.block.logic

import ice.content.IItems
import ice.world.content.blocks.abstractBlocks.IceBlock.Companion.requirements
import mindustry.type.Category
import mindustry.world.blocks.logic.LogicDisplay
import universecore.ui.bundle.localization

class 逻辑显示屏 :LogicDisplay("logic-display") {
  init {
    localization {
      zh_CN {
        localizedName = "逻辑显示屏"
        description = "显示处理器中绘制的各种图像"
      }
      en {
        localizedName = "Logic Display"
        description = "Displays various images drawn by processors."
      }
    }
    requirements(Category.logic, IItems.石英玻璃, 30, IItems.单晶硅, 30, IItems.金锭, 20, IItems.铬锭, 15, IItems.电子元件, 15)
    size = 3
    displaySize = 80
  }
}
