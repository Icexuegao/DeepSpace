package universecore.world.consumers

import arc.func.*
import arc.scene.event.Touchable
import arc.struct.ObjectMap
import arc.struct.ObjectSet
import arc.struct.Seq
import arc.util.Time
import mindustry.ctype.Content
import mindustry.gen.Building
import mindustry.type.Item
import mindustry.type.ItemStack
import mindustry.type.Liquid
import mindustry.type.LiquidStack
import mindustry.world.meta.Stat
import mindustry.world.meta.StatUnit
import mindustry.world.meta.Stats
import universecore.world.consumers.cons.SglConsumeEnergy
import universecore.world.consumers.cons.SglConsumeMedium
import universecore.components.blockcomp.ConsumerBuildComp
import universecore.util.Empties
import universecore.world.consumers.cons.ConsumeItems
import universecore.world.consumers.cons.ConsumeLiquids
import universecore.world.consumers.cons.ConsumePower
import kotlin.math.max

/**消耗列表，记录一个消耗的包含生产时间，可选等在内的所有信息
 * @author EBwilson
 */
open class BaseConsumers(
  /**是否为可选消耗项 */
  val optional: Boolean
) {
  companion object {
    protected val tmpCons: Seq<BaseConsume<*>> = Seq<BaseConsume<*>>()
  }

  protected val cons: ObjectMap<ConsumeType<*>, BaseConsume<*>> = ObjectMap<ConsumeType<*>, BaseConsume<*>>()

  /**该值控制生产消耗的时间 */
  var craftTime: Float = 60f

  /**是否在统计栏显示消耗所需时间 */
  var showTime: Boolean = false
  var optionalAlwaysValid: Boolean = true

  /**可选列表可用时将随更新执行的目标函数 */
  var optionalDef: Cons2<ConsumerBuildComp, BaseConsumers> = Cons2 { entity, cons -> }

  /**在统计信息显示自定义内容的函数 */
  var display: Cons2<Stats, BaseConsumers> = Cons2 { stats, cons -> }

  /**是否用自定义的display覆盖默认的条目显示 */
  var customDisplayOnly: Boolean = false
  var selectable: Prov<Visibility?> = Prov { Visibility.usable }
  var consDelta: Floatf<ConsumerBuildComp> = Floatf { e: ConsumerBuildComp ->
    e.building.delta() * e.consEfficiency()
  }

  /**本消耗的可用控制器 */
  var valid: Seq<Boolf<ConsumerBuildComp>> = Seq<Boolf<ConsumerBuildComp>>()

  /**消耗触发器，在消耗的trigger()方法执行时触发 */
  var triggers: Seq<Cons<ConsumerBuildComp>> = Seq<Cons<ConsumerBuildComp>>()
  internal val filter: ObjectMap<ConsumeType<*>?, ObjectSet<Content?>?> = ObjectMap<ConsumeType<*>?, ObjectSet<Content?>?>()
  internal val otherFilter: ObjectMap<ConsumeType<*>?, ObjectSet<Content?>?> = ObjectMap<ConsumeType<*>?, ObjectSet<Content?>?>()
  internal val selfAccess: ObjectMap<ConsumeType<*>?, ObjectSet<Content?>?> = ObjectMap<ConsumeType<*>?, ObjectSet<Content?>?>()

  fun initFilter() {
    filter.clear()
    for (entry in cons) {
      val cont = entry.value!!.filter()
      if (cont != null) filter.get(entry.key) { ObjectSet() }!!.addAll(cont)
    }
    for (entry in otherFilter) {
      filter.get(entry.key) { ObjectSet() }!!.addAll(entry.value)
    }
  }

  fun addToFilter(type: ConsumeType<*>?, content: Content?) {
    otherFilter.get(type) { ObjectSet() }!!.add(content)
  }

  fun addSelfAccess(type: ConsumeType<*>?, content: Content?) {
    selfAccess.get(type) { ObjectSet() }!!.add(content)
  }

  open fun time(time: Float): BaseConsumers? {
    this.craftTime = time
    showTime = time > 0
    return this
  }

  fun overdriveValid(valid: Boolean): BaseConsumers {
    this.consDelta = if (valid) Floatf { e: ConsumerBuildComp? -> e!!.building.delta() * e.consEfficiency() } else Floatf { e: ConsumerBuildComp? -> Time.delta * e!!.consEfficiency() }
    return this
  }

  fun <T : ConsumerBuildComp> setConsDelta(delta: Floatf<T>): BaseConsumers {
    this.consDelta = delta as Floatf<ConsumerBuildComp>
    return this
  }

  fun <T : ConsumerBuildComp?> consValidCondition(cond: Boolf<T>): BaseConsumers {
    this.valid.add(cond as Boolf<ConsumerBuildComp>)
    return this
  }

  fun <T : ConsumerBuildComp> setConsTrigger(cond: Cons<T>): BaseConsumers {
    this.triggers.add(cond as Cons<ConsumerBuildComp>)
    return this
  }

  fun delta(entity: ConsumerBuildComp): Float {
    val get = consDelta.get(entity)
    return if (optional) entity.building.delta() * entity.optionalConsEff(this) * max(1f, entity.consEfficiency()) else get
  }

  fun item(item: Item, amount: Int): ConsumeItems<*> {
    val items = ItemStack(item, amount)
    return items(items)
  }

  open fun items(vararg items: ItemStack): ConsumeItems<*> {
    return add(ConsumeItems(items))
  }

  open fun items(vararg items: Any): ConsumeItems<*> {
    return add(ConsumeItems(ItemStack.with(*items)))
  }

  fun liquid(liquid: Liquid, amount: Float): ConsumeLiquids<*> {
    return liquids(LiquidStack(liquid, amount))
  }

  fun liquids(vararg liquids: LiquidStack): ConsumeLiquids<*> {
    return add(ConsumeLiquids(liquids))
  }

  open fun liquids(vararg items: Any): ConsumeLiquids<*> {
    return add(ConsumeLiquids(LiquidStack.with(*items)))
  }

  fun energy(usage: Float): SglConsumeEnergy<*> {
    return add(SglConsumeEnergy(usage))
  }

  fun medium(cons: Float): SglConsumeMedium<*> {
    return add(SglConsumeMedium(cons))
  }

  fun power(usage: Float): ConsumePower<*> {
    return add(ConsumePower(usage, 0f))
  }

  fun <T> power(usage: Float, capacity: Float): ConsumePower<T>? where T : Building, T : ConsumerBuildComp {
    return add(ConsumePower(usage, capacity))
  }

  fun <T> powerCond(usage: Float, capacity: Float, cons: Boolf<T>): ConsumePower<T> where T : Building, T : ConsumerBuildComp {
    val consume = object : ConsumePower<T>(usage, capacity) {
      override fun requestedPower(entity: T): Float {
        return if (cons.get(entity)) super.requestedPower(entity) else 0f
      }
    }
    return add(consume)
  }

  fun <T> powerDynamic(cons: Floatf<T?>, capacity: Float, statBuilder: Cons<Stats?>): ConsumePower<T>? where T : Building, T : ConsumerBuildComp {
    return add(object : ConsumePower<T>(0f, capacity) {
      override fun requestedPower(entity: T): Float {
        return (cons as Floatf<T>).get(entity)
      }

      override fun display(stats: Stats) {
        statBuilder.get(stats)
      }
    })
  }

  fun <T> powerBuffer(usage: Float, capacity: Float): ConsumePower<T> where T : ConsumerBuildComp, T : Building {
    return add(ConsumePower(usage, capacity))
  }

  @Suppress("UNCHECKED_CAST")
  open fun <T : BaseConsume<out ConsumerBuildComp>> add(consume: T): T {
    val c = cons.get(consume.type())
    if (c == null) {
      cons.put(consume.type(), consume)
      consume.parent = this
      return consume
    } else {
      @Suppress("UNCHECKED_CAST") (c as BaseConsume<ConsumerBuildComp>).merge(consume as BaseConsume<ConsumerBuildComp>)
      return c as T
    }
  }

  fun <T : BaseConsume<out ConsumerBuildComp>> get(type: ConsumeType<T>): T? {
    return cons.get(type) as T?
  }

  fun first(): BaseConsume<*>? {
    for (con in cons) {
      if (con.value != null) return con.value
    }
    return null
  }

  fun all(): Iterable<BaseConsume<out ConsumerBuildComp>> {
    tmpCons.clear()

    for (type in ConsumeType.all()) {
      val c = cons.get(type)
      if (c != null) tmpCons.add(c)
    }

    return tmpCons
  }

  fun remove(type: ConsumeType<*>?) {
    cons.remove(type)
  }

  fun display(stats: Stats) {
    if (cons.size > 0 && !customDisplayOnly) {
      if (showTime) stats.add(Stat.productionTime, craftTime / 60f, StatUnit.seconds)
      for (c in all()) {
        c.display(stats)
      }
    }

    display.get(stats, this)
  }

  fun filter(type: ConsumeType<*>?, content: Content?): Boolean {
    return filter.get(type, Empties.nilSetO<Content?>())!!.contains(content)
  }

  fun selfAccess(type: ConsumeType<*>?, content: Content?): Boolean {
    return selfAccess.get(type, Empties.nilSetO<Content?>())!!.contains(content)
  }

  enum class Visibility(touchable: Touchable) {
    usable(Touchable.enabled),
    unusable(Touchable.disabled),
    hidden(Touchable.disabled);

    val buttonValid: Touchable = touchable
  }
}