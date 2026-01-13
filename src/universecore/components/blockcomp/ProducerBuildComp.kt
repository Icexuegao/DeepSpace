package universecore.components.blockcomp

import arc.math.Mathf
import arc.scene.ui.layout.Table
import universecore.world.blocks.modules.BaseProductModule
import universecore.world.producers.BaseProduce
import universecore.world.producers.ProducePower
import universecore.world.producers.ProduceType

/**生产者组件，令方块具有按需进行资源生产输出的能力
 *
 * @author EBwilson
 * @since 1.0
 */
interface ProducerBuildComp : BuildCompBase, ConsumerBuildComp {
    /**当前选择的生产项目的索引 */
    fun produceCurrent(): Int {
        return consumeCurrent
    }

    var powerProdEfficiency: Float

    fun prodMultiplier(): Float {
        return consMultiplier()
    }

    /**生产组件 */
    var producer: BaseProductModule?

    // @MethodEntry(entryMethod = "update")
    fun updateProducer() {
        producer!!.update()
    }

    val producerBlock: ProducerBlockComp
        /**获得该块的NuclearEnergyBlock */
        get() = getBlock(ProducerBlockComp::class.java)

    /**当前生产是否可用 */
    fun productValid(): Boolean {
        return producer == null || producer!!.valid()
    }

    /**当前是否应当执行生产项更新 */
    fun shouldProduct(): Boolean {
        return producer != null && produceCurrent() != -1
    }

    fun buildProducerBars(bars: Table) {
        if (consumer.current != null) {
            for (consume in producer!!.current!!.all()) {
                (consume as BaseProduce<ProducerBuildComp>).buildBars(this, bars)
            }
        }
    }

    // @get:MethodEntry(entryMethod = "getPowerProduction", override = true)
    fun powerProduction(): Float {
        if (!block!!.outputsPower || producer!!.current == null || producer!!.current!!.get(ProduceType.power) == null) return 0f
        powerProdEfficiency = Mathf.num(shouldConsume() && consumeValid()) * consEfficiency() * ((producer!!.current!!.get(ProduceType.power)) as ProducePower<ProducerBuildComp>).multiple(this)
        return producer!!.powerProduct * powerProdEfficiency
    }
}