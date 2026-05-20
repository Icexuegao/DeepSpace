package ice.content.block.power

import ice.content.IItems
import ice.graphics.IceColor
import ice.world.content.blocks.abstractBlocks.IceBlock.Companion.requirements
import mindustry.type.Category
import mindustry.world.blocks.power.Battery
import universecore.ui.bundle.localization

class 小型能量电池:Battery("smallPowerBattery"){
  init {
    size = 1
    health = 50
    baseExplosiveness = 1f
    emptyLightColor = IceColor.df
    fullLightColor = IceColor.b4
    consumePowerBuffered(3500f)
    requirements(Category.power, IItems.低碳钢, 5, IItems.高碳钢, 20, IItems.铅锭, 20)
    localization {
      zh_CN {
        this.localizedName = "小型能量电池"
        description = "存储少量电力,受损会发生爆炸"
      }
    }
  }
}