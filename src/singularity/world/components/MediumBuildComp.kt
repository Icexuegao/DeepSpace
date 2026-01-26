package singularity.world.components

import arc.util.Time
import mindustry.gen.Building
import universecore.components.blockcomp.BuildCompBase
import universecore.components.blockcomp.Takeable
import kotlin.math.max
import kotlin.math.min

interface MediumBuildComp : BuildCompBase, Takeable {
  var mediumContains: Float

  val mediumBlock: MediumComp
    get() = getBlock(MediumComp::class.java)

  fun acceptMedium(source: MediumBuildComp): Boolean {
    return building.interactable(source.building.team) && mediumContains < this.mediumBlock.mediumCapacity
  }

  fun remainingMediumCapacity(): Float {
    return this.mediumBlock.mediumCapacity - mediumContains
  }

  fun acceptMedium(source: MediumBuildComp, amount: Float): Float {
    return if (acceptMedium(source)) min(remainingMediumCapacity(), amount) else 0f
  }

  fun handleMedium(source: MediumBuildComp?, amount: Float) {
    mediumContains = (mediumContains + max(amount - this.mediumBlock.lossRate * Time.delta, 0f))
  }

  fun removeMedium(amount: Float) {
    mediumContains = (mediumContains - amount)
  }

  fun dumpMedium() {
    val next = getNext("medium") { e: Building? -> e is MediumBuildComp && (e as MediumBuildComp).acceptMedium(this, this.mediumBlock.mediumMoveRate) > 0 } as MediumBuildComp?
    if (next == null) return

    var move = min(mediumContains, this.mediumBlock.mediumMoveRate * building.delta())
    move = min(move, next.remainingMediumCapacity())

    removeMedium(move)
    next.handleMedium(this, move)
  }
}