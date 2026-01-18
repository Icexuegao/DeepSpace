package universecore.world.producers

import arc.Core
import arc.graphics.Color
import arc.math.Mathf
import arc.scene.ui.layout.Table
import arc.struct.ObjectMap
import mindustry.gen.Building
import mindustry.type.Item
import mindustry.type.ItemStack
import mindustry.world.meta.Stat
import mindustry.world.meta.StatValues
import mindustry.world.meta.Stats
import universecore.components.blockcomp.ProducerBuildComp
import universecore.world.consumers.ConsumeItemBase.Companion.buildItemIcons
import kotlin.math.floor
import kotlin.math.min

class ProduceItems<T>(var items: Array<out ItemStack>) : BaseProduce<T>() where T : Building, T : ProducerBuildComp {
  var showPerSecond: Boolean = true
  var displayLim: Int = 4

  /*控制是否随机产出产物(也就是是否为分离机)*/
  var random: Boolean = false

  fun random(): ProduceItems<T> {
    this.random = true
    return this
  }

  override fun type(): ProduceType<ProduceItems<*>> {
    return ProduceType.item
  }

  override fun color(): Color? {
    return items[0].item.color
  }

  override fun buildIcons(table: Table) {
    if (random) {
      val i = arrayOfNulls<ItemStack>(items.size)
      for (l in i.indices) {
        i[l] = items[l].copy()
        i[l]!!.amount = 0
      }
      val items1 = i as Array<ItemStack>
      buildItemIcons(table, items1, true, displayLim)
    } else buildItemIcons(table, items as Array<ItemStack>, false, displayLim)
  }

  override fun merge(other: BaseProduce<T>) {
    if (other is ProduceItems<*>) {
      TMP.clear()
      for (stack in items) {
        TMP.put(stack.item, stack)
      }

      for (stack in other.items) {
        TMP.get(stack.item) { ItemStack(stack.item, 0) }!!.amount += stack.amount
      }

      items = TMP.values().toSeq().sort(Comparator { a: ItemStack?, b: ItemStack? -> a!!.item.id - b!!.item.id }).toArray(ItemStack::class.java)
      return
    }
    throw IllegalArgumentException("only merge production with same type")
  }

  override fun produce(entity: T) {
    val f = multiple(entity)
    if (!random) {
      for (stack in items) {
        var amount = stack.amount * (floor(f.toDouble()).toInt()) + Mathf.num(Math.random() < f % 1)
        amount = min(amount, entity!!.block.itemCapacity - entity.items.get(stack.item))
        for (i in 0..<amount) {
          entity.handleItem(entity, stack.item)
        }
      }
    } else {
      var sum = 0
      for (stack in items) {
        sum += stack.amount
      }
      val i = Mathf.random(sum)
      var count = 0
      var item: Item? = null

      for (stack in items) {
        if (i >= count && i < count + stack.amount) {
          item = stack.item
          break
        }
        count += stack.amount
      }
      if (item != null) {
        var amount = (floor(f.toDouble()) + Mathf.num(Math.random() < f % 1)).toInt()
        amount = min(amount, entity.block.itemCapacity - entity.items.get(item))
        for (l in 0..<amount) {
          entity.handleItem(entity, item)
        }
      }
    }
  }

  override fun update(entity: T) {
  }

  override fun dump(entity: T) {
    for (stack in items) {
      if (entity.items.get(stack.item) > 0) entity.dump(stack.item)
    }
  }

  override fun display(stats: Stats) {
    stats.add(Stat.output) { table ->
      table.row()
      table.table { t ->
        t.defaults().left().fill().padLeft(6f)
        t.add(Core.bundle.get("misc.item") + ":").left()
        if (!random) {

          for (stack in items) {
            t.add(if (showPerSecond) StatValues.displayItem(stack.item, stack.amount, parent!!.cons!!.craftTime, true) else StatValues.displayItem(stack.item, stack.amount, true))
          }
        } else {
          val total = intArrayOf(0)
          val n = intArrayOf(items.size, items.size)
          t.table { item: Table? ->
            for (stack in items) {
              item!!.add(StatValues.displayItem(stack.item, 0, true))
              total[0] += stack.amount
              if (--n[0] > 0) item.add("/")
            }
            item!!.row()
            for (stack in items) {
              item.add("[gray]" + ((stack.amount.toFloat()) / (total[0].toFloat()) * 100).toInt() + "%")
              if (--n[1] > 0) item.add()
            }
          }
        }
      }.left().padLeft(5f)
    }
  }

  override fun valid(entity: T): Boolean {
    if (entity!!.items == null) return false
    var res = false
    for (stack in items) {
      if (entity.items.get(stack.item) + stack.amount * multiple(entity) > entity.block.itemCapacity) {
        if (blockWhenFull) return false
      } else res = true
    }
    return res
  }

  companion object {
    private val TMP = ObjectMap<Item?, ItemStack?>()
  }
}