package singularity.world.blocks.nuclear

import arc.func.Cons
import arc.func.Prov
import arc.math.Mathf
import arc.util.Nullable
import arc.util.io.Reads
import arc.util.io.Writes
import mindustry.world.meta.Stats
import singularity.world.blocks.SglBlock
import singularity.world.components.NuclearEnergyBuildComp

open class EnergyContainer(name: String) : SglBlock(name) {
    var energyPotential: Float = 256f
    var warmupSpeed: Float = 0.04f

    @Nullable
    var nonCons: Cons<EnergyContainerBuild?>? = null

    @Nullable
    var setStats: Cons<Stats?>? = null

    init {
        hasEnergy = true
        buildType = Prov(::EnergyContainerBuild)
    }

    override fun setStats() {
        super.setStats()
        if (setStats != null) setStats!!.get(stats)
    }

    inner class EnergyContainerBuild : SglBuilding() {
        var warmup: Float = 0f

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

        override val inputPotential: Float
            get() = energyPotential.coerceAtMost(getEnergy())
        override val outputPotential: Float
            get() = energyPotential.coerceAtMost(getEnergy())

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