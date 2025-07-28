package ice.library.type.baseContent.item

import arc.graphics.Color
import ice.content.IcePlanets
import ice.library.type.components.NutrientConcentration
import ice.library.type.meta.stat.IceStats
import mindustry.type.Item

open class IceItem(name: String, color: String) : Item(name, Color.valueOf(color)), NutrientConcentration {
    override var nutrientConcentration: Float = 0f
    override fun postInit() {
        shownPlanets.add(IcePlanets.阿德里)
        super.postInit()
    }
    override fun setStats() {
        super.setStats()
        if (nutrientConcentration != 0f) stats.addPercent(IceStats.营养浓度, nutrientConcentration)
        stats.add(IceStats.建造时间花费, cost)
        stats.add(IceStats.建筑血量系数, healthScaling)
        stats.add(IceStats.硬度, "$hardness")
        stats.add(IceStats.是否用于建造, buildable)
    }
}
