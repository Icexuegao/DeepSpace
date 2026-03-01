package universecore.world.consumers.cons

import arc.func.Boolf
import arc.func.Cons
import arc.scene.ui.layout.Table
import arc.struct.Seq
import ice.ui.bundle.BaseBundle.Bundle.Companion.localizedName
import ice.world.meta.IStatValues
import ice.world.meta.IceStats
import mindustry.Vars
import mindustry.ctype.Content
import mindustry.gen.Building
import mindustry.type.Item
import mindustry.type.ItemStack
import mindustry.ui.MultiReqImage
import mindustry.ui.ReqImage
import mindustry.world.meta.Stat
import mindustry.world.meta.Stats
import universecore.components.blockcomp.ConsumerBuildComp
import universecore.world.consumers.BaseConsume
import kotlin.math.max
import kotlin.math.min

class ConsumeItemCond<T> : ConsumeItemBase<T>() where T : Building, T : ConsumerBuildComp {
  var minRadioactivity: Float = 0f
  var maxRadioactivity: Float = 0f
  var minFlammability: Float = 0f
  var maxFlammability: Float = 0f
  var minCharge: Float = 0f
  var maxCharge: Float = 0f
  var minExplosiveness: Float = 0f
  var maxExplosiveness: Float = 0f
  var usage: Int = 1
  var filter: Boolf<Item> = Boolf { true }

  fun getCurrCons(entity: T): Item? {
    for (stack in consItems!!) {
      if (entity.items.get(stack.item) >= stack.amount) return stack.item
    }
    return null
  }

  val cons: Array<ItemStack>
    get() {
      if (consItems == null) {
        val seq = Seq<ItemStack?>()
        for (item in Vars.content.items()) {
          if (!filter.get(item)) continue

          if (minRadioactivity != maxRadioactivity) {
            if (item.radioactivity !in minRadioactivity..maxRadioactivity) continue
          }
          if (minFlammability != maxFlammability) {
            if (item.flammability !in minFlammability..maxFlammability) continue
          }
          if (minCharge != maxCharge) {
            if (item.charge !in minCharge..maxCharge) continue
          }
          if (minExplosiveness != maxExplosiveness) {
            if (item.explosiveness !in minExplosiveness..maxExplosiveness) continue
          }

          seq.add(ItemStack(item, usage))
        }
        consItems = seq.toArray(ItemStack::class.java)
      }

      return consItems!!
    }

  override fun buildIcons(table: Table) {
    buildItemIcons(table, this.cons, true, displayLim)
  }

  override fun merge(other: BaseConsume<T>) {
    if (other is ConsumeItemCond<*>) {
      minRadioactivity = min(other.minRadioactivity, minRadioactivity)
      minFlammability = min(other.minFlammability, minFlammability)
      minCharge = min(other.minCharge, minCharge)
      minExplosiveness = min(other.minExplosiveness, minExplosiveness)

      maxRadioactivity = max(other.maxRadioactivity, maxRadioactivity)
      maxFlammability = max(other.maxFlammability, maxFlammability)
      maxCharge = max(other.maxCharge, maxCharge)
      maxExplosiveness = max(other.maxExplosiveness, maxExplosiveness)

      usage += other.usage

      consItems = null
      this.cons
    } else throw IllegalArgumentException("only merge consume with same type")
  }

  override fun consume(entity: T) {
    val cons = this.cons
    if (cons.isEmpty()) return
    val curr = getCurrCons(entity) ?: return
    for (con in cons) {
      if (con.item === curr) {
        entity.items.remove(con.item, con.amount)
      }
    }
  }

  override fun update(entity: T) {}

  override fun display(stats: Stats) {
    stats.add(Stat.input) { table ->
      table.row()
      table.table { t ->
        t.defaults().left().padLeft(6f)
        t.add("${IceStats.物品.localizedName}:")
        for ((count, stack) in this.cons.withIndex()) {
          if (count != 0) t.add("[gray]/[]")
          if (count != 0 && count % 6 == 0) t.row()
          t.add(IStatValues.displayItem(stack.item, stack.amount, false))
        }
      }.left().padLeft(5f)
    }
  }

  override fun build(entity: T, table: Table) {
    val seq: Seq<Item> = Seq<Item>()
    cons.forEach {
      seq.add(it.item)
    }
    val list = seq.select { l: Item -> !l.isHidden && filter.get(l) }
    val image = MultiReqImage()
    list.each(Cons { item ->
      image.add(ReqImage(item.uiIcon) { entity.items != null && entity.items.get(item) > 0 })
    })

    table.add(image).size((8 * 4).toFloat())
  }

  override fun efficiency(entity: T): Float {
    val cons = this.cons
    if (cons.isEmpty()) return 1f
    val curr = getCurrCons(entity) ?: return 0f

    for (con in cons) {
      if (curr === con.item) return 1f
    }
    return 0f
  }

  override fun filter(): Seq<Content>? {
    return Seq.with(*this.cons).map { s: ItemStack -> s.item }
  }
}