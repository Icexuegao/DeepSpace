package ice.world.content.status

import arc.math.Mathf
import ice.content.IStatus
import ice.world.meta.IceStats
import mindustry.content.Fx
import mindustry.content.StatusEffects
import mindustry.entities.Effect
import mindustry.entities.units.StatusEntry
import mindustry.gen.Unit
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

class PercentStatus(name: String,
                    var percent: Float,
                    var min: Float,
                    var transitionDamages: Float = 0f,
                    val obj: Boolean = false,
                    appply: IceStatusEffect.() -> kotlin.Unit = {}
) : IceStatusEffect(name, appply) {
    companion object {
        var opposite = arrayOf(StatusEffects.wet, StatusEffects.freezing, IStatus.封冻)
        var affinitys = arrayOf(StatusEffects.tarred, Fx.burning)
    }

    init {
        init {
            if (obj) {
                opposite.forEach {
                    opposite(it)
                }
                val effect1 = affinitys[0] as mindustry.type.StatusEffect
                val effect2 = affinitys[1] as Effect
                affinity(effect1) { unit, result, time ->
                    unit.damagePierce(this.transitionDamages)
                    effect2.at(unit.x + Mathf.range(unit.bounds() / 2), unit.y + Mathf.range(unit.bounds() / 2))
                    result.set(this, min(time + result.time, 300f))
                }
            }
        }
    }

    override fun update(unit: Unit, entry: StatusEntry) {
        super.update(unit, entry)
        val umax = unit.type.health / 100 / 60
        val up = umax * percent
        val max = max(up, abs(min))

        if (min < 0) unit.heal(abs(max))
        else unit.damageContinuousPierce(max)
    }

    override fun setStats() {
        super.setStats()
        if (min < 0) {
            stats.add(IceStats.百分比治疗, "$percent%/秒")
            stats.add(IceStats.最小治疗, "${60 * -min}/秒")
        } else {
            stats.add(IceStats.百分比伤害, "$percent%/秒")
            stats.add(IceStats.最小伤害, "${60 * min}/秒")
        }
    }

}