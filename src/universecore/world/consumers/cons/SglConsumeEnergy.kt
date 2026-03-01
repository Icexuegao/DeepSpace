package universecore.world.consumers.cons

import arc.math.Mathf
import arc.scene.ui.Image
import arc.scene.ui.layout.Table
import arc.util.Scaling
import mindustry.core.UI
import mindustry.gen.Building
import mindustry.ui.Styles
import mindustry.world.meta.Stats
import singularity.graphic.SglDrawConst
import singularity.world.components.NuclearEnergyBuildComp
import singularity.world.meta.SglStat
import singularity.world.meta.SglStatUnit
import universecore.components.blockcomp.ConsumerBuildComp
import universecore.world.consumers.BaseConsume
import universecore.world.consumers.ConsumeType

class SglConsumeEnergy<T>(var usage: Float) : BaseConsume<T>() where T : Building, T : NuclearEnergyBuildComp, T : ConsumerBuildComp {
  companion object {
    fun buildNuclearIcon(table: Table, amount: Float) {
      table.stack(Table { o: Table? ->
        o!!.left()
        o.add(Image(SglDrawConst.nuclearIcon)).size(32f).scaling(Scaling.fit)
      }, Table { t: Table? ->
        t!!.left().bottom()
        t.add(if (amount * 60 >= 1000) UI.formatAmount((amount * 60).toLong()) + "NF/s" else (amount * 60).toString() + "NF/s").style(Styles.outlineLabel)
        t.pack()
      })
    }
  }

  var buffer: Boolean = false

  fun buffer() {
    this.buffer = true
  }

  override fun type() = ConsumeType.energy

  override fun buildIcons(table: Table) {
    buildNuclearIcon(table, usage)
  }

  override fun merge(other: BaseConsume<T>) {
    if (other is SglConsumeEnergy<*>) {
      buffer = buffer or other.buffer
      usage += other.usage

      return
    }
    throw IllegalArgumentException("only merge consume with same type")
  }

  override fun consume(entity: T) {
    if (buffer) entity.handleEnergy(-usage * 60 * multiple(entity))
  }

  override fun update(entity: T) {
    if (!buffer) {
      entity.handleEnergy(-usage * parent!!.delta(entity))
    }
  }

  override fun display(stats: Stats) {
    stats.add(SglStat.consumeEnergy, usage * 60, SglStatUnit.neutronFluxSecond)
  }

  override fun build(entity: T, table: Table) {
    table.row()
  }

  override fun efficiency(entity: T): Float {
    if (buffer) {
      return (if (entity.energy().energy >= usage * 60 * multiple(entity)) 1 else 0).toFloat()
    }
    return Mathf.clamp(entity.energy().energy / (usage * 12.5f * multiple(entity)))
  }

  override fun filter() = null
}