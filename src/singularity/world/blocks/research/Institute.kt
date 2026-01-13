package singularity.world.blocks.research

import arc.func.Prov
import arc.struct.IntSet
import arc.struct.Seq
import arc.util.io.Reads
import arc.util.io.Writes
import mindustry.game.Team
import mindustry.world.Tile
import singularity.Sgl
import singularity.game.planet.context.ResearchContext
import singularity.world.blocks.SglBlock
import singularity.world.blocks.research.InstituteRoom.InstituteRoomBuild
import universecore.components.blockcomp.ChainsBlockComp
import universecore.components.blockcomp.ChainsBuildComp
import universecore.world.blocks.modules.ChainsModule

class Institute(name: String) : SglBlock(name), ChainsBlockComp {
    var baseTechPoints: Int = 4
    override val maxChainsWidth=0
    override var maxChainsHeight=0
    init {
        configurable = true
        update = true
        buildType= Prov(::InstituteBuild)
    }

    override fun setStats() {
        super.setStats()
        setChainsStats(stats)
    }



    override fun chainable(other: ChainsBlockComp): Boolean {
        return other is Institute || other is InstituteRoom
    }

    override fun canPlaceOn(tile: Tile?, team: Team?, rotation: Int): Boolean {
        return super.canPlaceOn(tile, team, rotation)
                && !Sgl.logic.currentPlanet.currentContext(team, ResearchContext::class.java).processing
    }

    inner class InstituteBuild : SglBuilding(), ChainsBuildComp {
        var context: ResearchContext? = null
        var rooms: Seq<InstituteRoomBuild> = Seq<InstituteRoomBuild>()
        var deviceUpdated: Boolean = false
        override var loadingInvalidPos=IntSet()
        override var chains=ChainsModule(this)
        override fun created() {
            super.created()

            context = Sgl.logic.currentPlanet.currentContext(team(), ResearchContext::class.java)
            context!!.processing = true
        }

        override fun onProximityAdded() {
            super.onProximityAdded()
            onChainsAdded()
        }

        override fun onProximityRemoved() {
            super.onProximityRemoved()
            onChainsRemoved()
        }
        public override fun onDestroyed() {
            context!!.processing = false
        }

        fun deviceUpdate() {
            deviceUpdated = true
        }

        override fun write(write: Writes) {
            super.write(write)
            writeChains(write)
        }

        override fun read(read: Reads, revision: Byte) {
            super.read(read, revision)
            readChains(read)
        }

        override fun onChainsUpdated() {
            deviceUpdate()
        }

        override fun updateTile() {
            super.updateTile()

            if (deviceUpdated) {
                deviceUpdated = false

                rooms.clear()
                for (comp in this) {
                    if (comp is InstituteRoomBuild) rooms.add(comp)
                }

                context!!.devices.clear()

                for (room in rooms) {
                    if (room.device != null) {
                        context!!.devices.add(room.device)
                    }
                }
                context!!.updateTechs(baseTechPoints)
            }
        }
    }
}