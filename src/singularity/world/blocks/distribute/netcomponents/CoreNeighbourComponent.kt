package singularity.world.blocks.distribute.netcomponents

import arc.Core
import arc.func.Prov
import arc.scene.ui.layout.Table
import arc.struct.ObjectMap
import mindustry.world.meta.StatUnit
import singularity.world.blocks.distribute.DistNetBlock
import singularity.world.components.distnet.DistElementBuildComp
import singularity.world.components.distnet.DistNetworkCoreComp
import singularity.world.distribution.DistBufferType
import singularity.world.meta.SglStat
import universecore.util.NumberStrify

open class CoreNeighbourComponent(name: String) : DistNetBlock(name) {
  var topologyCapaity: Int = 0
  var computingPower: Int = 0
  var bufferSize: ObjectMap<DistBufferType<*>, Int> = ObjectMap<DistBufferType<*>, Int>()

  init {
    this.topologyUse = 0
    this.isNetLinker = false
    buildType= Prov(::CoreNeighbourComponentBuild)
  }

  override fun setStats() {
    super.setStats()
    if (this.topologyCapaity > 0) {
      this.stats.add(SglStat.topologyCapacity, this.topologyCapaity.toFloat())
    }

    if (this.computingPower > 0) {
      this.stats.add(SglStat.computingPower, (this.computingPower * 60).toFloat(), StatUnit.perSecond)
    }

    if (this.bufferSize.size > 0) {
      this.stats.add(SglStat.bufferSize) { t: Table? ->
        t!!.defaults().left().fillX().padLeft(10.0f)
        t.row()
        val var2: ObjectMap.Entries<*, *> = this.bufferSize.iterator()
        while (var2.hasNext()) {
          val entry: ObjectMap.Entry<DistBufferType<*>, Int> = var2.next() as ObjectMap.Entry<DistBufferType<*>, Int>
          if (entry.value > 0) {
            t.add(Core.bundle.get("content." + (entry.key as DistBufferType<*>).targetType().name + ".name") + ": " + NumberStrify.toByteFix(entry.value.toDouble(), 2))
            t.row()
          }
        }
      }
    }
  }

  inner class CoreNeighbourComponentBuild : DistNetBuild() {
    override fun linkable(other: DistElementBuildComp): Boolean {
      return false
    }

    override fun updateNetLinked() {
      super.updateNetLinked()

      for (building in this.proximity) {
        if (building is DistNetworkCoreComp) {
          val core = building as DistNetworkCoreComp
          this.netLinked.add(core)
        }
      }
    }
  }
}