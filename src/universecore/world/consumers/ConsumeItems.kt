package universecore.world.consumers

import arc.Core
import arc.func.Boolp
import arc.func.Cons
import arc.func.Func
import arc.func.Prov
import arc.math.Mathf
import arc.scene.ui.layout.Table
import arc.struct.ObjectMap
import arc.struct.Seq
import mindustry.ctype.Content
import mindustry.gen.Building
import mindustry.type.Item
import mindustry.type.ItemStack
import mindustry.ui.ReqImage
import mindustry.world.meta.Stat
import mindustry.world.meta.StatValue
import mindustry.world.meta.StatValues
import mindustry.world.meta.Stats
import universecore.components.blockcomp.ConsumerBuildComp
import kotlin.math.floor

//public class ConsumeItems<T extends  & ConsumerBuildComp> extends ConsumeItemBase<T>{
class ConsumeItems<T>(items: Array<out ItemStack>) : ConsumeItemBase<T>() where T : ConsumerBuildComp, T : Building {
  var showPerSecond: Boolean = true

  init {
    this.consItems = items as Array<ItemStack>
  }

  public override fun buildIcons(table: Table) {
    buildItemIcons(table!!, consItems!!, false, displayLim)
  }

  public override fun merge(other: BaseConsume<T>) {
    if (other is ConsumeItems<*>) {
      TMP.clear()
      for (stack in consItems!!) {
        TMP.put(stack.item, stack)
      }

      for (stack in other.consItems!!) {
        TMP.get(stack.item, Prov { ItemStack(stack.item, 0) })!!.amount += stack.amount
      }

      consItems = TMP.values().toSeq().sort(Comparator { a: ItemStack?, b: ItemStack? -> a!!.item.id - b!!.item.id }).toArray<ItemStack>(ItemStack::class.java)
      return
    }
    throw IllegalArgumentException("only merge consume with same type")
  }

  public override fun consume(`object`: T) {
    val f = multiple(`object`)
    for (stack in consItems!!) {
      val amount = stack.amount * (floor(f.toDouble()).toInt()) + Mathf.num(Math.random() < f % 1)
      `object`!!.items.remove(stack.item, amount)
    }
  }

  public override fun update(entity: T) {}

  public override fun display(stats: Stats) {
    stats.add(Stat.input, StatValue { table: Table ->
      table!!.row()
      table.table(Cons { t: Table? ->
        t!!.defaults().left().grow().fill().padLeft(6f)
        t.add(Core.bundle.get("misc.item") + ":")
        for (stack in consItems!!) {
          t.add<Table?>(if (showPerSecond) StatValues.displayItem(stack.item, stack.amount, parent!!.craftTime, true) else StatValues.displayItem(stack.item, stack.amount, true))
        }
      }).left().padLeft(5f)
    })
  }

  public override fun build(entity: T, table: Table) {
    for (stack in consItems!!) {
      var amount = (stack.amount * multiple(entity)).toInt()
      if (amount == 0 && !entity!!.consumer!!.valid()) amount = stack.amount
      val n = amount
      table.add<ReqImage?>(
        ReqImage(
          StatValues.stack(stack), Boolp { entity!!.items != null && entity.items.has(stack.item, n) })
      ).padRight(8f)
    }
    table.row()
  }

  public override fun efficiency(entity: T): Float {
    if (entity!!.items == null) return 0f
    for (stack in consItems!!) {
      if (entity.items == null || entity.items.get(stack.item) < stack.amount * multiple(entity)) return 0f
    }
    return 1f
  }

  public override fun filter(): Seq<Content?>? {
    return Seq.with(*consItems!!).map<Content?>(Func { s: ItemStack? -> s!!.item })
  }

  companion object {
    private val TMP = ObjectMap<Item?, ItemStack?>()
  }
}