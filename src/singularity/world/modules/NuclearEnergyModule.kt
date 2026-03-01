package singularity.world.modules

import arc.math.WindowedMean
import arc.scene.ui.layout.Table
import arc.util.Interval
import arc.util.Scaling
import arc.util.Strings
import arc.util.io.Reads
import arc.util.io.Writes
import mindustry.Vars
import mindustry.core.UI
import mindustry.gen.Building
import mindustry.graphics.Pal
import mindustry.ui.Bar
import mindustry.world.modules.BlockModule
import singularity.core.UpdatePool
import singularity.graphic.SglDrawConst
import singularity.world.components.NuclearEnergyBuildComp
import universecore.util.handler.FieldHandler

class NuclearEnergyModule(val entity: NuclearEnergyBuildComp) : BlockModule() {
  companion object {
    var lastShowFlow: NuclearEnergyBuildComp? = null

    init {
      UpdatePool.receive("updateEnergyFlow") {
        val nextFlowBuild = FieldHandler.getValueDefault<Building?>(Vars.ui.hudfrag.blockfrag, "nextFlowBuild")
        if (nextFlowBuild is NuclearEnergyBuildComp && nextFlowBuild.hasEnergy()) {
          if (lastShowFlow !== nextFlowBuild) {
            nextFlowBuild.energy().stopFlow()
            lastShowFlow = nextFlowBuild
          }

          nextFlowBuild.energy().updateFlow()
        }
      }
    }
  }

  private var added = 0f
  private var removed = 0f
  private val addedMean = WindowedMean(6)
  private val moveMean = WindowedMean(6)
  private val flowTimer = Interval()

  /** 目前具有的核能量大小  */
  var energy: Float = 0f

  var displayAdding: Float = 0f
  var displayMoving: Float = 0f

  fun stopFlow() {
    added = 0f
    removed = 0f
    addedMean.clear()
    moveMean.clear()
    displayAdding = 0f
    displayMoving = 0f
  }

  fun updateFlow() {
    if (flowTimer.get(20f)) {
      addedMean.add(added)
      moveMean.add(removed)
      added = 0f
      removed = 0f
      displayAdding = if (addedMean.hasEnoughData()) addedMean.mean() / 20 else -1f
      displayMoving = if (moveMean.hasEnoughData()) moveMean.mean() / 20 else -1f
    }
  }

  fun update() {
  }

  fun handle(value: Float) {
    energy += value
    if (value >= 0) added += value
    if (value < 0) removed += value
  }

  fun display(table: Table) {
    table.row()
    table.table { energyBoard ->
      energyBoard.defaults().pad(5f).left()
      energyBoard.image(SglDrawConst.nuclearIcon).size(20f).get().setScaling(Scaling.fit)
      energyBoard.add(
        Bar({
          val s1 = if (energy >= 1000) UI.formatAmount(energy.toLong()) else Strings.autoFixed(energy, 1)
          val s2 = if (entity.energyCapacity() >= 1000) UI.formatAmount(entity.energyCapacity().toLong()) else Strings.autoFixed(entity.energyCapacity(), 1)
          val s3 = if (displayAdding < -0.1f) "--" else "+" + UI.formatAmount((displayAdding * 60).toLong())
          "${s1}NF/${s2}NF ${s3}NF/秒"
        }, { Pal.reactorPurple }, { energy / entity.energyCapacity() })
      ).height(18f).padLeft(4f).growX()
    }.growX().fillY()
  }

  override fun write(write: Writes) {
    write.f(energy)
  }

  override fun read(read: Reads) {
    energy = read.f()
  }
}