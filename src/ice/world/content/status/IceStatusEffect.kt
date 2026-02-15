package ice.world.content.status

import arc.func.Cons2
import ice.library.util.accessMethod2
import ice.world.meta.IceStats
import mindustry.entities.units.StatusEntry
import mindustry.gen.Unit
import mindustry.type.StatusEffect
import kotlin.Unit as kUnit

open class IceStatusEffect(name: String, apply: IceStatusEffect.() -> kUnit = {}) : StatusEffect(name) {
  var armorBreak = 0f
  var armorBreakPercent = 0f
  var armorRecovery: Boolean = false
  private var update = Cons2<Unit, StatusEntry> { _, _ -> }
  private var drawFun: (Unit) -> kUnit = {}
  val affinitys: StatusEffect.(effect: StatusEffect, handler: TransitionHandler) -> kUnit = accessMethod2("affinity")

  fun opposites(vararg effect: StatusEffect) {
    opposite(*effect)
  }

  init {
    apply(this)
  }

  override fun setStats() {
    super.setStats()
    if (armorBreakPercent > 0f) stats.addPercent(IceStats.破甲, armorBreakPercent)
    if (armorBreak > 0) {
      stats.add(IceStats.破甲, armorBreak)
    }
  }

  override fun applied(unit: Unit, time: Float, extend: Boolean) {
    if (armorBreakPercent != 0f) unit.armor *= (1 - armorBreakPercent)
    if (armorBreak > 0) {
      unit.armor -= (if (unit.type.armor >= armorBreak) armorBreak else unit.type.armor)
    }
    super.applied(unit, time, extend)
  }

  override fun onRemoved(unit: Unit) {
    if (armorBreak > 0 && armorRecovery) {
      unit.armor += (if (unit.type.armor >= armorBreak) armorBreak else unit.type.armor)
    }
  }

  fun transs(effect: StatusEffect, handler: TransitionHandler) {
    trans(effect, handler)
  }

  fun setUpdate(update: Cons2<Unit, StatusEntry>) {
    this.update = update
  }

  override fun draw(unit: Unit) {
    super.draw(unit)
    drawFun(unit)
  }

  fun setDraw(draw: (Unit) -> kUnit) {
    drawFun = draw
  }

  override fun update(unit: Unit, entry: StatusEntry) {
    super.update(unit, entry)
    update.get(unit, entry)
  }

  override fun isHidden() = hideDatabase
}
