package ice.library.content.unit.ability

import arc.func.Prov
import arc.math.Angles
import arc.scene.ui.layout.Table
import ice.library.meta.IceEffects
import ice.library.meta.stat.IceStats
import mindustry.entities.abilities.Ability
import mindustry.gen.Groups
import mindustry.gen.Unit

class InterceptAbilty(var damage: Float, var range: Float) : Ability() {
    inner class DF {
        lateinit var unit: Unit
        var range: Prov<Float> = Prov { this@InterceptAbilty.range }
    }

    var df = DF()
    override fun addStats(t: Table) {
        super.addStats(t)
        t.row()
        t.add("[accent]${damage.toInt()}[][lightgray] " + IceStats.拦截伤害.localized()).left().padLeft(5f)
        t.row()
        t.add("[accent]${range.toInt()}[][lightgray] " + IceStats.拦截范围.localized()).left().padLeft(5f)
        t.row()
    }

    override fun localized(): String? {
        return IceStats.拦截护盾.localized()
    }

    override fun update(unit: Unit) {
        super.update(unit)
        df.unit = unit
        val intersect = Groups.bullet.intersect(unit.x - range, unit.y - range, range * 2f, range * 2f)
        intersect.forEach {
            if (it.team == unit.team() || it.damage > damage) return
            val angle = Angles.angle(unit.x, unit.y, it.x, it.y)
            IceEffects.shieldWave.at(it.x, it.y, angle, df)
            it.type.hit(it, it.x, it.y)
            it.remove()
        }
    }
}