package universecore.world.producers

import arc.struct.Seq

open class ProduceType<T : BaseProduce<*>>(val type: Class<T>) {
    private val id: Int = allType.size

    fun id(): Int {
        return id
    }

    init {
        allType.add(this)
    }

    companion object {
        private val allType = Seq<ProduceType<*>?>()

        fun all(): Array<ProduceType<*>?>? {
            return allType.toArray<ProduceType<*>?>(ProduceType::class.java)
        }

        fun <Type : BaseProduce<*>> add(type: Class<Type>): ProduceType<out Type> {
            return ProduceType<Type>(type)
        }

        val power: ProduceType<ProducePower<*>> = Companion.add(ProducePower::class.java) as ProduceType<ProducePower<*>>
        val item: ProduceType<ProduceItems<*>> = Companion.add(ProduceItems::class.java) as ProduceType<ProduceItems<*>>
        val liquid: ProduceType<ProduceLiquids<*>> = Companion.add(ProduceLiquids::class.java) as ProduceType<ProduceLiquids<*>>
        val payload: ProduceType<ProducePayload<*>> = Companion.add(ProducePayload::class.java) as ProduceType<ProducePayload<*>>
    }
}