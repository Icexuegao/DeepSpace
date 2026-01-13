package singularity.ui.tables

import singularity.world.distribution.DistributeNetwork
import singularity.world.distribution.MatrixGrid
import java.util.function.Consumer

class DistContainerMonitor(maxCount: Int) : Monitor() {
    public override fun startMonit(distNetwork: DistributeNetwork) {
        distNetwork.grids.forEach(Consumer { obj: MatrixGrid? -> obj!!.startStatContainer() })
    }

    public override fun endMonit(distNetwork: DistributeNetwork) {
        distNetwork.grids.forEach(Consumer { obj: MatrixGrid? -> obj!!.endStatContainer() })
    }
}