package ice.content.block.logic

import ice.content.IItems
import ice.world.content.blocks.abstractBlocks.IceBlock.Companion.requirements
import mindustry.type.Category
import mindustry.world.blocks.logic.MessageBlock
import universecore.ui.bundle.localization

class 信息板 :MessageBlock("message") {
  init {
    localization {
      zh_CN {
        localizedName = "信息板"
        description = "保存文字信息,用于队友间进行交流"
      }
      en {
        localizedName = "Message Board"
        description = "Saves text information for communication between teammates."
      }
    }
    health = 40
    requirements(Category.logic, IItems.低碳钢, 5, IItems.高碳钢, 5, IItems.铜锭, 3)
  }
}
