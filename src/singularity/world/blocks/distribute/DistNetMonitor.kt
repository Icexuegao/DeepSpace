package singularity.world.blocks.distribute

import arc.scene.ui.layout.Table

class DistNetMonitor(name: String) : DistNetBlock(name) {
    init {
        isNetLinker = false
        configurable = true
    }

    inner class DistNetMonitorBuild : DistNetBuild() {
        override fun buildConfiguration(table: Table?) {
        }
    }
}