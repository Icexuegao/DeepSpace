package universecore.world.producers

import arc.func.Floatf
import arc.graphics.Color
import arc.scene.ui.layout.Table
import mindustry.world.meta.Stats
import universecore.components.blockcomp.ProducerBuildComp

abstract class BaseProduce<T : ProducerBuildComp> {
    var prodMultiplier: Floatf<T>? = null
    var parent: BaseProducers? = null
    var blockWhenFull: Boolean = true

    /**产出资源类型 */
    abstract fun type(): ProduceType<*>

    open fun color(): Color? {
        return null
    }

    open fun hasIcons(): Boolean {
        return true
    }

    abstract fun buildIcons(table: Table)
    abstract fun merge(other: BaseProduce<T>)
    abstract fun produce(entity: T)
    abstract fun update(entity: T)
    abstract fun display(stats: Stats)
    open fun buildBars(entity: T, bars: Table) {}
    abstract fun valid(entity: T): Boolean

    open fun dump(entity: T) {}

    fun multiple(entity: T): Float {
        return (if (prodMultiplier == null) 1f else prodMultiplier!!.get(entity)) * entity.prodMultiplier()
    }

    fun <N : ProducerBuildComp> setMultiple(multiple: Floatf<N>): BaseProduce<T> {
        prodMultiplier = multiple as Floatf<T>
        return this
    }
}