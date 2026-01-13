package singularity.world.blocks.distribute

import singularity.world.blocks.distribute.DistEnergyManager.DistEnergyManagerBuild
import singularity.world.components.distnet.DistElementBuildComp

open class DistEnergyEntry(name: String) : DistNetBlock(name) {
    init {
        topologyUse = 0
    }

    open inner class DistEnergyEntryBuild : DistNetBuild() {
        var ownerManager: DistEnergyManagerBuild? = null

        override fun linkable(other: DistElementBuildComp?): Boolean {
            return false
        }

        override fun onProximityAdded() {
            ownerManager = null
            for (building in proximity) {
                if (building is DistEnergyManagerBuild) {
                    building.distributor!!.network.add(this)
                    ownerManager = building
                }
            }
        }
    }
}