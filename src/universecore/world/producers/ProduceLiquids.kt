package universecore.world.producers

import arc.Core
import arc.func.Cons
import arc.func.Prov
import arc.graphics.Color
import arc.scene.ui.layout.Table
import arc.struct.ObjectMap
import mindustry.gen.Building
import mindustry.type.Liquid
import mindustry.type.LiquidStack
import mindustry.ui.Bar
import mindustry.world.meta.Stat
import mindustry.world.meta.StatValue
import mindustry.world.meta.StatValues
import mindustry.world.meta.Stats
import universecore.components.blockcomp.ProducerBuildComp
import universecore.world.consumers.ConsumeLiquidBase
import kotlin.math.min

class ProduceLiquids<T>(var liquids: Array<out LiquidStack>) : BaseProduce<T>() where T : Building, T : ProducerBuildComp {
    var displayLim: Int = 4
    var portion: Boolean = false

    fun portion(): ProduceLiquids<T> {
        this.portion = true
        return this
    }

    public override fun buildBars(entity: T, bars: Table) {
        for (stack in liquids) {
            bars.add(
                Bar(
                    { stack.liquid.localizedName },
                    { if (stack.liquid.barColor != null) stack.liquid.barColor else stack.liquid.color },
                    { min(entity!!.liquids.get(stack.liquid) / entity.block.liquidCapacity, 1f) }
                ))
            bars.row()
        }
    }

    public override fun type(): ProduceType<ProduceLiquids<*>> {
        return ProduceType.liquid
    }

    public override fun color(): Color? {
        return liquids[0].liquid.color
    }

    public override fun buildIcons(table: Table) {
        ConsumeLiquidBase.buildLiquidIcons(table, liquids, false, displayLim)
    }

    public override fun merge(other: BaseProduce<T>) {
        if (other is ProduceLiquids<*>) {
            TMP.clear()
            for (stack in liquids) {
                TMP.put(stack.liquid, stack)
            }

            for (stack in other.liquids) {
                TMP.get(stack.liquid, Prov { LiquidStack(stack.liquid, 0f) })!!.amount += stack.amount
            }

            liquids = TMP.values().toSeq().sort(Comparator { a: LiquidStack?, b: LiquidStack? -> a!!.liquid.id - b!!.liquid.id }).toArray<LiquidStack?>(LiquidStack::class.java)
            return
        }
        throw IllegalArgumentException("only merge production with same type")
    }

    public override fun produce(entity: T) {
        if (portion) for (stack in liquids) {
            entity!!.handleLiquid(entity, stack.liquid, stack.amount * 60)
        }
    }

    public override fun update(entity: T) {
        if (!portion) for (stack in liquids) {
            var amount = stack.amount * parent!!.cons!!.delta(entity!!) * multiple(entity)
            amount = min(amount, entity.block.liquidCapacity - entity.liquids.get(stack.liquid))
            entity.handleLiquid(entity, stack.liquid, amount)
        }
    }

    public override fun dump(entity: T) {
        for (stack in liquids) {
            if (entity!!.liquids.get(stack.liquid) > 0.01f) entity.dumpLiquid(stack.liquid)
        }
    }

    public override fun display(stats: Stats) {
        stats.add(Stat.output, StatValue { table: Table? ->
            table!!.row()
            table.table(Cons { t: Table? ->
                t!!.defaults().left().fill().padLeft(6f)
                t.add(Core.bundle.get("misc.liquid") + ":").left()
                for (stack in liquids) {
                    t.add<Table?>(StatValues.displayLiquid(stack.liquid, stack.amount * 60, true))
                }
            }).left().padLeft(5f)
        })
    }

    public override fun valid(entity: T): Boolean {
        if (entity!!.liquids == null) return false
        var res = false
        for (stack in liquids) {
            if (entity.liquids.get(stack.liquid) + stack.amount * multiple(entity) > entity.block.liquidCapacity - 0.001f) {
                if (blockWhenFull) return false
            } else res = true
        }
        return res
    }

    companion object {
        private val TMP = ObjectMap<Liquid?, LiquidStack?>()
    }
}