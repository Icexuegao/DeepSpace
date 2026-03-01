package universecore.world.consumers.cons

import arc.Core
import arc.func.Boolp
import arc.func.Floatp
import arc.math.Mathf
import arc.scene.ui.Image
import arc.scene.ui.layout.Table
import arc.struct.Seq
import arc.util.Scaling
import mindustry.core.UI
import mindustry.ctype.Content
import mindustry.gen.Building
import mindustry.gen.Icon
import mindustry.graphics.Pal
import mindustry.ui.Bar
import mindustry.ui.Styles
import mindustry.world.meta.Stat
import mindustry.world.meta.StatUnit
import mindustry.world.meta.Stats
import universecore.components.blockcomp.ConsumerBuildComp
import universecore.world.consumers.BaseConsume
import universecore.world.consumers.ConsumeType

/*仅仅保存消耗参数，能量消耗本身实际应用仍为默认consumes*/
open class ConsumePower<T>(var usage: Float, var capacity: Float) : BaseConsume<T>() where T : Building, T : ConsumerBuildComp {
  companion object {
    fun buildPowerImage(table: Table, usage: Float) {
      table.stack(Table { o: Table? ->
        o!!.left()
        o.add(Image(Icon.power)).size(32f).scaling(Scaling.fit)
      }, Table { t: Table? ->
        t!!.left().bottom()
        t.add(if (usage * 60 >= 1000) UI.formatAmount((usage * 60).toLong()) + "/s" else (usage * 60).toInt().toString() + "/s").style(Styles.outlineLabel)
        t.pack()
      })
    }
  }

  var buffered: Boolean
  var showIcon: Boolean = false
  var others: Seq<ConsumePower<T>> = Seq<ConsumePower<T>>(ConsumePower::class.java)

  init {
    buffered = capacity > 0f
  }

  override fun type() = ConsumeType.power

  override fun hasIcons() = showIcon

  override fun buildIcons(table: Table) {
    if (showIcon) {
      buildPowerImage(table, usage)
    }
  }

  override fun merge(other: BaseConsume<T>) {
    if (other is ConsumePower<*>) {
      buffered = buffered or other.buffered
      capacity += other.capacity

      others.add(other as ConsumePower)
      return
    }
    throw IllegalArgumentException("only merge consume with same type")
  }

  open fun requestedPower(entity: T): Float {
    var res = usage
    for (other in others.items) {
      if (other == null) continue
      res += other.requestedPower(entity)
    }
    return res
  }

  override fun buildBars(entity: T, bars: Table) {
    val buffered = Boolp { entity.block.consPower.buffered }
    val capacity = Floatp { entity.block.consPower.capacity }
    bars.add(
      Bar({
        if (buffered.get()) {
          val naN = (entity.power.status * capacity.get()).isNaN()
          val format = Core.bundle.format(
            "bar.poweramount", if (naN) "<ERROR>" else {
              val toInt = (entity.power.status * capacity.get()).toInt()
              toInt
            }
          )
          format
        } else {
          val get = Core.bundle.get("bar.power")
          get
        }
      }, { Pal.powerBar }, { if (Mathf.zero(entity.block.consPower.requestedPower(entity)) && entity.power.graph.getPowerProduced() + entity.power.graph.getBatteryStored() > 0f) 1f else entity.power.status })
    ).growX()
    bars.row()
  }

  override fun build(entity: T, table: Table) {}

  override fun update(entity: T) {}

  override fun efficiency(entity: T): Float {
    if (entity.power == null) return 0f
    return if (buffered) {
      (if (entity.power.status > 0) 1 else 0).toFloat()
    } else {
      entity.power.status
    }
  }

  override fun display(stats: Stats) {
    stats.add(Stat.powerUse, usage * 60f, StatUnit.powerSecond)
  }

  override fun consume(entity: T) {}

  override fun filter(): Seq<Content>? {
    return null
  }
}