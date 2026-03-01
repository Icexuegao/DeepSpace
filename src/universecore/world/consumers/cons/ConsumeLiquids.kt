package universecore.world.consumers.cons

import arc.math.Mathf
import arc.scene.ui.layout.Table
import arc.struct.ObjectMap
import arc.struct.Seq
import ice.ui.bundle.BaseBundle.Bundle.Companion.localizedName
import ice.world.meta.IceStats
import mindustry.ctype.Content
import mindustry.gen.Building
import mindustry.type.Liquid
import mindustry.type.LiquidStack
import mindustry.ui.ReqImage
import mindustry.world.meta.Stat
import mindustry.world.meta.StatValues
import mindustry.world.meta.Stats
import universecore.components.blockcomp.ConsumerBuildComp
import universecore.world.consumers.BaseConsume
import universecore.world.consumers.ConsumeType
import kotlin.math.min

class ConsumeLiquids<T>(liquids: Array<out LiquidStack>) : ConsumeLiquidBase<T>() where T : Building, T : ConsumerBuildComp {
  companion object {
    private val TMP = ObjectMap<Liquid, LiquidStack>()
  }

  /**是否只需要满足任意一项*/
  var portion: Boolean = false

  init {
    this.consLiquids = arrayOf(*liquids)
  }

  override fun type() = ConsumeType.liquid

  override fun buildIcons(table: Table) {
    buildLiquidIcons(table, consLiquids!!, false, displayLim)
  }

  override fun merge(other: BaseConsume<T>) {
    if (other is ConsumeLiquids<*>) {
      TMP.clear()
      for (stack in consLiquids!!) {
        TMP.put(stack.liquid, stack)
      }

      for (stack in other.consLiquids!!) {
        TMP.get(stack.liquid) { LiquidStack(stack.liquid, 0f) }!!.amount += stack.amount
      }

      consLiquids = TMP.values().toSeq().sort(Comparator { a: LiquidStack?, b: LiquidStack? -> a!!.liquid.id - b!!.liquid.id }).toArray(LiquidStack::class.java)
      return
    }
    throw IllegalArgumentException("only merge consume with same type")
  }

  override fun consume(entity: T) {
    if (portion) for (stack in consLiquids!!) {
      entity.liquids.remove(stack.liquid, stack.amount * 60 * multiple(entity))
    }
  }

  override fun update(entity: T) {
    if (!portion) for (stack in consLiquids!!) {
      entity.liquids.remove(stack.liquid, stack.amount * parent!!.delta(entity) * multiple(entity))
    }
  }

  override fun display(stats: Stats) {
    stats.add(Stat.input) { table: Table? ->
      table!!.row()
      table.table { t: Table? ->
        t!!.defaults().left().fill().padLeft(6f)
        t.add("${IceStats.流体.localizedName}:")
        for (stack in consLiquids!!) {
          t.add(StatValues.displayLiquid(stack.liquid, stack.amount * 60, true))
        }
      }.left().padLeft(5f)
    }
  }

  override fun build(entity: T, table: Table) {
    for (stack in consLiquids!!) {
      table.add(
        ReqImage(
          stack.liquid.uiIcon
        ) { entity.liquids != null && entity.liquids.get(stack.liquid) > 0 }
      ).padRight(8f)
    }
    table.row()
  }

  override fun efficiency(entity: T): Float {
    if (entity.liquids == null) return 0f
    if (portion) {
      for (stack in consLiquids!!) {
        if (entity.liquids.get(stack.liquid) < stack.amount * multiple(entity) * 60) return 0f
      }
      return 1f
    } else {
      var min = 1f

      for (stack in consLiquids!!) {
        min = min(entity.liquids.get(stack.liquid) / (stack.amount * multiple(entity)), min)
      }

      if (min < 0.0001f) return 0f
      return Mathf.clamp(min)
    }
  }

  override fun filter(): Seq<Content>? {
    return Seq.with(*consLiquids!!).map { s: LiquidStack -> s.liquid }
  }
}