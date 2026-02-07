package singularity.world.blocks.product

import arc.func.Prov
import singularity.world.components.MediumBuildComp
import singularity.world.components.MediumComp
import singularity.world.consumers.SglConsumeType
import singularity.world.products.ProduceMedium
import universecore.world.producers.ProduceType

open class MediumCrafter(name: String) : NormalCrafter(name), MediumComp {
  override var mediumCapacity: Float = 16f
  override var lossRate: Float = 0.01f
  override var mediumMoveRate: Float = 1.325f
  override var outputMedium: Boolean = false

  init {

    buildType = Prov { MediumCrafterBuild() }
  }

  override fun init() {
    super.init()
    var m: ProduceMedium<*>?
    for (producer in producers) {
      outputMedium = outputMedium or ((producer.get(ProduceType.medium).also { m = it }) != null && (m!!.product.also { mediumMoveRate = it }) > 0)
    }
  }

  inner class MediumCrafterBuild : NormalCrafterBuild(), MediumBuildComp {
    override var mediumContains: Float= 0f

    override fun acceptMedium(source: MediumBuildComp): Boolean {
      return consumer.current != null && consumer.current!!.get(SglConsumeType.medium) != null && super.acceptMedium(source)
    }
  }
}