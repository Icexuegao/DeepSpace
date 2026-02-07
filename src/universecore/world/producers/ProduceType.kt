package universecore.world.producers

import arc.struct.Seq
import singularity.world.products.ProduceEnergy
import singularity.world.products.ProduceMedium

open class ProduceType<T : BaseProduce<*>>(val type: Class<T>) {
  companion object {
    private val allType = Seq<ProduceType<*>>()

    fun all(): Array<ProduceType<*>?>? {
      return allType.toArray<ProduceType<*>?>(ProduceType::class.java)
    }

    fun <Type : BaseProduce<*>> add(type: Class<Type>): ProduceType<out Type> {
      return ProduceType(type)
    }

    val power = add(ProducePower::class.java) as ProduceType<ProducePower<*>>
    val item = add(ProduceItems::class.java) as ProduceType<ProduceItems<*>>
    val liquid = add(ProduceLiquids::class.java) as ProduceType<ProduceLiquids<*>>
    val payload = add(ProducePayload::class.java) as ProduceType<ProducePayload<*>>
    val energy = add(ProduceEnergy::class.java) as ProduceType<ProduceEnergy<*>>
    val medium = add(ProduceMedium::class.java) as ProduceType<ProduceMedium<*>>

    val add: ProduceType<out ProduceItems<*>> = add(ProduceItems::class.java)
    val mediusm = add
  }

  val id: Int = allType.size

  init {
    allType.add(this)
  }
}