package ice.content.block.power

import arc.graphics.Color
import ice.content.IItems
import ice.graphics.IceColor
import ice.world.content.blocks.abstractBlocks.IceBlock.Companion.requirements
import mindustry.type.Category
import mindustry.world.blocks.power.BeamNode
import universecore.ui.bundle.localization

class 能量节点:BeamNode("powerNode"){
  init {
    localization {
      zh_CN {
        this.localizedName = "能量节点"
        description = "通过激光束传输电力,可连接多个节点扩展电网范围"
      }
    }
    squareSprite = false
    requirements(Category.power, IItems.高碳钢, 2, IItems.锌锭, 5, IItems.铜锭, 5)
    laserColor1 = IceColor.b4
    laserColor2 = Color.valueOf("bad7e6")
    consumesPower = true
    outputsPower = true
    health = 90
    range = 10
    fogRadius = 1
    buildCostMultiplier = 2.5f
    consumePowerBuffered(200f)
  }
}