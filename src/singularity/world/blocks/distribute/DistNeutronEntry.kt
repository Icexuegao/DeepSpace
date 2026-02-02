package singularity.world.blocks.distribute

import arc.func.Prov
import arc.util.Time
import kotlin.math.min

open class DistNeutronEntry(name: String) : DistEnergyEntry(name) {
  var maxEnergyInput: Float = 20.0f

  init {
    this.hasEnergy = true
    this.consumeEnergy = true
    this.energyCapacity = 1024.0f
    buildType= Prov(::DistNeutronEntryBuild)
  }

  inner class DistNeutronEntryBuild : DistEnergyEntryBuild() {
    var energyProduct: Float = 0f

    public override fun updateTile() {
      super.updateTile()
      if (this.distributor.network.netStructValid()) {
        val energyInput = min(this@DistNeutronEntry.maxEnergyInput, this.energy.getEnergy())
        this.energyProduct = energyInput * 1.25f
        this.handleEnergy(-energyInput * Time.delta)
      }
    }

    override fun matrixEnergyProduct(): Float {
      return this.energyProduct
    }
  }
}