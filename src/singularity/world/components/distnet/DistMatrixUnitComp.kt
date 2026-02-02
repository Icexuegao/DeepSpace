package singularity.world.components.distnet

import singularity.world.blocks.distribute.matrixGrid.MatrixGridBlock

interface DistMatrixUnitComp {
    fun <T> slef(): MatrixGridBlock {
        return this as MatrixGridBlock
    }

    //@Annotations.BindField("bufferCapacity")
  var bufferCapacity: Int

}