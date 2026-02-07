package universecore.world.blocks.modules

import arc.math.Mathf
import arc.struct.Seq
import arc.util.io.Reads
import arc.util.io.Writes
import mindustry.world.modules.BlockModule
import universecore.components.blockcomp.ProducerBuildComp
import universecore.world.producers.BaseProduce
import universecore.world.producers.BaseProducers
import universecore.world.producers.ProduceType

/**生产者的产出模块，用于集中处理方块的生产工作
 * @author EBwilson
 */
open class BaseProductModule(val entity: ProducerBuildComp) : BlockModule() {
  var consumer: BaseConsumeModule = entity.consumer
  var current: BaseProducers?
  var valid: Boolean = false

  init {
    current = if (entity.produceCurrent() != -1) entity.producerBlock.producers.get(entity.produceCurrent()) else null
  }

  fun get(): Seq<BaseProducers> {
    return entity.producerBlock.producers
  }

  fun trigger() {
    if (current != null) for (prod in current!!.all()) {
      (prod as BaseProduce<ProducerBuildComp>).produce(entity.getBuilding(ProducerBuildComp::class.java))
    }
  }

  val powerProduct: Float
    get() {
      if (current == null) return 0f
      return current!!.get(ProduceType.power)!!.powerProduction * entity.consumer.powerOtherEff * (Mathf.num(entity.shouldConsume() && entity.consumeValid()) * (current!!.get(ProduceType.power) as BaseProduce<ProducerBuildComp>).multiple(entity))
    }

  fun setCurrent() {
    current = if (entity.consumeCurrent == -1) null else get().get(entity.consumeCurrent)
  }

  open fun update() {
    setCurrent()

    valid = true
    //只在选择了生产列表时才进行产出更新
    if (current != null) {
      setCurrent()
      val doprod = entity.consumeValid() && entity.shouldConsume() && entity.shouldProduct()
      val preValid = valid()
      var anyValid = false
      for (prod in current!!.all()) {
        val v = (prod as BaseProduce<ProducerBuildComp>).valid(entity.getBuilding(ProducerBuildComp::class.java))
        anyValid = anyValid or v
        valid = valid and (!prod.blockWhenFull || v)
        if (doprod && preValid && v) {
          prod.update(entity.getBuilding(ProducerBuildComp::class.java))
        }
      }
      if (!anyValid) valid = false
    }
    //无论何时都向外导出产品
    doDump(entity)
  }

  fun doDump(entity: ProducerBuildComp) {
    if (current != null) {
      for (prod in current!!.all()) {
        (prod as BaseProduce<ProducerBuildComp>).dump(entity.getBuilding(ProducerBuildComp::class.java))
      }
    }
  }

  fun valid(): Boolean {
    return valid && entity.building.enabled
  }

  override fun write(write: Writes) {
    write.bool(valid)
  }

  override fun read(read: Reads) {
    valid = read.bool()
  }
}