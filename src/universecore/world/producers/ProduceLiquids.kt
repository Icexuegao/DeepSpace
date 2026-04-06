package universecore.world.producers

import arc.graphics.Color
import arc.scene.ui.layout.Table
import arc.struct.ObjectMap
import ice.world.meta.IStatValues
import ice.world.meta.IceStats
import mindustry.gen.Building
import mindustry.type.Liquid
import mindustry.type.LiquidStack
import mindustry.ui.Bar
import mindustry.world.meta.Stat
import mindustry.world.meta.Stats
import universecore.components.blockcomp.ProducerBuildComp
import universecore.world.consumers.cons.liquid.ConsumeLiquidBase
import kotlin.math.min

class ProduceLiquids<T>(var liquids: Array<out LiquidStack>) : BaseProduce<T>() where T : Building, T : ProducerBuildComp {
  var displayLim: Int = 4
  var portion: Boolean = false

  fun portion(): ProduceLiquids<T> {
    this.portion = true
    return this
  }

  override fun buildBars(entity: T, bars: Table) {
    for (stack in liquids) {
      bars.add(Bar({stack.liquid.localizedName}, {stack.liquid.barColor ?: stack.liquid.color}, {min(entity.liquids.get(stack.liquid) / entity.block.liquidCapacity, 1f)}))
      bars.row()
    }
  }

  override fun type(): ProduceType<ProduceLiquids<*>> {
    return ProduceType.liquid
  }

  override fun color(): Color? {
    return liquids[0].liquid.color
  }

  override fun buildIcons(table: Table) {
    ConsumeLiquidBase.buildLiquidIcons(table, liquids, false, displayLim)
  }

  override fun merge(other: BaseProduce<T>) {
    if (other is ProduceLiquids<*>) {
      TMP.clear()
      for (stack in liquids) {
        TMP.put(stack.liquid, stack)
      }

      for (stack in other.liquids) {
        TMP.get(stack.liquid) {LiquidStack(stack.liquid, 0f)}!!.amount += stack.amount
      }

      liquids = TMP.values().toSeq().sort(Comparator {a: LiquidStack?, b: LiquidStack? -> a!!.liquid.id - b!!.liquid.id}).toArray(LiquidStack::class.java)
      return
    }
    throw IllegalArgumentException("only merge production with same type")
  }

  override fun produce(entity: T) {
    if (portion) for (stack in liquids) {
      entity.handleLiquid(entity, stack.liquid, stack.amount * 60)
    }
  }

  override fun update(entity: T) {
    if (!portion) for (stack in liquids) {
      var amount = stack.amount * parent!!.cons!!.delta(entity) * multiple(entity)
      amount = min(amount, entity.block.liquidCapacity - entity.liquids.get(stack.liquid))
      entity.handleLiquid(entity, stack.liquid, amount)
    }
  }

  override fun dump(entity: T) {
    for (stack in liquids) {
      if (entity.liquids.get(stack.liquid) > 0.01f) entity.dumpLiquid(stack.liquid)
    }
  }

  override fun display(stats: Stats) {
    stats.add(Stat.output) {table: Table ->
      table.row()
      table.table {t: Table ->
        t.defaults().left().fill().padLeft(6f)
        t.add("${IceStats.流体.getLocalizedName()}:").left()
        for (stack in liquids) {
          t.add(IStatValues.displayLiquid(stack.liquid, stack.amount, true, showName = true))
        }
      }.left().padLeft(5f)
    }
  }

  override fun valid(entity: T): Boolean {
    if (entity.liquids == null) return false
    var res = false
    for (stack in liquids) {
      if (entity.liquids.get(stack.liquid) + stack.amount * multiple(entity) > entity.block.liquidCapacity - 0.001f) {
        if (blockWhenFull) return false
      } else res = true
    }
    return res
  }

  companion object {
    private val TMP = ObjectMap<Liquid?, LiquidStack?>()
  }
}