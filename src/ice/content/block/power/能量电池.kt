package ice.content.block.power

import ice.content.IItems
import ice.graphics.IceColor
import ice.world.content.blocks.abstractBlocks.IceBlock.Companion.requirements
import mindustry.type.Category
import mindustry.world.blocks.power.Battery
import universecore.ui.bundle.localization

class 能量电池:Battery("powerBattery"){
  init {
    size = 2
    health = 300
    squareSprite = false
    baseExplosiveness = 1f
    emptyLightColor = IceColor.df
    fullLightColor = IceColor.b4
    consumePowerBuffered(15000f)
    requirements(Category.power, IItems.低碳钢, 10, IItems.高碳钢, 20, IItems.黄铜锭, 30, IItems.铅锭, 50)
    localization {
      zh_CN {
        this.localizedName = "能量电池"
        description = "存储大量电力,受损会发生爆炸"
      }
    }
  }
}