package singularity.world.blocks.distribute

import arc.func.Prov
import singularity.world.blocks.distribute.DistEnergyManager.DistEnergyManagerBuild
import singularity.world.components.distnet.DistElementBuildComp

open class DistEnergyEntry(name: String) : DistNetBlock(name) {
  init {
    this.topologyUse = 0
    buildType= Prov(::DistEnergyEntryBuild)
  }

  open inner class DistEnergyEntryBuild : DistNetBuild() {
    var ownerManager: DistEnergyManagerBuild? = null

    override fun linkable(other: DistElementBuildComp): Boolean {
      return false
    }

    public override fun onProximityAdded() {
      this.ownerManager = null

      for (building in this.proximity) {
        if (building is DistEnergyManagerBuild) {
          val manager = building
          manager.distributor.network.add(this)
          this.ownerManager = manager
        }
      }
    }
  }
}