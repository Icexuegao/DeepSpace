package ice.library.content.status

import arc.func.Cons2
import ice.library.meta.stat.IceStats
import mindustry.entities.units.StatusEntry
import mindustry.gen.Unit
import mindustry.type.StatusEffect

open class IceStatusEffect(name: String, apply: IceStatusEffect.() -> kotlin.Unit) : StatusEffect(name) {
    var armorBreak = 0f
    var armorBreakPercent = 0f
    var armorRecovery: Boolean = false
    private var statsFun = {}
    private var update = Cons2<Unit, StatusEntry> { _, _ -> }

    init {
        apply(this)
    }

    fun setStatsFun(statsFun: () -> kotlin.Unit) {
        this.statsFun = statsFun
    }

    override fun setStats() {
        super.setStats()
        if (armorBreakPercent > 0f) stats.addPercent(IceStats.破甲, armorBreakPercent)
        if (armorBreak > 0) {
            stats.add(IceStats.破甲, armorBreak)
        }
        statsFun.invoke()
    }

    override fun applied(unit: Unit, time: Float, extend: Boolean) {
        if (armorBreakPercent != 0f) unit.armor *= (1 - armorBreakPercent)
        if (armorBreak > 0) {
            if (unit.type.armor >= armorBreak) {
                unit.armor -= armorBreak
            } else {
                unit.armor -= unit.type.armor
            }
        }

        super.applied(unit, time, extend)
    }

    override fun onRemoved(unit: Unit) {
        if (armorBreak > 0 && armorRecovery) {
            if (unit.type.armor >= armorBreak) {
                unit.armor += armorBreak
            } else {
                unit.armor += unit.type.armor
            }
        }
    }

    fun setUpdate(update: Cons2<Unit, StatusEntry>) {
        this.update = update
    }

    override fun update(unit: Unit, entry: StatusEntry) {
        super.update(unit, entry)
        update.get(unit, entry)
    }

    override fun isHidden() = hideDatabase
}
