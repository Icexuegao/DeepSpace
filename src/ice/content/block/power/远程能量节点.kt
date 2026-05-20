package ice.content.block.power

import ice.content.IItems
import ice.world.content.blocks.abstractBlocks.IceBlock.Companion.requirements
import ice.world.content.blocks.power.PowerNode
import mindustry.type.Category

class 远程能量节点:PowerNode("remotePowerNode"){
  init  {
    size = 3
    armor = 3f
    maxNodes = 4
    laserRange = 100f
    squareSprite = false
    consumesPower = true
    outputsPower = true
    consumePowerBuffered(50000f)
    requirements(Category.power, IItems.铅锭, 15, IItems.铱板, 15, IItems.导能回路, 10, IItems.暮光合金, 5)
    localization {
      zh_CN {
        this.localizedName = "远程能量节点"
        description = "向超大范围连接的建筑传输电力"
      }
    }
  }
}