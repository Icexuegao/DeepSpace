package ice.library.baseContent.liquid

import arc.graphics.Color
import ice.library.components.NutrientConcentration
import ice.library.meta.stat.IceStats
import mindustry.type.Liquid

open class IceLiquid(name: String, color: String) : Liquid(name, Color.valueOf(color)), NutrientConcentration {
    var nutrientConcentration: Float = 0f
    override fun setStats() {
        if (nutrientConcentration != 0f) stats.addPercent(IceStats.营养浓度, nutrientConcentration)
        super.setStats()
    }

    override fun getNutrient(): Float = nutrientConcentration

}
