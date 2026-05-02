package ice.content.block.liquid

import ice.content.IItems
import ice.world.content.blocks.distribution.itemNode.TransferNode
import mindustry.type.Category

class 装甲导管桥:TransferNode("bridgeConduitArmored")  {
  init {
    localization {
      zh_CN {
        localizedName = "装甲导管桥"
        description = "向被连接的输出节点传输流体,传输节点面向连接的一侧不可接收流体.拥有更厚的装甲"
      }
    }
    health = 220
    directionAny = false
    armor = 4f
    allowDiagonal = false
    range = 10
    fadeIn = false
    hasItems = false
    bridgeWidth = 8f
    hasPower = false
    arrowSpacing = 6f
    liquidCapacity = 120f
    placeableLiquid = true
    requirements(Category.liquid, IItems.石英玻璃, 8, IItems.陶钢, 3, IItems.铱板, 5)
  }
}
