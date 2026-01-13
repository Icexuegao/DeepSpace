package singularity.world.blocks.distribute.netcomponents

import arc.Core
import arc.scene.ui.layout.Table
import arc.struct.ObjectMap
import mindustry.world.meta.StatUnit
import mindustry.world.meta.StatValue
import singularity.world.blocks.distribute.DistNetBlock
import singularity.world.components.distnet.DistElementBuildComp
import singularity.world.components.distnet.DistNetworkCoreComp
import singularity.world.distribution.DistBufferType
import singularity.world.meta.SglStat
import universecore.util.NumberStrify

open class CoreNeighbourComponent(name: String) : DistNetBlock(name) {
    var topologyCapaity: Int = 0
    var computingPower: Int = 0
    var bufferSize: ObjectMap<DistBufferType<*>?, Int?> = ObjectMap<DistBufferType<*>?, Int?>()

    init {
        topologyUse = 0
        isNetLinker = false
    }

    public override fun setStats() {
        super.setStats()
        if (topologyCapaity > 0) stats.add(SglStat.topologyCapacity, topologyCapaity.toFloat())
        if (computingPower > 0) stats.add(SglStat.computingPower, (computingPower * 60).toFloat(), StatUnit.perSecond)
        if (bufferSize.size > 0) {
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
    }

    inner class CoreNeighbourComponentBuild : DistNetBuild() {
        override fun linkable(other: DistElementBuildComp?): Boolean {
            return false
        }

        override fun updateNetLinked() {
            super.updateNetLinked()
            for (building in proximity) {
                if (building is DistNetworkCoreComp) {
                    netLinked.add(building)
                }
            }
        }
    }
}