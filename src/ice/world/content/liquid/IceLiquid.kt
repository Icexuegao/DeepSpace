package ice.world.content.liquid

import arc.graphics.Color
import ice.content.IPlanets
import ice.world.meta.IceStats
import mindustry.gen.Puddle
import mindustry.type.Liquid

open class IceLiquid(name: String, color: Color , app: IceLiquid.() -> Unit = {}) : Liquid(name, color) {

  constructor(name: String,color: String, app: IceLiquid.() -> Unit = {}) : this(name,Color.valueOf(color),app)
  var updateFun: (Puddle) -> Unit = {}
  var nutrientConcentration = 0f

  init {
    app(this)
  }

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
