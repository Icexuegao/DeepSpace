package universecore.world.consumers.cons

import arc.math.Mathf
import arc.scene.ui.layout.Table
import arc.struct.ObjectMap
import arc.struct.Seq
import ice.ui.bundle.BaseBundle.Bundle.Companion.localizedName
import ice.world.meta.IceStats
import mindustry.ctype.Content
import mindustry.gen.Building
import mindustry.type.Item
import mindustry.type.ItemStack
import mindustry.ui.ReqImage
import mindustry.world.meta.Stat
import mindustry.world.meta.StatValues
import mindustry.world.meta.Stats
import universecore.components.blockcomp.ConsumerBuildComp
import universecore.world.consumers.BaseConsume
import kotlin.math.floor

class ConsumeItems<T>(items: Array<out ItemStack>) : ConsumeItemBase<T>() where T : ConsumerBuildComp, T : Building {
  companion object {
    private val TMP = ObjectMap<Item, ItemStack>()
  }

  var showPerSecond: Boolean = true

  init {
    this.consItems = arrayOf(*items)
  }

  override fun buildIcons(table: Table) {
    buildItemIcons(table, consItems!!, false, displayLim)
  }

  override fun merge(other: BaseConsume<T>) {
    if (other is ConsumeItems<*>) {
      TMP.clear()
      for (stack in consItems!!) {
        TMP.put(stack.item, stack)
      }

      for (stack in other.consItems!!) {
        TMP.get(stack.item) { ItemStack(stack.item, 0) }!!.amount += stack.amount
      }

      consItems = TMP.values().toSeq().sort(Comparator { a: ItemStack?, b: ItemStack? -> a!!.item.id - b!!.item.id }).toArray(ItemStack::class.java)
      return
    }
    throw IllegalArgumentException("only merge consume with same type")
  }

  override fun consume(entity: T) {
    val f = multiple(entity)
    for (stack in consItems!!) {
      val amount = stack.amount * (floor(f.toDouble()).toInt()) + Mathf.num(Math.random() < f % 1)
      entity.items.remove(stack.item, amount)
    }
  }

  override fun update(entity: T) {}

  override fun display(stats: Stats) {
    stats.add(Stat.input) { table ->
      table.row()
      table.table { t ->
        t.defaults().left().grow().fill().padLeft(6f)
        t.add("${IceStats.物品.localizedName}:")
        for (stack in consItems!!) {
          t.add(if (showPerSecond) StatValues.displayItem(stack.item, stack.amount, parent!!.craftTime, true) else StatValues.displayItem(stack.item, stack.amount, true))
        }
      }.left().padLeft(5f)
    }
  }

  override fun build(entity: T, table: Table) {
    for (stack in consItems!!) {
      var amount = (stack.amount * multiple(entity)).toInt()
      if (amount == 0 && !entity.consumer.valid()) amount = stack.amount
      val n = amount
      table.add(
        ReqImage(
          StatValues.stack(stack)
        ) { entity.items != null && entity.items.has(stack.item, n) }
      ).padRight(8f)
    }
    table.row()
  }

  override fun efficiency(entity: T): Float {
    if (entity.items == null) return 0f
    for (stack in consItems!!) {
      if (entity.items == null || entity.items.get(stack.item) < stack.amount * multiple(entity)) return 0f
    }
    return 1f
  }

  override fun filter(): Seq<Content>? {
    return Seq.with(*consItems!!).map { s -> s.item }
  }
}