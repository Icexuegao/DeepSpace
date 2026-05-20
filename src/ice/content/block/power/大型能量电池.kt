package ice.content.block.power

import ice.content.IItems
import ice.graphics.IceColor
import ice.world.content.blocks.abstractBlocks.IceBlock.Companion.requirements
import mindustry.type.Category
import mindustry.world.blocks.power.Battery
import universecore.ui.bundle.localization

class 大型能量电池 :Battery("largePowerBattery") {
  init {
    localization {
      zh_CN {
        localizedName = "大型能量电池"
        description = "存储巨量电力,受损会发生爆炸"
      }
      en {
        localizedName = "Large Power Battery"
        description = "Stores a massive amount of power. Will explode when damaged."
      }
    }
    size = 4
    armor = 4f
    absorbLasers = true
    baseExplosiveness = size.toFloat()
    emptyLightColor = IceColor.df
    fullLightColor = IceColor.b4
    consumePowerBuffered(1000000f)
    requirements(Category.power, IItems.铅锭, 150, IItems.铱板, 145, IItems.导能回路, 85, IItems.陶钢, 30)
  }
}