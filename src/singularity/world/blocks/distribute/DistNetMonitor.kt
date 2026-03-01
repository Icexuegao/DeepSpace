package singularity.world.blocks.distribute

import arc.func.Prov
import arc.scene.ui.layout.Table

class DistNetMonitor(name: String) : DistNetBlock(name) {
  init {
    this.isNetLinker = false
    this.configurable = true
    buildType= Prov(::DistNetMonitorBuild)
  }

  inner class DistNetMonitorBuild : DistNetBuild() {
    override fun buildConfiguration(table: Table?) {
    }
  }
}