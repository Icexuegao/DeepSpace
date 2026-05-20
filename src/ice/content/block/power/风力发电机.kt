package ice.content.block.power

import ice.content.IItems
import ice.world.content.blocks.power.WindGenerator
import mindustry.type.Category

class 风力发电机 :WindGenerator("windGenerator") {
  init {
    localization {
      zh_CN {
        localizedName = "风力发电机"
        description = "依靠风场发电,无需维护即可持续运作,但无法稳定提供电力来源.工作区域内不能放置大型建筑,否则无法工作"
      }
    }
    basePowerProduction = 70f
    size = 2
    range = 2
    health = 100
    requirements(Category.power, IItems.铅锭, 20, IItems.黄铜锭, 30, IItems.铜锭, 15, IItems.单晶硅, 10)
  }
}