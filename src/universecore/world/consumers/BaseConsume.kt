package universecore.world.consumers;

import arc.func.Floatf
import arc.scene.ui.layout.Table
import arc.struct.Seq
import mindustry.ctype.Content
import mindustry.world.meta.Stats
import universecore.components.blockcomp.ConsumerBuildComp

abstract class BaseConsume<T> where T:ConsumerBuildComp{
    var parent: BaseConsumers? = null
    var consMultiplier: Floatf<T>? = null

    /**消耗的类型 */
    abstract fun type(): ConsumeType<*>?

    open fun hasIcons(): Boolean {
        return true
    }

    abstract fun buildIcons(table: Table)

    abstract fun merge(other: BaseConsume<T>)

    abstract fun consume(entity: T)
    abstract fun update(entity: T)
    abstract fun display(stats: Stats)
    abstract fun build(entity: T, table: Table)
    open fun buildBars(entity: T, bars: Table) {}
    abstract fun efficiency(entity: T): Float

    abstract fun filter(): Seq<Content?>?

    fun multiple(entity: T): Float {
        return (if (consMultiplier == null) 1f else consMultiplier!!.get(entity)) * entity!!.consMultiplier()
    }

    fun <N : ConsumerBuildComp?> setMultiple(multiple: Floatf<N>): BaseConsume<T> {
        consMultiplier = multiple as Floatf<T>
        return this
    }
}