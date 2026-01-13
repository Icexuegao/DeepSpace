package singularity.world.blocks.distribute

import arc.Core
import arc.func.Cons
import arc.graphics.g2d.Draw
import arc.graphics.g2d.Lines
import arc.math.geom.Geometry
import arc.scene.ui.layout.Table
import arc.struct.ObjectMap
import arc.struct.Seq
import arc.util.Strings
import mindustry.Vars
import mindustry.game.Team
import mindustry.gen.Building
import mindustry.graphics.Pal
import mindustry.world.Block
import mindustry.world.Tile
import mindustry.world.meta.BlockStatus
import mindustry.world.meta.StatUnit
import mindustry.world.meta.StatValue
import singularity.world.blocks.distribute.netcomponents.CoreNeighbourComponent.CoreNeighbourComponentBuild
import singularity.world.blocks.distribute.netcomponents.NetPluginComp
import singularity.world.components.distnet.DistMatrixUnitComp
import singularity.world.components.distnet.DistNetworkCoreComp
import singularity.world.components.distnet.IOPointComp
import singularity.world.distribution.DistBufferType
import singularity.world.meta.SglStat
import singularity.world.meta.SglStatUnit
import singularity.world.modules.DistCoreModule
import universecore.util.NumberStrify
import kotlin.math.max

//import universecore.annotations.Annotations;
open class DistNetCore(name: String) : NetPluginComp(name), DistMatrixUnitComp {
    var requestEnergyCost: Float = 0.1f

    init {
        topologyUse = 0
        isNetLinker = true

        computingPower = 8
        topologyCapacity = 8
        bufferSize = ObjectMap.of<DistBufferType<*>?, Int?>(
            DistBufferType.itemBuffer, 256,
            DistBufferType.liquidBuffer, 256
        )
    }

    override fun setStats() {
        super.setStats()
        stats.add(SglStat.computingPower, (computingPower * 60).toFloat(), StatUnit.perSecond)
        stats.add(SglStat.topologyCapacity, topologyCapacity.toFloat())
        stats.remove(SglStat.matrixEnergyUse)
        stats.add(
            SglStat.matrixEnergyUse,
            (Strings.autoFixed(matrixEnergyUse * 60, 2) + SglStatUnit.matrixEnergy.localized() + Core.bundle.get("misc.perSecond") + " + "
                    + Strings.autoFixed(requestEnergyCost * 60, 2) + SglStatUnit.matrixEnergy.localized() + Core.bundle.get("misc.perRequest") + Core.bundle.get("misc.perSecond"))
        )
        stats.add(SglStat.bufferSize, StatValue { t: Table? ->
            t!!.defaults().left().fillX().padLeft(10f)
            t.row()
            for (entry in bufferSize) {
                if (entry.value!! <= 0) continue
                t.add(Core.bundle.get("content." + entry.key!!.targetType().name + ".name") + ": " + NumberStrify.toByteFix(entry.value!!.toDouble(), 2))
                t.row()
            }
        })
    }

    // @Annotations.ImplEntries
    inner class DistNetCoreBuild : NetPluginCompBuild(), DistNetworkCoreComp {
        var distCore: DistCoreModule? = null
        var proximityComps: Seq<CoreNeighbourComponentBuild?> = Seq<CoreNeighbourComponentBuild?>()

        override fun onProximityUpdate() {
            super.onProximityUpdate()

            netLinked.removeAll(proximityComps)

            proximityComps.clear()
            for (building in proximity) {
                if (building is CoreNeighbourComponentBuild) proximityComps.add(building)
            }

            netLinked.addAll(proximityComps)
        }

        override fun updateNetLinked() {
            super<DistNetworkCoreComp>.updateNetLinked()
            netLinked.addAll(proximityComps)
        }

        override fun priority(priority: Int) {
            matrixGrid()!!.priority = priority
            distributor!!.network.priorityModified(this)
        }

        override fun networkValided() {
            matrixGrid()!!.clear()
        }

        override fun status(): BlockStatus? {
            return if (distCore!!.requestTasks.isEmpty()) BlockStatus.noInput else super.status()
        }

        override fun create(block: Block?, team: Team?): Building? {
            distCore = DistCoreModule(this)
            super.create(block, team)
            initBuffers()
            items = getBuffer(DistBufferType.itemBuffer)!!.generateBindModule()
            liquids = getBuffer(DistBufferType.liquidBuffer)!!.generateBindModule()

            priority(-65536)
            return this
        }

        override fun drawSelect() {
            super.drawSelect()
            Lines.stroke(1f, Pal.accent)
            val outline = Cons { b: Building? ->
                for (i in 0..3) {
                    val p = Geometry.d8edge[i]
                    val offset = -max(b!!.block.size - 1, 0) / 2f * Vars.tilesize
                    Draw.rect("block-select", b.x + offset * p.x, b.y + offset * p.y, (i * 90).toFloat())
                }
            }
            outline.get(this)
            proximityComps.each(outline)
        }

        override fun matrixEnergyConsume(): Float {
            return matrixEnergyUse + requestEnergyCost * distCore!!.lastProcessed
        }

        override fun ioPointConfigBackEntry(ioPoint: IOPointComp) {
            //no action
        }

        override fun tileValid(tile: Tile?): Boolean {
            return true
        }

        override fun drawValidRange() {
            //no action
        }
    }
}