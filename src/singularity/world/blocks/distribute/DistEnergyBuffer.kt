package singularity.world.blocks.distribute

import arc.Core
import arc.func.Floatp
import arc.func.Func
import arc.func.Prov
import arc.util.Strings
import mindustry.core.UI
import mindustry.ui.Bar
import singularity.graphic.SglDrawConst
import singularity.world.components.distnet.DistElementBuildComp

open class DistEnergyBuffer(name: String) : DistEnergyEntry(name) {
    public override fun setBars() {
        super.setBars()
        addBar<DistEnergyBufferBuild?>("energyBuffered", Func { e: DistEnergyBufferBuild? ->
            Bar(
                Prov {
                    Core.bundle.format(
                        "bar.energyBuffered",
                        if (e!!.matrixEnergyBuffered >= 1000) UI.formatAmount(e.matrixEnergyBuffered.toLong()) else Strings.autoFixed(e.matrixEnergyBuffered, 1),
                        if (matrixEnergyCapacity >= 1000) UI.formatAmount(matrixEnergyCapacity.toLong()) else Strings.autoFixed(matrixEnergyCapacity, 1)
                    )
                },
                Prov { SglDrawConst.matrixNet },
                Floatp { e!!.matrixEnergyBuffered / matrixEnergyCapacity }
            )
        })
    }

    inner class DistEnergyBufferBuild : DistEnergyEntryBuild() {
        public override fun linkable(other: DistElementBuildComp?): Boolean {
            return false
        }
    }
}