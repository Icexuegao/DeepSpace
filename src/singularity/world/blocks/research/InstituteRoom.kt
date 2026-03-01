package singularity.world.blocks.research

import arc.func.Prov
import arc.struct.IntSet
import arc.util.io.Reads
import arc.util.io.Writes
import singularity.world.blocks.SglBlock
import universecore.components.blockcomp.ChainsBlockComp
import universecore.components.blockcomp.ChainsBuildComp
import universecore.world.blocks.modules.ChainsModule

class InstituteRoom(name: String) : SglBlock(name), ChainsBlockComp {
    init {
        destructible = true
        update = false
        buildType = Prov(::InstituteRoomBuild)
    }

    override fun chainable(other: ChainsBlockComp): Boolean {
        return other is Institute || other is InstituteRoom
    }

    override val maxChainsWidth=1
    override var maxChainsHeight=1
    override fun setStats() {
        super.setStats()
        setChainsStats(stats)
    }

    inner class InstituteRoomBuild : SglBuilding(), ChainsBuildComp {
        var device: ResearchDevice? = null
        override var loadingInvalidPos= IntSet()
        override var chains= ChainsModule(this)
        override fun onProximityAdded() {
            super.onProximityAdded()
            onChainsAdded()
        }
        override fun onProximityRemoved() {
            super.onProximityRemoved()
            onChainsRemoved()
        }

        override fun write(write: Writes) {
            super.write(write)
            writeChains(write)
        }

        override fun read(read: Reads, revision: Byte) {
            super.read(read, revision)
            readChains(read)
        }


    }
}