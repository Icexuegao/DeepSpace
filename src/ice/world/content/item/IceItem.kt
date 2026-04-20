package ice.world.content.item

import arc.graphics.Color
import ice.content.IPlanets
import ice.ui.bundle.Localizable
import ice.world.meta.IceStats
import mindustry.ctype.UnlockableContent
import mindustry.type.Item
import mindustry.world.meta.Stat

@Suppress("PROPERTY_HIDES_JAVA_FIELD")
open class IceItem(name: String, color: String, applys: IceItem.(IceItem) -> Unit = {}) :Item(name, Color.valueOf(color)), Localizable {

  override var localizedName: String by UnlockableContent::localizedName
  override var description: String by UnlockableContent::description
  override var details: String by UnlockableContent::details

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
