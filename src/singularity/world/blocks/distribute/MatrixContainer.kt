package singularity.world.blocks.distribute

import arc.func.Prov
import mindustry.gen.Building
import mindustry.gen.Teamc
import mindustry.type.Item
import mindustry.type.Liquid
import singularity.Sgl
import singularity.world.blocks.SglBlock
import singularity.world.distribution.DistBufferType
import singularity.world.distribution.DistSupportContainerTable
import kotlin.math.min

class MatrixContainer(name: String) : SglBlock(name) {
    var isIntegrate: Boolean = true

    init {
        update = false
        destructible = true
        unloadable = false
        outputItems = false
    }

    public override fun init() {
        super.init()
        setDistSupport()
    }

    fun setDistSupport() {
        val cont = Sgl.matrixContainers.getContainer(this, Prov { DistSupportContainerTable.Container(this, isIntegrate) })
        if (hasItems) cont.setCapacity(DistBufferType.itemBuffer, itemCapacity.toFloat())
        if (hasLiquids) cont.setCapacity(DistBufferType.liquidBuffer, liquidCapacity)
    }

    inner class MatrixContainerBuild : SglBuilding() {
        public override fun acceptItem(source: Building, item: Item?): Boolean {
            if (!isIntegrate) return super.acceptItem(source, item)
            return interactable(source.team) && items.total() < itemCapacity
        }

        override fun acceptStack(item: Item?, amount: Int, source: Teamc): Int {
            if (!isIntegrate) return super.acceptStack(item, amount, source)
            return if (interactable(source.team())) min(amount, itemCapacity - items.total()) else 0
        }

        public override fun acceptLiquid(source: Building, liquid: Liquid?): Boolean {
            if (!isIntegrate) return super.acceptLiquid(source, liquid)
            return interactable(source.team) && liquids()!!.total() < liquidCapacity
        }
    }
}