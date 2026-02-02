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
    this.update = false
    this.destructible = true
    this.unloadable = false
    this.outputItems = false
    buildType = Prov(::MatrixContainerBuild)
  }

  public override fun init() {
    super.init()
    this.setDistSupport()
  }

  fun setDistSupport() {
    val cont = Sgl.matrixContainers.getContainer(this, Prov { DistSupportContainerTable.Container(this, this.isIntegrate) })
    if (this.hasItems) {
      cont.setCapacity(DistBufferType.itemBuffer, this.itemCapacity.toFloat())
    }

    if (this.hasLiquids) {
      cont.setCapacity(DistBufferType.liquidBuffer, this.liquidCapacity)
    }
  }

  inner class MatrixContainerBuild : SglBuilding() {
    public override fun acceptItem(source: Building, item: Item?): Boolean {
      if (!this@MatrixContainer.isIntegrate) {
        return super.acceptItem(source, item)
      } else {
        return this.interactable(source.team) && this.items.total() < this@MatrixContainer.itemCapacity
      }
    }

    override fun acceptStack(item: Item?, amount: Int, source: Teamc): Int {
      if (!this@MatrixContainer.isIntegrate) {
        return super.acceptStack(item, amount, source)
      } else {
        return if (this.interactable(source.team())) min(amount, this@MatrixContainer.itemCapacity - this.items.total()) else 0
      }
    }

    public override fun acceptLiquid(source: Building, liquid: Liquid?): Boolean {
      if (!this@MatrixContainer.isIntegrate) {
        return super.acceptLiquid(source, liquid)
      } else {
        return this.interactable(source.team) && this.liquids()!!.total() < this@MatrixContainer.liquidCapacity
      }
    }
  }
}