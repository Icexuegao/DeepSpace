package singularity.world.blocks.distribute

import arc.func.Prov

open class DistPowerEntry(name: String) : DistEnergyEntry(name) {
    var consPower: Float = 0f
    var eneProd: Float = 0f

    init {
        consumesPower = true
        hasPower = consumesPower
        buildType = Prov { DistPowerEntryBuild() }
    }

    public override fun init() {
        super.init()
        newConsume()
        consume!!.power(consPower)
    }

    inner class DistPowerEntryBuild : DistEnergyEntryBuild() {
        var energyProduct: Float = 0f

        public override fun updateTile() {
            super.updateTile()
            energyProduct = eneProd * power.status
        }

        override fun matrixEnergyProduct(): Float {
            return energyProduct
        }
    }
}