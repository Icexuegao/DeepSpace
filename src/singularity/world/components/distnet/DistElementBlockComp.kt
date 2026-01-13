package singularity.world.components.distnet

import arc.Core
import arc.util.Strings
import mindustry.world.meta.Stats
import singularity.world.meta.SglStat
import singularity.world.meta.SglStatUnit

interface DistElementBlockComp {
  //  @Annotations.BindField(value = "topologyUse", initialize = "1")
    fun topologyUse(): Int {
        return 0
    }

  //  @Annotations.BindField("matrixEnergyUse")
    fun matrixEnergyUse(): Float {
        return 0f
    }

   // @Annotations.BindField("matrixEnergyCapacity")
    fun matrixEnergyCapacity(): Float {
        return 0f
    }

  //  @get:Annotations.BindField("isNetLinker")
    val isNetLinker: Boolean


  //  @MethodEntry(entryMethod = "setStats", context = "stats -> stats")
    fun setDistNetStats(stats: Stats) {
        if (matrixEnergyUse() > 0) stats.add(
            SglStat.matrixEnergyUse,
            Strings.autoFixed(matrixEnergyUse() * 60, 2) + SglStatUnit.matrixEnergy.localized() + Core.bundle.get("misc.perSecond")
        )
        if (matrixEnergyCapacity() > 0) stats.add(SglStat.matrixEnergyCapacity, matrixEnergyCapacity(), SglStatUnit.matrixEnergy)
        if (topologyUse() > 0) stats.add(SglStat.topologyUse, topologyUse().toFloat())
    }
}