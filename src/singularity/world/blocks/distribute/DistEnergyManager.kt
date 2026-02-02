package singularity.world.blocks.distribute

import arc.Core
import arc.func.Prov
import arc.util.Strings
import mindustry.core.UI
import mindustry.ui.Bar
import singularity.graphic.SglDrawConst
import singularity.world.blocks.distribute.DistEnergyEntry.DistEnergyEntryBuild

open class DistEnergyManager(name: String) : DistNetBlock(name) {
  init {
    this.matrixEnergyCapacity = 2048.0f
    this.isNetLinker = true
    buildType= Prov(::DistEnergyManagerBuild)
  }

  override fun setBars() {
    super.setBars()
    this.addBar("energyBuffered") { e: DistEnergyManagerBuild ->

      Bar({ Core.bundle.format("bar.energyBuffered", *arrayOf<Any>(if (e.matrixEnergyBuffered >= 1000.0f) UI.formatAmount(e.matrixEnergyBuffered.toLong()) else Strings.autoFixed(e.matrixEnergyBuffered, 1), if (this.matrixEnergyCapacity >= 1000.0f) UI.formatAmount(this.matrixEnergyCapacity.toLong()) else Strings.autoFixed(this.matrixEnergyCapacity, 1))) }, { SglDrawConst.matrixNet }, { e.matrixEnergyBuffered / this.matrixEnergyCapacity }) }
  }

  inner class DistEnergyManagerBuild : DistNetBuild() {
    override fun updateNetLinked() {
      super.updateNetLinked()

      for (building in this.proximity) {
        if (building is DistEnergyEntryBuild) {
          val entry = building
          this.netLinked.add(entry)
        }
      }
    }

    override fun onProximityUpdate() {
      super.onProximityUpdate()
      this.updateNetLinked()
    }
  }
}