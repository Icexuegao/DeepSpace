package singularity.world.blocks.nuclear

import arc.func.Cons
import arc.func.Prov
import arc.math.Mathf
import arc.util.io.Reads
import arc.util.io.Writes
import ice.library.util.toStringi
import ice.world.meta.IceStats
import mindustry.world.meta.Stats
import singularity.world.blocks.SglBlock
import singularity.world.components.NuclearEnergyBuildComp
import singularity.world.meta.SglStatUnit
import kotlin.math.min

open class EnergyContainer(name: String) : SglBlock(name) {
  var energyPotential: Float = 256f
  var warmupSpeed: Float = 0.04f
  var nonCons: Cons<EnergyContainerBuild>? = null
  var setStats: Cons<Stats>? = null

  init {
    hasEnergy = true
    buildType = Prov(::EnergyContainerBuild)
  }

  override fun setStats() {
    super.setStats()
    stats.remove(IceStats.最大能量势)
    stats.add(IceStats.最大能量势,energyPotential,SglStatUnit.neutronFlux)
    if (setStats != null) setStats!!.get(stats)
  }

  inner class EnergyContainerBuild : SglBuilding() {
    var warmup: Float = 0f

    override val inputPotential: Float
      get() = min(energyPotential, getEnergy())
    override val outputPotential: Float
      get() = min(energyPotential, getEnergy())

    override fun warmup(): Float {
      return warmup
    }

    override fun consEfficiency(): Float {
      return super.consEfficiency() * warmup
    }

    override fun updateTile() {
      super.updateTile()
      dumpEnergy()

      warmup = Mathf.lerpDelta(warmup, (if (shouldConsume() && consumeValid()) 1 else 0).toFloat(), warmupSpeed)

      if (!consumers.isEmpty && consumer != null && nonCons != null && (!consumeValid() || !shouldConsume())) nonCons!!.get(this)
    }

    override fun getEnergyPressure(other: NuclearEnergyBuildComp): Float {
      return if (other is EnergyContainerBuild) getEnergy() - other.getEnergy() else super.getEnergyPressure(other)
    }

    override fun read(read: Reads, revision: Byte) {
      super.read(read, revision)
      warmup = read.f()
    }

    override fun write(write: Writes) {
      super.write(write)
      write.f(warmup)
    }
  }
}