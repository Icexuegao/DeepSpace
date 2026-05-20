package ice.content.block.logic

import ice.content.IItems
import ice.world.content.blocks.abstractBlocks.IceBlock.Companion.requirements
import mindustry.type.Category
import mindustry.world.blocks.logic.SwitchBlock
import universecore.ui.bundle.localization

class 开关 :SwitchBlock("switch") {
  init {
    localization {
      zh_CN {
        localizedName = "开关"
        description = "可切换的开关,开关状态可以用逻辑处理器读取和控制"
      }
      en {
        localizedName = "Switch"
        description = "A toggleable switch. Its state can be read and controlled by a logic processor."
      }
    }
    health = 40
    requirements(Category.logic, IItems.低碳钢, 5, IItems.高碳钢, 5, IItems.铅锭, 3)
  }
}
