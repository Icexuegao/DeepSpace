package singularity.world.blocks.nuclear

import arc.func.Prov
import arc.scene.ui.layout.Table
import mindustry.world.meta.Env
import singularity.world.components.NuclearEnergyBuildComp

open class EnergyVoid(name: String) : NuclearBlock(name) {
  init {
    energyCapacity = 65536f
    energyProtect = true
    outputEnergy = false
    consumeEnergy = true
    noUpdateDisabled = true
    envEnabled = Env.any
    buildType = Prov(::EnergyVoidBuild)
  }

  inner class EnergyVoidBuild : SglBuilding() {
    override fun handleEnergy(value: Float) {}
    override fun displayEnergy(table: Table) {}
    override fun acceptEnergy(source: NuclearEnergyBuildComp) = true
  }
}