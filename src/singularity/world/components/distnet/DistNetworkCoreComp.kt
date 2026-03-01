package singularity.world.components.distnet

import singularity.world.modules.DistCoreModule

interface DistNetworkCoreComp : DistMatrixUnitBuildComp {
  // @Annotations.BindField("distCore")
  var distCore: DistCoreModule

  fun updateState(): Boolean {
    return false
  }

  // @Annotations.MethodEntry(entryMethod = "updateTile")
  fun updateDistNetwork() {
    distCore.update()
  }
}