package singularity.world.blocks.distribute.netcomponents

import arc.Core
import arc.scene.ui.layout.Table
import arc.struct.ObjectMap
import mindustry.world.meta.StatUnit
import mindustry.world.meta.StatValue
import singularity.world.blocks.distribute.DistNetBlock
import singularity.world.distribution.DistBufferType
import singularity.world.meta.SglStat
import universecore.util.NumberStrify

open class NetPluginComp(name: String) : DistNetBlock(name) {
    var computingPower: Int = 0
    var topologyCapacity: Int = 0
    protected var bufferSize: ObjectMap<DistBufferType<*>?, Int?> = ObjectMap<DistBufferType<*>?, Int?>()

    init {
        update = true
    }

    public override fun setStats() {
        super.setStats()
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

    fun setBufferSize(buffer: DistBufferType<*>?, size: Int) {
        bufferSize.put(buffer, size)
    }

    open inner class NetPluginCompBuild : DistNetBuild() {
        override fun updateNetStat() {
            distributor!!.network.handleCalculatePower(computingPower)
            distributor!!.network.handleTopologyCapacity(topologyCapacity)
            for (entry in bufferSize) {
                distributor!!.network.handleBufferCapacity(entry.key!!, entry.value!!)
            }
        }
    }
}