package singularity.world.blocks.distribute

import arc.Core
import arc.func.Floatp
import arc.func.Func
import arc.func.Prov
import arc.util.Strings
import mindustry.core.UI
import mindustry.ui.Bar
import singularity.graphic.SglDrawConst
import singularity.world.blocks.distribute.DistEnergyEntry.DistEnergyEntryBuild

open class DistEnergyManager(name: String) : DistNetBlock(name) {
    init {
        matrixEnergyCapacity = 2048f
        isNetLinker = true
    }

    public override fun setBars() {
        super.setBars()
        addBar<DistEnergyManagerBuild?>("energyBuffered", Func { e: DistEnergyManagerBuild? ->
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

    inner class DistEnergyManagerBuild : DistNetBuild() {
        override fun updateNetLinked() {
            super.updateNetLinked()
            for (building in proximity) {
                if (building is DistEnergyEntryBuild) {
                    netLinked.add(building)
                }
            }
        }

        override fun onProximityUpdate() {
            super.onProximityUpdate()

            updateNetLinked()
        }
    }
}