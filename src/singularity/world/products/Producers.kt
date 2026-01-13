package singularity.world.products

import universecore.world.producers.BaseProducers

class Producers : BaseProducers() {
    fun energy(prod: Float): ProduceEnergy<*> {
        return add(ProduceEnergy(prod))
    }

    fun medium(prod: Float): ProduceMedium<*> {
        return add(ProduceMedium(prod))
    }
}