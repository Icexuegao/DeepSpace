package ice.content.block.power

import ice.content.IItems
import ice.world.content.blocks.power.WindGenerator
import mindustry.type.Category

class 大型风力发电机 :WindGenerator("windGeneratorLarge") {
  init {
    localization {
      zh_CN {
        localizedName = "大型风力发电机"
        description = "高效依靠风场发电,无需维护即可持续运作,但无法稳定提供电力来源.工作区域内不能放置大型建筑,否则无法工作"
      }
      en {
        localizedName = "Large Wind Generator"
        description =
          "Efficiently generates power using wind fields. Operates continuously without maintenance, but cannot provide a stable power source. Large buildings cannot be placed within its working area, or it will stop functioning."
      }
    }
    size = 4
    health = 500
    range = 5
    basePowerProduction = 270f
    requirements(Category.power, IItems.铬锭, 60, IItems.黄铜锭, 40, IItems.铜锭, 45, IItems.电子元件, 20)
  }
}