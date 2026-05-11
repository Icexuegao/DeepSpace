package ice.content.remains

import ice.content.IUnitTypes
import ice.type.Remains
import mindustry.world.meta.Stats

class 不焚者的余烬:Remains("remains_unburnt_ashes"){
  init {
    localization {
      zh_CN {
        this.localizedName = "不焚者的余烬"
        description = "温热的结晶体,烈焰中被焚尽却未曾死去之人的最后残留"
      }
    }
    val f = 5
    effect = "单位[${IUnitTypes.仆从.localizedName}]的武器伤害提升[$f]"
    install = {
      IUnitTypes.仆从.weapons.forEach {
        it.bullet.damage += if (it.mirror) f / 2 else f
      }
      IUnitTypes.仆从.stats = Stats()
      IUnitTypes.仆从.checkStats()
    }
    uninstall = {
      IUnitTypes.仆从.weapons.forEach {
        it.bullet.damage -= if (it.mirror) f / 2 else f
      }
      IUnitTypes.仆从.stats = Stats()
      IUnitTypes.仆从.checkStats()
    }
  }
}