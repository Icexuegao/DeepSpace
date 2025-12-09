package ice.world.content.unit.ability

import ice.world.content.unit.IceUnitType
import ice.world.meta.IceStats
import mindustry.gen.Bullet
import mindustry.gen.Unit

interface UnitTypeFun {
    fun IceUnitType.frontalInjuryFree(size: Float) {
        setUnitDamageEvent { u: Unit, b: Bullet ->
            if (b.type.pierceArmor) return@setUnitDamageEvent
            val bulletDir = b.rotation()
            val unitDir = u.rotation()
            // 计算相对角度（0-360）
            val relativeAngle = (bulletDir - unitDir + 360) % 360
            if (relativeAngle !in 90.0..270.0) {
                // 身后范围
            } else {
                b.damage -= b.damage * size
            }
        }
        statsFun {
            stats.addPercent(IceStats.正面免伤, size)
        }
    }
}