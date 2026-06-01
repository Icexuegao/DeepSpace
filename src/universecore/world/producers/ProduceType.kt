package universecore.world.producers

import arc.struct.Seq
import singularity.world.products.ProduceEnergy
import singularity.world.products.ProduceMedium

open class ProduceType<T :BaseProduce<*>>(val type: Class<T>) {
  companion object {
    private val allType = Seq<ProduceType<*>>()

    val power = add(ProducePower::class.java)
    val item = add(ProduceItems::class.java)
    val liquid = add(ProduceLiquids::class.java)
    val payload = add(ProducePayload::class.java)
    val energy = add(ProduceEnergy::class.java)
    val medium = add(ProduceMedium::class.java)

    fun all(): Array<ProduceType<*>> = allType.toArray(ProduceType::class.java)

    fun <Type :BaseProduce<*>> add(type: Class<Type>) = ProduceType(type)

  }

  val id: Int = allType.size

  init {
    allType.add(this)
  }
}