package ice.content.block.power

import ice.content.IItems
import ice.world.content.blocks.abstractBlocks.IceBlock.Companion.requirements
import mindustry.type.Category
import mindustry.world.blocks.power.SolarGenerator
import universecore.ui.bundle.localization

class 光伏板:SolarGenerator("solarPanel"){
  init {
    localization {
      zh_CN {
        this.localizedName = "光伏板"
        description = "利用恒星光产生电力,无需维护即可持续运作,提供基础能源支持"
      }
    }
    size = 2
    powerProduction = 58f / 60f
    requirements(Category.power, IItems.低碳钢, 35, IItems.锌锭, 20, IItems.单晶硅, 10)
  }
}