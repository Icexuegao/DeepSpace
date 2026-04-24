package ice.world.content.liquid

import arc.graphics.Color
import ice.content.IPlanets
import ice.ui.bundle.Localizable
import ice.world.meta.IceStats
import mindustry.ctype.UnlockableContent
import mindustry.gen.Puddle
import mindustry.type.Liquid

@Suppress("PROPERTY_HIDES_JAVA_FIELD")
open class IceLiquid(name: String, color: Color, configured: IceLiquid.() -> Unit = { }) :Liquid(name, color), Localizable {

  constructor(name: String, color: String, app: IceLiquid.() -> Unit = {}) :this(name, Color.valueOf(color), app)

  override var localizedName: String by UnlockableContent::localizedName
  override var description: String by UnlockableContent::description
  override var details: String by UnlockableContent::details

  val c = configured(this)
  var updateFun: (Puddle) -> Unit = {}
  var nutrientConcentration = 0f

  override fun postInit() {
    shownPlanets.add(IPlanets.阿德里)
    super.postInit()
  }

  fun setUpdate(updateFun: (Puddle) -> Unit) {
    this.updateFun = updateFun
  }

  override fun setStats() {
    if (nutrientConcentration > 0f) stats.addPercent(IceStats.营养浓度, nutrientConcentration)
    super.setStats()
  }

  override fun update(puddle: Puddle) {
    super.update(puddle)
    updateFun(puddle)
  }
}
