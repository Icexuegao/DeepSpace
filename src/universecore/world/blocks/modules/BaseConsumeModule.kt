package universecore.world.blocks.modules

import arc.scene.ui.layout.Table
import arc.struct.ObjectMap
import arc.struct.Seq
import arc.util.io.Reads
import arc.util.io.Writes
import ice.library.struct.isNotEmpty
import mindustry.world.meta.BlockStatus
import mindustry.world.modules.BlockModule
import singularity.world.blocks.SglBlock
import universecore.components.blockcomp.ConsumerBuildComp
import universecore.world.consumers.BaseConsume
import universecore.world.consumers.BaseConsumers
import universecore.world.consumers.ConsumePower
import universecore.world.consumers.ConsumeType

/**生产者的消耗器模块，用于集中处理方块的材料需求等，提供了可选需求以及其特殊的触发器
 * @author EBwilson
 */
@Suppress("UNCHECKED_CAST")
open class BaseConsumeModule(val entity: ConsumerBuildComp) : BlockModule() {
  protected val optProgress: ObjectMap<BaseConsumers, FloatArray> = ObjectMap<BaseConsumers, FloatArray>()
  protected val optEfficiency: ObjectMap<BaseConsumers, FloatArray> = ObjectMap<BaseConsumers, FloatArray>()
  var current: BaseConsumers?
  var optionalCurr: BaseConsumers? = null
  var valid: Boolean = false
  var consEfficiency: Float = 0f
  var powerOtherEff: Float = 0f
  private var powerCons = 0f

  init {
    val bool = entity.consumerBlock.consumers.isNotEmpty() && entity.consumeCurrent != -1
    current = if (bool) entity.consumerBlock!!.consumers.get(entity.consumeCurrent) else null
  }

  fun build(table: Table) {
    if (current != null) for (cons in current!!.all()) {
      (cons as BaseConsume<ConsumerBuildComp>).build(entity.getBuilding(ConsumerBuildComp::class.java), table)
    }
  }

  fun get(): Seq<BaseConsumers> {
    return entity.consumerBlock!!.consumers
  }

  val optional: Seq<BaseConsumers>
    get() = entity.consumerBlock!!.optionalCons

  fun status(): BlockStatus {
    if (!entity.shouldConsume()) {
      return BlockStatus.noOutput
    }

    if (!valid) {
      return BlockStatus.noInput
    }

    return BlockStatus.active
  }

  fun hasConsume(): Boolean {
    return !get().isEmpty
  }

  fun hasOptional(): Boolean {
    return !this.optional.isEmpty
  }

  fun getOptionalEff(consumers: BaseConsumers): Float {
    return optEfficiency.get(consumers, ZERO)!![0]
  }

  val powerUsage: Float
    get() = powerCons * (current!!.get(ConsumeType.power) as BaseConsume<ConsumerBuildComp>).multiple(entity) * powerOtherEff

  fun setCurrent() {
    current = if (entity.consumeCurrent == -1) null else get().get(entity.consumeCurrent)
  }

  open fun update() {
    setCurrent()
    powerCons = 0f
    if ((!hasOptional() && !hasConsume())) return

    valid = true
    //只在选中消耗列表时才进行消耗更新
    if (current != null) {
      val preValid = valid()

      for (b in current!!.valid) {
        valid = valid and b!!.get(entity)
      }
      powerOtherEff = (if (valid) 1 else 0).toFloat()
      consEfficiency = powerOtherEff

      for (cons in current!!.all()) {
        val eff = (cons as BaseConsume<ConsumerBuildComp>).efficiency(entity.getBuilding(ConsumerBuildComp::class.java))
        if (cons is ConsumePower<*>) {
          val power = cons as ConsumePower<ConsumerBuildComp>
          powerCons += power.requestedPower(entity.getBuild<SglBlock.SglBuilding>())
        } else powerOtherEff *= eff

        consEfficiency *= eff
        valid = valid and (eff > 0.0001f)

        if (!valid) {
          consEfficiency = 0f
          break
        }

        if (preValid && entity.shouldConsume()) {
          cons.update(entity.getBuilding(ConsumerBuildComp::class.java))
        }
      }
    }

    updateOptional()
  }

