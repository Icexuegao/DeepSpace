package universecore.components.blockcomp;

import arc.scene.ui.layout.Table
import universecore.world.blocks.modules.BaseConsumeModule
import universecore.world.consumers.BaseConsume
import universecore.world.consumers.BaseConsumers

/**消耗者组件，令方块具有进行资源消耗与资源检查的能力
 * @author EBwilson
 * @since 1.0
 */
interface ConsumerBuildComp : BuildCompBase {
    /**当前已选中的消耗项索引 */
    //   @BindField("consumeCurrent")
    var consumeCurrent: Int

    /**获取消耗模块 */
    var consumer: BaseConsumeModule

    // @MethodEntry(entryMethod = "update")
    fun updateConsumer() {
        consumer.update()
    }

    fun consMultiplier(): Float {
        return 1f
    }

    /**当前的消耗执行效率，从0-1 */
    fun consEfficiency(): Float {
        return consumer.consEfficiency
    }

    fun optionalConsEff(consumers: BaseConsumers): Float {
        return consumer.getOptionalEff(consumers)
    }

    val consumerBlock: ConsumerBlockComp
        /**获得该块的ConsumerBlock */
        get() = getBlock(ConsumerBlockComp::class.java)
    val consumerBuilding: ConsumerBuildComp
        /**获得该块的NuclearEnergyBlock */
        get() = getBlock(ConsumerBuildComp::class.java)

    /**这个方块当前的消耗列表的消耗条件是否满足 */
    fun consumeValid(): Boolean {
        return consumer == null || !consumer.hasConsume() || consumer.valid()
    }

    /**这个方块当前是否应该对消耗列表执行消耗 */
    fun shouldConsume(): Boolean {
        return consumer != null && consumeCurrent != -1
    }

    /**这个方块当前是否应该对可选消耗列表执行消耗 */
    fun shouldConsumeOptions(): Boolean {
        return shouldConsume() && consumer.hasOptional()
    }

    fun buildConsumerBars(bars: Table) {
        if (consumer.current != null) {
            for (consume in consumer.current?.all()!!) {
                (consume as BaseConsume<ConsumerBuildComp>).buildBars(this, bars)
            }
        }
    }
}
