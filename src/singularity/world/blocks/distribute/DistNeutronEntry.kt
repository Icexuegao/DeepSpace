package singularity.world.blocks.distribute

import arc.util.Time
import kotlin.math.min

open class DistNeutronEntry(name: String) : DistEnergyEntry(name) {
    var maxEnergyInput: Float = 20f

    init {
        hasEnergy = true
        consumeEnergy = true
        energyCapacity = 1024f
    }

    inner class DistNeutronEntryBuild : DistEnergyEntryBuild() {
        var energyProduct: Float = 0f

        public override fun updateTile() {
            super.updateTile()
            if (distributor!!.network.netStructValid()) {
                val energyInput = min(maxEnergyInput, energy!!.getEnergy())
                energyProduct = energyInput * 1.25f
                handleEnergy(-energyInput * Time.delta)
            }
        }

        override fun matrixEnergyProduct(): Float {
            return energyProduct
        }
    }
}