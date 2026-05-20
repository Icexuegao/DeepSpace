package ice.content.block.effect

import ice.content.IItems

import ice.world.content.blocks.science.Laboratory
import mindustry.type.Category

class 基础实验室 :Laboratory("laboratory") {
  init {
    localization {
      zh_CN {
        localizedName = "基础实验室"
        description = "提供资源后会缓慢研究选定科技.可配置"
      }
      en {
        localizedName = "Basic Laboratory"
        description = "Slowly researches selected technologies when supplied with resources. Configurable."
      }
    }
    consumePower(100f / 60)
    itemCapacity = 100
    alwaysUnlocked = true
    requirements(Category.effect, IItems.高碳钢, 50, IItems.低碳钢, 50, IItems.铜锭, 50)
  }
}