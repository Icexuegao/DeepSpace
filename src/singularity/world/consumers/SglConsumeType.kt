package singularity.world.consumers

import mindustry.ctype.ContentType
import universecore.world.consumers.BaseConsume
import universecore.world.consumers.ConsumeType

class SglConsumeType<T : BaseConsume<*>>(type: Class<T>, cType: ContentType?) : ConsumeType<T>(type, cType) {
    companion object {
        val energy: ConsumeType<SglConsumeEnergy<*>> = add(SglConsumeEnergy::class.java, null) as ConsumeType<SglConsumeEnergy<*>>
        val medium: ConsumeType<SglConsumeMedium<*>> = add(SglConsumeMedium::class.java, null) as ConsumeType<SglConsumeMedium<*>>
        val floor: ConsumeType<SglConsumeFloor<*>> = add(SglConsumeFloor::class.java, null) as ConsumeType<SglConsumeFloor<*>>
    }
}