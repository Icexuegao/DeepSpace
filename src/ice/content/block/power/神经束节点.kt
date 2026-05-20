package ice.content.block.power

import arc.graphics.Color
import ice.content.IItems
import ice.world.content.blocks.power.PowerNode
import mindustry.type.Category

class 神经束节点 :PowerNode("neuralBeamNode") {
  init {
    localization {
      zh_CN {
        localizedName = "神经束节点"
        description = "向更大范围连接的建筑传输电力"
      }
      en {
        localizedName = "Neural Beam Node"
        description = "Transmits power to buildings within a larger connected range."
      }
    }
    squareSprite = false
    healAmount = 20f
    size = 2
    armor = 8f
    maxNodes = 24
    laserRange = 24f
    consumesPower = true
    outputsPower = true
    consumePowerBuffered(80000f)
    laserColor1 = Color.valueOf("E78F92")
    laserColor2 = Color.valueOf("D75B6E")
    requirements(Category.power, IItems.铱板, 10, IItems.导能回路, 5, IItems.生物钢, 1)
  }
}