package ice.content.block.power

import arc.graphics.Color
import ice.content.IItems
import ice.graphics.IceColor
import ice.world.content.blocks.abstractBlocks.IceBlock.Companion.requirements
import mindustry.type.Category
import mindustry.world.blocks.power.BeamNode
import universecore.ui.bundle.localization

class 大型能量节点 :BeamNode("powerNodeLarge") {
  init {
    localization {
      zh_CN {
        localizedName = "大型能量节点"
        description = "通过激光束传输电力,可连接多个节点扩展电网范围"
      }
      en {
        localizedName = "Large Power Node"
        description = "Transmits power via laser beam. Can connect multiple nodes to expand the grid range."
      }
    }
    size = 3
    squareSprite = false
    requirements(Category.power, IItems.高碳钢, 10, IItems.锌锭, 35, IItems.单晶硅, 15)
    laserColor1 = IceColor.b4
    laserColor2 = Color.valueOf("bad7e6")
    consumesPower = true
    outputsPower = true
    health = 290
    range = 30
    fogRadius = 3
    buildCostMultiplier = 2.5f
    consumePowerBuffered(1000f)
  }
}