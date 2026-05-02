package ice.content.block.liquid

import ice.content.IItems
import ice.world.content.blocks.distribution.itemNode.TransferNode
import mindustry.type.Category

class 导管桥:TransferNode("bridgeConduit")  {
  init {
    localization {
      zh_CN {
        localizedName = "导管桥"
        description = "向任意方向传输流体,4个方向皆可输入输出"
      }
    }
    health = 30
    range = 6
    hasItems = false
    hasPower = false
    liquidCapacity = 20f
    requirements(Category.liquid, IItems.单晶硅, 3, IItems.锌锭, 5, IItems.石英玻璃, 10)
  }
}
