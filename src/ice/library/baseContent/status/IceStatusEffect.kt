package ice.library.baseContent.status

import ice.library.meta.stat.IceStats
import mindustry.gen.Unit
import mindustry.type.StatusEffect


open class IceStatusEffect(name: String) : StatusEffect(name) {
    /** 破甲百分比  */
    var armorBreakPercent: Float = 0f

    /** 破甲数量  */
    var armorBreak: Int = 0

    /** 破甲状态消失后是否恢复护甲  */
    var armorRecovery: Boolean = false

    override fun setStats() {
        if (armorBreakPercent != 0f) stats.addPercent(IceStats.破甲, armorBreakPercent)
        if (armorBreak != 0) stats.add(IceStats.破甲, armorBreak.toFloat())
        super.setStats()
    }

    override fun applied(unit: Unit, time: Float, extend: Boolean) {
        /**扣除百分比 */
        if (armorBreakPercent != 0f) {
            unit.armor *= armorBreakPercent
        }
        /**扣除护甲 */
        if (armorBreak != 0) {
            if (unit.type.armor >= armorBreak) {
                unit.armor -= armorBreak.toFloat()
            } else {
                unit.armor -= unit.type.armor
            }
        }

        super.applied(unit, time, extend)
    }

    override fun onRemoved(unit: Unit) {
        super.onRemoved(unit)
    }

    override fun update(unit: Unit, time: Float) {
        /**时间结束恢复护甲 */
        if (armorBreak != 0 && !armorRecovery && time <= 60) {
            armorRecovery = true
            if (unit.type.armor >= armorBreak) {
                unit.armor += armorBreak.toFloat()
            } else {
                unit.armor += unit.type.armor
            }
        }
        super.update(unit, time)
    }

    override fun isHidden(): Boolean {
        return false
    }
}
