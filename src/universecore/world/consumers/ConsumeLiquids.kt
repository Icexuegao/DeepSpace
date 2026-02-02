package universecore.world.consumers

import arc.Core
import arc.func.Boolp
import arc.func.Cons
import arc.func.Func
import arc.func.Prov
import arc.math.Mathf
import arc.scene.ui.layout.Table
import arc.struct.ObjectMap
import arc.struct.Seq
import mindustry.ctype.Content
import mindustry.gen.Building
import mindustry.type.Liquid
import mindustry.type.LiquidStack
import mindustry.ui.ReqImage
import mindustry.world.meta.Stat
import mindustry.world.meta.StatValue
import mindustry.world.meta.StatValues
import mindustry.world.meta.Stats
import universecore.components.blockcomp.ConsumerBuildComp
import kotlin.math.min

class ConsumeLiquids<T>(liquids: Array<out LiquidStack>) : ConsumeLiquidBase<T>() where T : Building, T : ConsumerBuildComp {
    var portion: Boolean = false

    init {
        this.consLiquids = liquids as Array<LiquidStack>
    }

    override fun type(): ConsumeType<*> {
        return ConsumeType.liquid
    }

    public override fun buildIcons(table: Table) {
        buildLiquidIcons(table, consLiquids!!, false, displayLim)
    }

    fun portion() {
        this.portion = true
    }

    public override fun merge(other: BaseConsume<T>) {
        if (other is ConsumeLiquids<*>) {
            TMP.clear()
            for (stack in consLiquids!!) {
                TMP.put(stack.liquid, stack)
            }

            for (stack in other.consLiquids!!) {
                TMP.get(stack.liquid, Prov { LiquidStack(stack.liquid, 0f) })!!.amount += stack.amount
            }

            consLiquids = TMP.values().toSeq().sort(Comparator { a: LiquidStack?, b: LiquidStack? -> a!!.liquid.id - b!!.liquid.id }).toArray<LiquidStack>(LiquidStack::class.java)
            return
        }
        throw IllegalArgumentException("only merge consume with same type")
    }

    public override fun consume(entity: T) {
        if (portion) for (stack in consLiquids!!) {
            entity!!.liquids.remove(stack.liquid, stack.amount * 60 * multiple(entity))
        }
    }

    public override fun update(entity: T) {
        if (!portion) for (stack in consLiquids!!) {
            entity!!.liquids.remove(stack.liquid, stack.amount * parent!!.delta(entity) * multiple(entity))
        }
    }

    public override fun display(stats: Stats) {
        stats.add(Stat.input, StatValue { table: Table? ->
            table!!.row()
            table.table(Cons { t: Table? ->
                t!!.defaults().left().fill().padLeft(6f)
                t.add(Core.bundle.get("misc.liquid") + ":")
                for (stack in consLiquids!!) {
                    t.add<Table?>(StatValues.displayLiquid(stack.liquid, stack.amount * 60, true))
                }
            }).left().padLeft(5f)
        })
    }

    public override fun build(entity: T, table: Table) {
        for (stack in consLiquids!!) {
            table.add<ReqImage?>(
                ReqImage(
                    stack.liquid.uiIcon,
                    Boolp { entity!!.liquids != null && entity.liquids.get(stack.liquid) > 0 })
            ).padRight(8f)
        }
        table.row()
    }

    public override fun efficiency(entity: T): Float {
        if (entity!!.liquids == null) return 0f
        if (portion) {
            for (stack in consLiquids!!) {
                if (entity.liquids.get(stack.liquid) < stack.amount * multiple(entity) * 60) return 0f
            }
            return 1f
        } else {
            var min = 1f

            for (stack in consLiquids!!) {
                min = min(entity.liquids.get(stack.liquid) / (stack.amount * multiple(entity)), min)
            }

            if (min < 0.0001f) return 0f
            return Mathf.clamp(min)
        }
    }

    public override fun filter(): Seq<Content?>? {
        return Seq.with<LiquidStack>(*consLiquids!!).map<Content?>(Func { s: LiquidStack? -> s!!.liquid })
    }

    companion object {
        private val TMP = ObjectMap<Liquid?, LiquidStack?>()
    }
}