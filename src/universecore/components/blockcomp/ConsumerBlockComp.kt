package universecore.components.blockcomp;

import arc.func.Cons2
import arc.func.Floatf
import arc.math.Mathf
import arc.struct.Seq
import mindustry.gen.Building
import mindustry.world.Block
import mindustry.world.meta.Stats
import universecore.world.consumers.BaseConsumers
import universecore.world.consumers.ConsFilter
import universecore.world.consumers.ConsumeType

/**Consume组件，为方块添加可标记消耗项的功能
 *
 * @author EBwilson
 * @since 1.0
 */
interface ConsumerBlockComp {
    /**方块的消耗的清单列表 */ //initialize = "new arc.struct.Seq<>()")
    var consumers:Seq<BaseConsumers>

    /**方块的可选消耗的清单列表 */
    var optionalCons:Seq<BaseConsumers>
    //initialize = "new arc.struct.Seq<>()")

    /**这个方块是否只在一次消耗可选列表时仅选中一个最靠前的可选项 */
    var oneOfOptionCons: Boolean

   // @BindField(value = "consFilter", initialize = "new universecore.world.consumers.ConsFilter()")
    var consFilter: ConsFilter

    /**创建一个新的消耗列表插入容器，并返回它 */
    fun newConsume(): BaseConsumers? {
        val consume = BaseConsumers(false)
        consumers.add(consume)
        return consume
    }

    /**创建一个可选消耗列表插入容器，并返回它
     *
     * @param validDef 当这个消耗项可用时每次刷新要进行的行为
     * @param displayDef 用于设置统计条目，显示该可选消耗的功能
     */
    fun <T : ConsumerBuildComp> newOptionalConsume(validDef: Cons2<T, BaseConsumers>, displayDef: Cons2<Stats, BaseConsumers>): BaseConsumers? {
        val consume: BaseConsumers = object : BaseConsumers(true) {
            init {
                optionalDef = validDef as Cons2<ConsumerBuildComp, BaseConsumers>
                display = displayDef
            }
        }
        optionalCons.add(consume)
        return consume
    }

    /**为将方块加入到能量网络中，需要初始化一个原有的能量消耗器进行代理，这在组件化实现中由[Block.init]入口调用 */
   // @MethodEntry(entryMethod = "init")
    fun initPower() {
        val block = this as Block

        if (block.consumesPower) {
            block.consumePowerDynamic(Floatf { e: Building? ->
                val entity = e as ConsumerBuildComp
                if (entity.consumer!!.current == null) return@Floatf 0f
                if (entity.building.tile.build == null || entity.consumeCurrent == -1 || !entity.consumer!!.excludeValid(ConsumeType.power)) return@Floatf 0f
                val cons = entity.consumer!!.current!!.get(ConsumeType.power)
                if (cons == null) return@Floatf 0f
                if (cons.buffered) {
                    return@Floatf (1f - entity.building.power.status) * cons.capacity
                } else {
                    return@Floatf entity.consumer!!.powerUsage * Mathf.num(entity.shouldConsume())
                }
            })
        }
    }

 //   @MethodEntry(entryMethod = "init")
    fun initFilter() {
     consFilter.applyFilter(consumers, optionalCons)
        for (consumer in consumers) {
            consumer.initFilter()
        }
    }
}
