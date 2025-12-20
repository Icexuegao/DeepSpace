package ice.world.content.unit.ability

import arc.math.Angles
import arc.scene.ui.layout.Table
import ice.world.meta.IceEffects
import ice.world.meta.IceStats
import mindustry.entities.abilities.Ability
import mindustry.gen.Groups
import mindustry.gen.Unit

class InterceptAbilty(var damage: Float, var range: Float) : Ability() {
    data class UnitData(var unit: Unit, var range: Float)

    var unitData: UnitData? = null
    override fun addStats(t: Table) {
        super.addStats(t)
        t.row()
        t.add("[accent]${damage.toInt()}[][lightgray] " + IceStats.拦截伤害.localized()).left().padLeft(5f)
        t.row()
        t.add("[accent]${range.toInt()}[][lightgray] " + IceStats.拦截范围.localized()).left().padLeft(5f)
        t.row()
    }

    override fun created(unit: Unit) {
        super.created(unit)
        unitData = UnitData(unit, range)
    }

    override fun localized(): String {
        return IceStats.拦截护盾.localized()
    }

    override fun update(unit: Unit) {
        super.update(unit)
        unitData?.let {ud->
            ud.unit = unit
            val intersect = Groups.bullet.intersect(unit.x - range, unit.y - range, range * 2f, range * 2f)
            intersect.forEach {
                if (it.team == unit.team() || it.damage > damage) return
                val angle = Angles.angle(unit.x, unit.y, it.x, it.y)
                IceEffects.shieldWave.at(it.x, it.y, angle, ud)
                it.type.hit(it, it.x, it.y)
                it.remove()
            }
        }

    }
}