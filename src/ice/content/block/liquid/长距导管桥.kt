package ice.content.block.liquid

import ice.content.IItems
import ice.world.content.blocks.distribution.itemNode.TransferNode
import mindustry.type.Category

class 长距导管桥:TransferNode("bridgeConduitLarge") {
  init  {
    localization {
      zh_CN {
        localizedName = "长距导管桥"
        description = "消耗电力,向任意方向长距离传输流体,4个方向皆可输入输出"
      }
    }
    range = 10
    hasItems = false
    liquidCapacity = 30f
    consumePower(10f / 60f)
    requirements(Category.liquid, IItems.单晶硅, 6, IItems.铜锭, 8, IItems.锌锭, 10, IItems.石英玻璃, 20)
  }
}