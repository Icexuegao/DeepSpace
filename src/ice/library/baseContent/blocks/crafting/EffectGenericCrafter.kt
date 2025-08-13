package ice.library.baseContent.blocks.crafting;

import arc.func.Prov
import ice.library.meta.stat.IceStats
import ice.library.scene.tex.Colors
import mindustry.content.StatusEffects
import mindustry.entities.Units
import mindustry.gen.Unit
import mindustry.graphics.Drawf
import mindustry.type.StatusEffect
import mindustry.world.blocks.production.GenericCrafter

open class EffectGenericCrafter(name: String) : GenericCrafter(name) {
    var effectTime = 60f
    var statusEffect: StatusEffect = StatusEffects.wet
    var statusTime = 15f
    var radius = 40f

    init {
        buildType = Prov(::EffectGenericCrafterBuild)
    }

    override fun setStats() {
        stats.add(IceStats.状态重载时间, (effectTime / 60).toString() + " seconds")
        stats.add(IceStats.状态效果, statusEffect.localizedName)
        stats.add(IceStats.状态持续时间, "$statusTime seconds")
        stats.add(IceStats.范围, "[" + radius / 8 + "] T")
        super.setStats()
    }

    inner class EffectGenericCrafterBuild : GenericCrafterBuild() {
        override fun drawSelect() {
            super.drawSelect()
            Drawf.circles(x, y, radius,Colors.s1)
        }
        override fun craft() {
            super.craft()
            Units.nearby(
                team, x, y, radius
            ) { e: Unit -> e.apply(statusEffect, statusTime * 60) }
            heal(maxHealth * 0.05f)
        }
    }
}
