package ice.world.content.status

import arc.func.Cons2
import arc.struct.ObjectMap
import ice.library.util.accessField
import ice.ui.bundle.Localizable
import ice.world.meta.IceStats
import mindustry.entities.units.StatusEntry
import mindustry.gen.Unit
import mindustry.type.StatusEffect
import mindustry.ui.Fonts
import kotlin.Unit as kUnit

@Suppress("PROPERTY_HIDES_JAVA_FIELD")
open class IceStatusEffect(name: String, apply: IceStatusEffect.() -> kUnit = {}) : StatusEffect(name), Localizable {
  var armorBreak = 0f
  var armorBreakPercent = 0f
  var armorRecovery: Boolean = false
  private var update = Cons2<Unit, StatusEntry> { _, _ -> }
  private var drawFun: (Unit) -> kUnit = {}


  init {
    apply(this)
  }
companion object{
  var iconId : ObjectMap<String, String> by Fonts::class.accessField("stringIcons")
}
  override fun init() {
    super.init()
    Fonts.registerIcon(name, "status-"+name, iconId.size, fullIcon)
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
  override var localizedName: String
    get() = super.localizedName
    set(value) {
      super.localizedName = value
    }

  override var description: String
    get() = super.description
    set(value) {
      super.description = value
    }
  override var details: String
    get() = super.details
    set(value) {
      super.details = value
    }
}
