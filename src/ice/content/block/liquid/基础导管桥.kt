package ice.content.block.liquid

import ice.content.IItems
import ice.world.content.blocks.distribution.itemNode.TransferNode
import mindustry.type.Category

class 基础导管桥 :TransferNode("baseBridgeConduit") {
  init {
    localization {
      zh_CN {
        localizedName = "基础导管桥"
        description = "向被连接的输出节点传输流体,传输节点面向连接的一侧不可接收流体"
      }
    }
    directionAny = false
    range = 5
    hasPower = false
    arrowSpacing = 6f
    liquidCapacity = 50f
    placeableLiquid = true
    requirements(Category.liquid, IItems.高碳钢, 2, IItems.锌锭, 5, IItems.石英玻璃, 5)
  }
}