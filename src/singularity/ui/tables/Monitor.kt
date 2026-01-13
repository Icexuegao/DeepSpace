package singularity.ui.tables

import arc.scene.ui.layout.Table
import singularity.world.distribution.DistributeNetwork

abstract class Monitor : Table() {
    abstract fun startMonit(distNetwork: DistributeNetwork)

    abstract fun endMonit(distNetwork: DistributeNetwork)
}