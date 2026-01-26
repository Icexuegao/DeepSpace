package singularity.world.components.distnet

import arc.Core
import arc.util.Strings
import mindustry.world.meta.Stats
import singularity.world.meta.SglStat
import singularity.world.meta.SglStatUnit

interface DistElementBlockComp {
  //  @Annotations.BindField(value = "topologyUse", initialize = "1")
  var topologyUse: Int

  //  @Annotations.BindField("matrixEnergyUse")
  var matrixEnergyUse: Float

  // @Annotations.BindField("matrixEnergyCapacity")
  var matrixEnergyCapacity: Float

  //  @get:Annotations.BindField("isNetLinker")
  val isNetLinker: Boolean

  //  @MethodEntry(entryMethod = "setStats", context = "stats -> stats")
  fun setDistNetStats(stats: Stats) {
    if (matrixEnergyUse > 0) stats.add(
      SglStat.matrixEnergyUse, Strings.autoFixed(matrixEnergyUse * 60, 2) + SglStatUnit.matrixEnergy.localized() + Core.bundle.get("misc.perSecond")
    )
    if (matrixEnergyCapacity > 0) stats.add(SglStat.matrixEnergyCapacity, matrixEnergyCapacity, SglStatUnit.matrixEnergy)
    if (topologyUse > 0) stats.add(SglStat.topologyUse, topologyUse.toFloat())
  }
}