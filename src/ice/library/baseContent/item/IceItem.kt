package ice.library.baseContent.item

import arc.graphics.Color
import ice.content.IPlanets
import ice.library.components.NutrientConcentration
import ice.library.meta.stat.IceStats
import mindustry.type.Item

open class IceItem(name: String, color: String) : Item(name, Color.valueOf(color)), NutrientConcentration {
    var nutrientConcentration = 0f
    override fun postInit() {
        shownPlanets.add(IPlanets.阿德里)
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

    override fun getNutrient(): Float = nutrientConcentration
}
