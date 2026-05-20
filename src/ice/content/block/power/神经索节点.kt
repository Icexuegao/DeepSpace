package ice.content.block.power

import arc.graphics.Color
import ice.content.IItems
import ice.world.content.blocks.power.PowerNode
import mindustry.type.Category

class 神经索节点:PowerNode("neuralNode"){
  init {
    squareSprite = false
    healAmount = 5f
    size = 1
    armor = 4f
    maxNodes = 12
    laserRange = 12f
    consumesPower = true
    outputsPower = true
    consumePowerBuffered(16000f)
    laserColor1 = Color.valueOf("E78F92")
    laserColor2 = Color.valueOf("D75B6E")
    requirements(Category.power, IItems.铱板, 5, IItems.导能回路, 2, IItems.生物钢, 1)
    localization {
      zh_CN {
        this.localizedName = "神经索节点"
        description = "向连接的建筑传输电力"
      }
    }
  }
}