package ice.library.content.blocks.abstractBlocks

import arc.func.Prov
import arc.util.Eachable
import mindustry.entities.units.BuildPlan
import mindustry.graphics.Drawf
import mindustry.logic.Ranged

open class RangeBlock(name: String) : IceBlock(name) {
    var range = 0f

    init {
        update=true
        buildType = Prov(::RangeBlockBuild)
    }

    override fun drawPlan(plan: BuildPlan, list: Eachable<BuildPlan>, valid: Boolean, alpha: Float) {
        super.drawPlan(plan, list, valid, alpha)
        if (range != 0f) Drawf.circles(plan.drawx(), plan.drawy(), range, blockColor)
    }

    open inner class RangeBlockBuild : IceBuild(), Ranged {
        override fun drawSelect() {
            super.drawSelect()
            Drawf.circles(x, y, range(), blockColor)
        }

        override fun drawConfigure() {
            super.drawConfigure()
            Drawf.circles(x, y, range(), blockColor)
        }

        override fun range(): Float {
            return range
        }
    }
}