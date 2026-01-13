package singularity.world.blocks.nuclear

import arc.func.Prov
import arc.scene.ui.layout.Table
import mindustry.world.meta.Env
import singularity.world.components.NuclearEnergyBuildComp

open class EnergySource(name: String) : NuclearNode(name) {
    init {
        energyCapacity = 65536f
        outputEnergy = true
        consumeEnergy = false
        configurable = true
        noUpdateDisabled = true
        envEnabled = Env.any
        buildType = Prov(::EnergySourceBuild)
    }

    inner class EnergySourceBuild : NuclearNodeBuild() {
        override fun moveEnergy(next: NuclearEnergyBuildComp): Float {
            val adding = next.energyCapacity() - next.getEnergy()
            next.energy().handle(adding)
            energyMoved(next, adding)
            return adding
        }

        override fun getEnergyMoveRate(next: NuclearEnergyBuildComp): Float {
            return 1f
        }

        override fun displayEnergy(table: Table) {
            //不执行任何操作
        }

        override fun config(): Any {
            return outputEnergy
        }

        override fun acceptEnergy(source: NuclearEnergyBuildComp): Boolean {
            return false
        }

    }
}