package singularity.world.consumers

import universecore.world.consumers.BaseConsume
import universecore.world.consumers.BaseConsumers

open class SglConsumers(optional: Boolean) : BaseConsumers(optional) {
    fun energy(usage: Float): SglConsumeEnergy<*> {
        return add(SglConsumeEnergy(usage))
    }

    fun medium(cons: Float): SglConsumeMedium<*> {
        return add(SglConsumeMedium(cons))
    }

    fun first(): BaseConsume<*>? {
        for (con in cons) {
            if (con.value != null) return con.value
        }
        return null
    }
}