package ice.library.type.baseContent.liquid

import arc.graphics.Color
import ice.library.type.components.NutrientConcentration
import ice.library.type.meta.stat.IceStats
import ice.library.type.meta.stat.Stats
import mindustry.type.Liquid

open class IceLiquid(name: String, color: String) : Liquid(name, Color.valueOf(color)), NutrientConcentration {

    override var nutrientConcentration: Float = 0f

    override fun init() {
        stats = Stats()
        super.init()
    }

    override fun setStats() {
        val stats1 = stats as Stats
        if (nutrientConcentration != 0f) stats1.addPercent(IceStats.营养浓度, nutrientConcentration)
        super.setStats()
    }

}
