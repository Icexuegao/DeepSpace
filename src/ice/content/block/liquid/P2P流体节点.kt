package ice.content.block.liquid

import ice.content.IItems
import ice.world.content.blocks.liquid.P2PLiquidNode
import mindustry.type.Category

class P2P流体节点 :P2PLiquidNode() {
  init {
    localization {
      zh_CN {
        localizedName = "P2P流体节点"
        description = "分散流体交换通信方式"
      }
      en {
        localizedName = "P2P Liquid Node"
        description = "Decentralized fluid exchange communication method."
      }
    }
    size = 2
    health = 500
    requirementPairs(Category.liquid, IItems.铬锭 to 30, IItems.电子元件 to 20, IItems.石英玻璃 to 10, IItems.铝锭 to 20)
    newConsume().apply {
      power(90f / 60f)
    }
  }
}