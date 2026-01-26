package singularity.world.blocks.distribute

import arc.func.Prov

open class DistPowerEntry(name: String) : DistEnergyEntry(name) {
  var consPower: Float = 0f
  var eneProd: Float = 0f

  init {
    this.consumesPower = true
    this.hasPower = this.consumesPower
    this.buildType = Prov { DistPowerEntryBuild() }
  }

  public override fun init() {
    super.init()
    this.newConsume()
    this.consume!!.power(this.consPower)
  }

  inner class DistPowerEntryBuild : DistEnergyEntryBuild() {
    var energyProduct: Float = 0f

    public override fun updateTile() {
      super.updateTile()
      this.energyProduct = this@DistPowerEntry.eneProd * this.power.status
    }

    override fun matrixEnergyProduct(): Float {
      return this.energyProduct
    }
  }
}