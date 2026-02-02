package singularity.world.products

import universecore.world.producers.BaseProduce
import universecore.world.producers.ProduceType

class SglProduceType<T : BaseProduce<*>>(type: Class<T>) : ProduceType<T>(type) {
    companion object {
        val energy: ProduceType<ProduceEnergy<*>> = add(ProduceEnergy::class.java) as ProduceType<ProduceEnergy<*>>
        val medium: ProduceType<ProduceMedium<*>> = add(ProduceMedium::class.java) as ProduceType<ProduceMedium<*>>
    }
}