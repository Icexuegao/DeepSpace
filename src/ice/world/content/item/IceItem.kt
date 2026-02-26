package ice.world.content.item

import arc.graphics.Color
import ice.content.IPlanets
import ice.world.meta.IceStats
import mindustry.type.Item
import mindustry.world.meta.Stat

open class IceItem(name: String, color: String, applys: IceItem.(IceItem) -> Unit = {}) :
  Item(name, Color.valueOf(color)) {
  var nutrientConcentration = 0f

  init {
    applys(this)
  }

  override fun postInit() {
    shownPlanets.add(IPlanets.阿德里)
    super.postInit()
  }

  override fun setStats() {
    stats.addPercent(Stat.explosiveness, explosiveness)
    stats.addPercent(Stat.flammability, flammability)
    stats.addPercent(Stat.radioactivity, radioactivity)
    stats.addPercent(Stat.charge, charge)
    stats.addPercent(IceStats.营养浓度, nutrientConcentration)
    stats.add(IceStats.建造时间花费, cost)
    stats.add(IceStats.建筑血量系数, healthScaling)
    stats.add(IceStats.硬度, "$hardness")
    stats.add(IceStats.是否用于建造, buildable)
  }
}
