package ice.content.block.liquid

import ice.content.IItems
import ice.world.content.blocks.distribution.itemNode.TransferNode
import mindustry.type.Category

class 动脉导管桥:TransferNode("bridgeConduitArtery")  {
  init {
    localization {
      zh_CN {
        localizedName = "动脉导管桥"
        description = "消耗电力,向被连接的输出节点长距离传输流体,传输节点面向连接的一侧不可接收流体"
      }
    }
    healAmount = 60f
    allowDiagonal = false
    hasItems = false
    directionAny = false
    armor = 4f
    range = 18
    liquidCapacity = 100f
    placeableLiquid = true
    consumePower(0.5f)
    requirements(Category.liquid, IItems.石英玻璃, 20, IItems.导能回路, 10, IItems.生物钢, 5)
  }
}