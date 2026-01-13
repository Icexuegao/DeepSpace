package universecore.world.consumers

import arc.func.Boolp
import arc.func.Cons
import arc.func.Func
import arc.func.Prov
import arc.scene.ui.Image
import arc.scene.ui.layout.Stack
import arc.scene.ui.layout.Table
import arc.struct.ObjectMap
import arc.struct.Seq
import arc.util.Scaling
import mindustry.ctype.Content
import mindustry.ctype.UnlockableContent
import mindustry.gen.Building
import mindustry.type.PayloadStack
import mindustry.ui.ReqImage
import mindustry.ui.Styles
import mindustry.world.meta.Stat
import mindustry.world.meta.StatValue
import mindustry.world.meta.StatValues
import mindustry.world.meta.Stats
import universecore.components.blockcomp.ConsumerBuildComp

class ConsumePayload<T>(var payloads: Array<PayloadStack>) : BaseConsume<T>() where T : Building, T : ConsumerBuildComp {
    var displayLim: Int = 4

    public override fun type(): ConsumeType<*>? {
        return ConsumeType.payload
    }

    public override fun buildIcons(table: Table) {
        buildPayloadIcons(table, payloads, displayLim)
    }

    public override fun merge(other: BaseConsume<T>) {
        if (other is ConsumePayload<*>) {
            TMP.clear()
            for (stack in payloads) {
                TMP.put(stack.item, stack)
            }

            for (stack in (other as ConsumePayload<T?>).payloads) {
                TMP.get(stack.item, Prov { PayloadStack(stack.item, 0) })!!.amount += stack.amount
            }

            payloads = TMP.values().toSeq().sort(Comparator { a: PayloadStack?, b: PayloadStack? -> a!!.item.id - b!!.item.id }).toArray<PayloadStack?>(PayloadStack::class.java)
        } else throw IllegalArgumentException("only merge consume with same type")
    }

    public override fun efficiency(build: T): Float {
        for (stack in payloads) {
            if (!build!!.getPayloads().contains(stack.item, stack.amount)) {
                return 0f
            }
        }
        return 1f
    }

    public override fun consume(build: T) {
        for (stack in payloads) {
            build!!.getPayloads().remove(stack.item, stack.amount)
        }
    }

    public override fun update(entity: T) {}

    public override fun display(stats: Stats) {
        for (stack in payloads) {
            stats.add(Stat.input, StatValue { t: Table? ->
                t!!.add<Stack?>(StatValues.stack(stack))
                t.add(stack.item.localizedName).padLeft(4f).padRight(4f)
            })
        }
    }

    public override fun build(entity: T, table: Table) {
        val inv = entity.getPayloads()

        table.table(Cons { c: Table? ->
            var i = 0
            for (stack in payloads) {
                c!!.add<ReqImage?>(
                    ReqImage(
                        StatValues.stack(stack),
                        Boolp { inv.contains(stack.item, stack.amount) })
                ).padRight(8f)
                if (++i % 4 == 0) c.row()
            }
        }).left()
    }

    public override fun filter(): Seq<Content?>? {
        return Seq.with<PayloadStack?>(*payloads).map<Content?>(Func { s: PayloadStack? -> s!!.item })
    }

    companion object {
        private val TMP = ObjectMap<UnlockableContent?, PayloadStack?>()

        fun buildPayloadIcons(table: Table, payloads: Array<PayloadStack>, displayLim: Int) {
            var count = 0
            for (stack in payloads) {
                count++
                if (displayLim >= 0 && count > displayLim) {
                    table.add("...")
                    break
                }

                table.stack(
                    Table(Cons { o: Table? ->
                        o!!.left()
                        o.add<Image?>(Image(stack.item.fullIcon)).size(32f).scaling(Scaling.fit)
                    }),
                    Table(Cons { t: Table? ->
                        t!!.left().bottom()
                        t.add(stack.amount.toString() + "").style(Styles.outlineLabel)
                        t.pack()
                    })
                )
            }
        }
    }
}