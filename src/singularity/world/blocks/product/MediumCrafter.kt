package singularity.world.blocks.product

import singularity.world.components.MediumBuildComp
import singularity.world.components.MediumComp
import singularity.world.consumers.SglConsumeMedium
import singularity.world.consumers.SglConsumeType
import singularity.world.products.ProduceMedium
import singularity.world.products.SglProduceType

open class MediumCrafter(name: String) : NormalCrafter(name), MediumComp {
    var mediumCapacity: Float = 16f
    var lossRate: Float = 0.01f
    var mediumMoveRate: Float = 1.325f
    var outputMedium: Boolean = false

    override fun init() {
        super.init()
        var m: ProduceMedium<*>?
        for (producer in producers!!) {
            outputMedium = outputMedium or ((producer.get<ProduceMedium<*>>(SglProduceType.medium).also { m = it }) != null && (m!!.product.also { mediumMoveRate = it }) > 0)
        }
    }

    inner class MediumCrafterBuild : NormalCrafterBuild(), MediumBuildComp {
        override fun acceptMedium(source: MediumBuildComp): Boolean {
            return consumer!!.current != null && consumer!!.current!!.get<SglConsumeMedium<*>>(SglConsumeType.medium) != null && super<MediumBuildComp>.acceptMedium(source)
        }
    }
}