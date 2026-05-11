package ice.content.remains

import ice.content.IUnitTypes
import ice.type.Remains
import mindustry.world.meta.Stats

class 坚固的装甲板:Remains("remains_armor_plates"){
  init  {
    localization {
      zh_CN {
        this.localizedName = "坚固的装甲板"
        description = "多层淬火钢板铆接而成,表面布满划痕与凹坑"
      }
    }
    val hea = 500
    effect = "单位[${IUnitTypes.断业.localizedName}]的生命值提升[$hea]"
    install = {
      IUnitTypes.断业.health += hea
      IUnitTypes.断业.stats = Stats()
      IUnitTypes.断业.checkStats()
    }
    uninstall = {
      IUnitTypes.断业.health -= hea
      IUnitTypes.断业.stats = Stats()
      IUnitTypes.断业.checkStats()
    }
  }
}