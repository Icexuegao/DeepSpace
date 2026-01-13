package singularity.world.blocks.distribute.netcomponents

import arc.Core
import arc.func.Prov
import arc.graphics.g2d.TextureRegion
import arc.struct.IntSet
import arc.struct.Seq
import arc.util.io.Reads
import arc.util.io.Writes
import mindustry.game.Team
import mindustry.gen.Building
import mindustry.world.Tile
import singularity.world.blocks.distribute.DistNetBlock
import singularity.world.components.distnet.DistElementBuildComp
import singularity.world.distribution.DistributeNetwork
import universecore.components.blockcomp.ChainsBlockComp
import universecore.components.blockcomp.ChainsBuildComp
import universecore.components.blockcomp.SpliceBlockComp
import universecore.components.blockcomp.SpliceBuildComp
import universecore.world.DirEdges
import universecore.world.blocks.modules.ChainsModule
import java.util.*

open class ComponentInterface(name: String) : DistNetBlock(name), SpliceBlockComp {
    var interfaceLinker: TextureRegion? = null
    var linker: TextureRegion? = null
    override var maxChainsWidth: Int = 40
    override var maxChainsHeight: Int = 40
    override var interCorner = false
    override var negativeSplice = false

    init {
        isNetLinker = true
        buildType = Prov(::ComponentInterfaceBuild)
    }

    override fun load() {
        super.load()
        interfaceLinker = Core.atlas.find(name + "_linker")
        linker = Core.atlas.find(name + "_comp_linker")
    }

    override fun chainable(other: ChainsBlockComp): Boolean {
        return other === this
    }

    inner class ComponentInterfaceBuild : DistNetBuild(), SpliceBuildComp {
        override var loadingInvalidPos= IntSet()
        override  var chains= ChainsModule(this)
        var links: Seq<ComponentInterfaceBuild?> = Seq<ComponentInterfaceBuild?>()
        var connects: Seq<DistElementBuildComp?> = Seq<DistElementBuildComp?>()
        var interSplice: Byte = 0
        var connectSplice: ByteArray = ByteArray(4)
        var mark: Boolean = false
        override var splice = 0

        override fun init(tile: Tile?, team: Team?, shouldAdd: Boolean, rotation: Int): Building? {
            super.init(tile, team, shouldAdd, rotation)
            chains = ChainsModule(this)
            chains.newContainer()
            return this
        }

        override fun updateTile() {
            super.updateTile()

            if (mark) {
                updateNetLinked()
                DistributeNetwork().flow(this)
                mark = false
            }
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
        override fun updateNetLinked() {
            super.updateNetLinked()

            links.clear()
            connects.clear()

            Arrays.fill(connectSplice, 0.toByte())

            for (building in proximity) {
                if (building is ComponentInterfaceBuild && canChain(building)) {
                    links.add(building)
                } else if (building is DistNetBuild && linkable(building) && building.linkable(this) && connectable(building)) {
                    connects.add(building)
                    val dir = relativeTo(building).toInt()
                    val arr = DirEdges.get(size, dir)

                    for (i in arr.indices) {
                        val t = tile.nearby(arr[i])
                        if (t != null && t.build === building) connectSplice[dir] = (connectSplice[dir].toInt() or (1 shl i).toByte().toInt()).toByte()
                    }
                }
            }

            netLinked()!!.addAll(links).addAll(connects)
        }

        override fun onProximityAdded() {
            super.onProximityAdded()

            mark = true
            onChainsAdded()
        }

        override fun onProximityUpdate() {
            super.onProximityUpdate()

            updateNetLinked()
            mark = true
            updateRegionBit()
        }

        override fun networkRemoved(remove: DistElementBuildComp) {
            super.networkRemoved(remove)

            connects.remove(remove)
            if (remove is ComponentInterfaceBuild) links.remove(remove)

            mark = true
        }

        override fun updateRegionBit() {
            super.updateRegionBit()

            interSplice = 0
            for (i in 0..3) {
                if ((splice and (1 shl i * 2)) !== 0) interSplice = (interSplice.toInt() or (1 shl i).toByte().toInt()).toByte()
            }
        }

        fun connectable(other: DistNetBuild): Boolean {
            val dir = other.relativeTo(this).toInt()
            val t = other.tile
            for (point2 in DirEdges.get(other.block.size, dir)) {
                val ot = t.nearby(point2)
                if (ot == null || ot.build !is ComponentInterfaceBuild) return false
                var build = ot.build as ComponentInterfaceBuild
                if (build !== this && build.distributor!!.network !== distributor!!.network) return false
            }

            return true
        }

        override fun canChain(other: ChainsBuildComp): Boolean {
            return super.canChain(other) && (other.tileX() == tileX() || other.tileY() == tileY())
        }
    }
}