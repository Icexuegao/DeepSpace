package universecore.components.blockcomp

import arc.math.Mathf
import arc.util.io.Reads
import arc.util.io.Writes
import mindustry.gen.Building
import mindustry.type.Item

/**工厂建筑的接口组件，该组件赋予方块执行生产/制造的行为，工厂行为整合了[ConsumerBuildComp]和[ProducerBlockComp]的行为并描述了制造行为的默认实现
 *
 * @since 1.4
 * @author EBwilson
 */
interface FactoryBuildComp : ProducerBuildComp {
    /**`getter-`生产进度 */
    var progress: Float

    /**`getter-`整体总的生产进度 */
    var totalProgress: Float

    /**`getter-`工作预热的插值 */
    var warmup: Float

    override fun consEfficiency(): Float {
        return super.consEfficiency() * warmup
    }

    //  @MethodEntry(entryMethod = "update")
    fun updateFactory() {
        /*当未选择配方时不进行更新*/
        if (produceCurrent() == -1 || producer!!.current == null) {
            warmup = (Mathf.lerpDelta(warmup, 0f, this.factoryBlock.stopSpeed))
            return
        }

        if (shouldConsume() && consumeValid()) {
            progress = (progress + progressIncrease(consumer.current!!.craftTime))
            warmup = (Mathf.lerpDelta(warmup, 1f, this.factoryBlock.warmupSpeed))
            onCraftingUpdate()
        } else {
            warmup = (Mathf.lerpDelta(warmup, 0f, this.factoryBlock.stopSpeed))
        }

        totalProgress = (totalProgress + consumer!!.consDelta())

        while (progress >= 1) {
            progress -= 1
            consumer!!.trigger()
            producer!!.trigger()

            craftTrigger()
        }
    }

    /**对生产进行处理，当产出者是Building时需要对产出项进行正确的统计，则当工厂handleItem并传入本身时，将此次添加的物品加入统计 */
    //  @MethodEntry(entryMethod = "handleItem", paramTypes = ["mindustry.gen.Building -> source", "mindustry.type.Item -> item"])
    fun handleProductItem(source: Building?, item: Item?) {
        if (source === this) {
            source.produced(item)
        }
    }

    //  @MethodEntry(entryMethod = "read", paramTypes = ["arc.util.io.Reads -> read", "byte"])
    fun readFactory(read: Reads) {
        progress = (read.f())
        totalProgress = (read.f())
        warmup = (read.f())
    }

    // @MethodEntry(entryMethod = "write", paramTypes = "arc.util.io.Writes -> write")
    fun writeFactory(write: Writes) {
        write.f(progress)
        write.f(totalProgress)
        write.f(warmup)
    }

    /**当前机器的工作效率，0-1 */
    fun workEfficiency(): Float {
        return consEfficiency()
    }

    /**机器工作的销量增量，标准情况下是生产时间的倒数，乘以额外的增量返回
     *
     * @param baseTime 当前执行的消耗的基准耗时
     */
    fun progressIncrease(baseTime: Float): Float {

        return 1 / baseTime * consumer.consDelta()
    }

    val factoryBlock: FactoryBlockComp
        get() = getBlock(FactoryBlockComp::class.java)

    override fun shouldConsume(): Boolean {
        return super.shouldConsume() && productValid()
    }

    /**机器工作中一次生产执行时调用 */
    fun craftTrigger()

    /**机器工作中随每一次刷新调用 */
    fun onCraftingUpdate()
}