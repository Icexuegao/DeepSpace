package universecore.world.producers

import arc.graphics.Color
import arc.struct.ObjectMap
import arc.struct.Seq
import mindustry.type.Item
import mindustry.type.ItemStack
import mindustry.type.Liquid
import mindustry.type.LiquidStack
import mindustry.world.meta.Stats
import universecore.components.blockcomp.ProducerBuildComp
import universecore.world.consumers.BaseConsumers

/**产出列表，绑定一个消耗列表，在执行消耗的同时对应执行此生产列表，以实现工厂生产
 * @author EBwilson
 */
open class BaseProducers {
  protected val prod: ObjectMap<ProduceType<*>, BaseProduce<*>> = ObjectMap<ProduceType<*>, BaseProduce<*>>()

  /**此生产项的颜色，这通常被用于确定绘制top等目的时快速选择颜色 */
  var color: Color? = TRANS

  /**后初始化的变量，不要手动更改，该变量绑定到与此生产匹配的消耗项 */
  var cons: BaseConsumers? = null

  fun setColor(color: Color?): BaseProducers {
    this.color = color
    return this
  }

  fun item(item: Item?, amount: Int): ProduceItems<*> {
    return items(ItemStack(item, amount))
  }

  fun items(vararg items: ItemStack): ProduceItems<*> {
    return add(ProduceItems(items))
  }
  fun items(vararg items: Any): ProduceItems<*> {
    return add(ProduceItems(ItemStack.with(*items)))
  }

  fun liquid(liquid: Liquid?, amount: Float): ProduceLiquids<*> {
    return liquids(LiquidStack(liquid, amount))
  }

  fun liquids(vararg liquids: LiquidStack): ProduceLiquids<*> {
    return add(ProduceLiquids(liquids))
  }

  fun power(prod: Float): ProducePower<*> {
    return add(ProducePower(prod))
  }

  @Suppress("UNCHECKED_CAST")
  open fun <T : BaseProduce<out ProducerBuildComp>> add(produce: T): T {
    val p = prod.get(produce.type())
    if (p == null) {
      prod.put(produce.type(), produce)
      produce.parent = this
      if (color === TRANS && produce.color() != null) {
        color = produce.color()
      }
      return produce
    } else {
      (p as BaseProduce<ProducerBuildComp>).merge(produce as BaseProduce<ProducerBuildComp>)
    }
    return p as T
  }

  @Suppress("UNCHECKED_CAST")
  fun <T : BaseProduce<out ProducerBuildComp>> get(type: ProduceType<T>): T? {
    return prod.get(type) as T?
  }

  fun all(): Iterable<BaseProduce<*>> {
    tmpProd.clear()

    for (type in ProduceType.all()!!) {
      val p = prod.get(type)
      if (p != null) tmpProd.add(p)
    }

    return tmpProd
  }

  fun remove(type: ProduceType<*>?) {
    prod.remove(type)
  }

  fun display(stats: Stats) {
    for (p in prod.values().toSeq().sort(Comparator { a: BaseProduce<*>, b: BaseProduce<*> -> a.type().id - b.type().id })) {
      p.display(stats)
    }
  }

  companion object {
    val TRANS: Color = Color(0f, 0f, 0f, 0f)
    protected val tmpProd: Seq<BaseProduce<*>> = Seq<BaseProduce<*>>()
  }
}