  fun updateOptional() {
    if (optional.isNotEmpty()) {
      var cons: BaseConsumers
      val onlyOne = entity.consumerBlock!!.oneOfOptionCons
      for (id in 0..<optional.size) {
        cons = optional.get(id)

        var optionalEff = 1f
        for (b in cons.valid) {
          optionalEff *= if (b.get(entity)) 1f else 0f
        }
        for (c in cons.all()) {
          optionalEff *= (c as BaseConsume<ConsumerBuildComp>).efficiency(entity.getBuilding(ConsumerBuildComp::class.java))
        }
        optEfficiency.get(cons) { FloatArray(1) }[0] = optionalEff

        if (optionalEff > 0.0001f) {
          optionalCurr = cons

          if (!entity.shouldConsumeOptions() || (!cons.optionalAlwaysValid && !valid)) continue
          for (c in cons.all()) {
            (c as BaseConsume<ConsumerBuildComp>).update(entity.getBuilding(ConsumerBuildComp::class.java))
            if (c is ConsumePower<*>){

              val power = c as ConsumePower<ConsumerBuildComp>
              val entity1: ConsumerBuildComp = entity.getBuild()
              powerCons += power.requestedPower(entity1)
            }
          }

          if (cons.craftTime > 0) {
            val arr = optProgress.get(cons) { floatArrayOf(0f) }
            arr[0] += 1 / cons.craftTime * cons.delta(entity)
            while (arr[0] >= 1) {
              arr[0] %= 1f
              triggerOpt(id)
            }
          }

          cons.optionalDef.get(entity, cons)
          if (onlyOne) break
        }
      }
    }
  }

  fun consDelta(): Float {
    return if (current == null) 0f else current!!.delta(entity)
  }

  /**获取指定索引的消耗列表 */
  fun get(index: Int): BaseConsumers? {
    return get().get(index)
  }

  /**获取指定索引处的可选消耗列表 */
  fun getOptional(index: Int): BaseConsumers? {
    return if (index < this.optional.size) this.optional.get(index) else null
  }

  /**触发当前主要消耗项的trigger方法 */
  fun trigger() {
    if (current != null) {
      for (cons in current!!.all()) {
        (cons as BaseConsume<ConsumerBuildComp>).consume(entity.getBuilding(ConsumerBuildComp::class.java))
      }
      for (trigger in current!!.triggers) {
        trigger!!.get(entity)
      }
    }
  }

  /**触发一个可选消耗项的trigger方法 */
  fun triggerOpt(id: Int) {
    if (this.optional != null && this.optional.size > id) {
      val cons = this.optional.get(id)
      for (c in cons.all()) {
        (c as BaseConsume<ConsumerBuildComp>).consume(entity.getBuilding(ConsumerBuildComp::class.java))
      }
      for (trigger in cons.triggers) {
        trigger!!.get(entity)
      }
    }
  }

  /**当前消耗列表除指定消耗项以外是否其他全部可用 */
  fun excludeValid(type: ConsumeType<*>?): Boolean {
    var temp = true
    for (cons in current!!.all()) {
      if (cons.type() === type) continue
      temp = temp and ((cons as BaseConsume<ConsumerBuildComp>).efficiency(entity.getBuilding(ConsumerBuildComp::class.java)) > 0.0001f)
    }
    return temp
  }

  /**当前消耗列表是否可用 */
  fun valid(): Boolean {
    return valid && entity.building.enabled
  }

  /**当前消耗列表指定消耗项是否可用 */
  fun valid(type: ConsumeType<*>): Boolean {
    val get = current!!.get(type)

    return get != null && (current!!.get(type)!! as BaseConsume<ConsumerBuildComp>).efficiency(entity.getBuilding(ConsumerBuildComp::class.java)) > 0.0001
  }

  /**指定的消耗列表是否可用 */
  fun valid(index: Int): Boolean {
    if (index >= get().size) return false

    for (c in get().get(index)!!.all()) {
      if ((c as BaseConsume<ConsumerBuildComp>).efficiency(entity.getBuilding(ConsumerBuildComp::class.java)) < 0.0001f) return false
    }

    return true
  }

  override fun write(write: Writes) {
    write.bool(valid)
  }

  override fun read(read: Reads) {
    valid = read.bool()
  }

  companion object {
    private val ZERO = floatArrayOf(0f)
  }
}