package singularity.world.blocks.distribute

import arc.Core
import arc.func.Prov
import arc.util.Strings
import mindustry.core.UI
import mindustry.ui.Bar
import singularity.graphic.SglDrawConst
import singularity.world.components.distnet.DistElementBuildComp

open class DistEnergyBuffer(name: String) : DistEnergyEntry(name) {
  init {
    buildType= Prov(::DistEnergyBufferBuild)
  }
  override fun setBars() {
    super.setBars()
    this.addBar("energyBuffered") { e: DistEnergyBufferBuild -> Bar({ Core.bundle.format("bar.energyBuffered", *arrayOf<Any>(if (e.matrixEnergyBuffered >= 1000.0f) UI.formatAmount(e.matrixEnergyBuffered.toLong()) else Strings.autoFixed(e.matrixEnergyBuffered, 1), if (this.matrixEnergyCapacity >= 1000.0f) UI.formatAmount(this.matrixEnergyCapacity.toLong()) else Strings.autoFixed(this.matrixEnergyCapacity, 1))) }, { SglDrawConst.matrixNet }, { e.matrixEnergyBuffered / this.matrixEnergyCapacity }) }
  }

  inner class DistEnergyBufferBuild : DistEnergyEntryBuild() {
    override fun linkable(other: DistElementBuildComp): Boolean {
      return false
    }
  }
}