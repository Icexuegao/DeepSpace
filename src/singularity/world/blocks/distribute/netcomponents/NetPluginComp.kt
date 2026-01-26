package singularity.world.blocks.distribute.netcomponents

import arc.Core
import arc.func.Prov
import arc.struct.ObjectMap
import mindustry.world.meta.StatUnit
import singularity.world.blocks.distribute.DistNetBlock
import singularity.world.distribution.DistBufferType
import singularity.world.meta.SglStat
import universecore.util.NumberStrify

open class NetPluginComp(name: String) : DistNetBlock(name) {
  var computingPower: Int = 0
  var topologyCapacity: Int = 0
  var bufferSize = ObjectMap<DistBufferType<*>, Int>()

  init {
    this.update = true
    buildType = Prov(::NetPluginCompBuild)
  }

  override fun setStats() {
    super.setStats()
    if (this.computingPower > 0) {
      this.stats.add(SglStat.computingPower, (this.computingPower * 60).toFloat(), StatUnit.perSecond)
    }

    if (this.bufferSize.size > 0) {
      this.stats.add(SglStat.bufferSize, { t ->
        t.defaults().left().fillX().padLeft(10.0f)
        t.row()
        val var2: ObjectMap.Entries<DistBufferType<*>, Int> = this.bufferSize.iterator()
        while (var2.hasNext()) {
          val entry: ObjectMap.Entry<DistBufferType<*>, Int> = var2.next()
          if (entry.value > 0) {
            t.add(Core.bundle.get("content." + (entry.key as DistBufferType<*>).targetType().name + ".name") + ": " + NumberStrify.toByteFix(entry.value.toDouble(), 2))
            t.row()
          }
        }
      })
    }
  }

  fun setBufferSize(buffer: DistBufferType<*>?, size: Int) {
    this.bufferSize.put(buffer, size)
  }

  open inner class NetPluginCompBuild : DistNetBuild() {
   override fun updateNetStat() {
      this.distributor.network.handleCalculatePower(this@NetPluginComp.computingPower)
      this.distributor.network.handleTopologyCapacity(this@NetPluginComp.topologyCapacity)
      val var1: ObjectMap.Entries<DistBufferType<*>, Int> = this@NetPluginComp.bufferSize.iterator()

      while (var1.hasNext()) {
        val entry: ObjectMap.Entry<DistBufferType<*>, Int> = var1.next() as ObjectMap.Entry<DistBufferType<*>, Int>
        this.distributor.network.handleBufferCapacity(entry.key, entry.value)
      }
    }
  }
}