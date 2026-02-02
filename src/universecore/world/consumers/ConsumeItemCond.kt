package universecore.world.consumers

import arc.Core
import arc.func.Boolf
import arc.func.Boolp
import arc.func.Cons
import arc.func.Func
import arc.scene.ui.layout.Table
import arc.struct.Seq
import mindustry.Vars
import mindustry.ctype.Content
import mindustry.gen.Building
import mindustry.type.Item
import mindustry.type.ItemStack
import mindustry.ui.MultiReqImage
import mindustry.ui.ReqImage
import mindustry.world.meta.Stat
import mindustry.world.meta.StatValue
import mindustry.world.meta.StatValues
import mindustry.world.meta.Stats
import universecore.components.blockcomp.ConsumerBuildComp
import kotlin.math.max
import kotlin.math.min

class ConsumeItemCond<T> : ConsumeItemBase<T>() where T : Building, T : ConsumerBuildComp {
    var minRadioactivity: Float = 0f
    var maxRadioactivity: Float = 0f
    var minFlammability: Float = 0f
    var maxFlammability: Float = 0f
    var minCharge: Float = 0f
    var maxCharge: Float = 0f
    var minExplosiveness: Float = 0f
    var maxExplosiveness: Float = 0f
    var usage: Int = 0
    var filter: Boolf<Item?>? = null

    fun getCurrCons(entity: T?): Item? {
        for (stack in consItems!!) {
            if (entity!!.items.get(stack.item) >= stack.amount) return stack.item
        }
        return null
    }

    val cons: Array<ItemStack>
        get() {
            if (consItems == null) {
                val seq = Seq<ItemStack?>()
                for (item in Vars.content.items()) {
                    if (filter != null && !filter!!.get(item)) continue

                    if (minRadioactivity != maxRadioactivity) {
                        if (item.radioactivity > maxRadioactivity || item.radioactivity < minRadioactivity) continue
                    }
                    if (minFlammability != maxFlammability) {
                        if (item.flammability > maxFlammability || item.flammability < minFlammability) continue
                    }
                    if (minCharge != maxCharge) {
                        if (item.charge > maxCharge || item.charge < minCharge) continue
                    }
                    if (minExplosiveness != maxExplosiveness) {
                        if (item.explosiveness > maxExplosiveness || item.explosiveness < minExplosiveness) continue
                    }

                    seq.add(ItemStack(item, usage))
                }
                consItems = seq.toArray<ItemStack>(ItemStack::class.java)
            }

            return consItems!!
        }

    public override fun buildIcons(table: Table) {
        buildItemIcons(table!!, this.cons, true, displayLim)
    }

    public override fun merge(other: BaseConsume<T>) {
        if (other is ConsumeItemCond<*>) {
            minRadioactivity = min(other.minRadioactivity, minRadioactivity)
            minFlammability = min(other.minFlammability, minFlammability)
            minCharge = min(other.minCharge, minCharge)
            minExplosiveness = min(other.minExplosiveness, minExplosiveness)

            maxRadioactivity = max(other.maxRadioactivity, maxRadioactivity)
            maxFlammability = max(other.maxFlammability, maxFlammability)
            maxCharge = max(other.maxCharge, maxCharge)
            maxExplosiveness = max(other.maxExplosiveness, maxExplosiveness)

            usage += other.usage

            consItems = null
            this.cons
        } else throw IllegalArgumentException("only merge consume with same type")
    }

    public override fun consume(entity: T) {
        val cons = this.cons
        if (cons.size == 0) return
        val curr = getCurrCons(entity)
        if (curr == null) return
        for (con in cons) {
            if (con.item === curr) {
                entity!!.items.remove(con.item, con.amount)
            }
        }
    }

    public override fun update(entity: T) {}

    public override fun display(stats: Stats) {
        stats.add(Stat.input, StatValue { table: Table? ->
            table!!.row()
            table.table(Cons { t: Table? ->
                t!!.defaults().left().fill().padLeft(6f)
                t.add(Core.bundle.get("misc.item") + ":")
                var count = 0
                for (stack in this.cons) {
                    if (count != 0) t.add("[gray]/[]")
                    if (count != 0 && count % 6 == 0) t.row()
                    t.add<Table?>(StatValues.displayItem(stack.item, stack.amount * 60, true))
                    count++
                }
            }).left().padLeft(5f)
        })
    }

    public override fun build(entity: T, table: Table) {
        val list = Vars.content.items().select(Boolf { l: Item? -> !l!!.isHidden() && filter!!.get(l) })
        val image = MultiReqImage()
        list.each(Cons { item: Item? ->
            image.add(ReqImage(item!!.uiIcon, Boolp { entity!!.items != null && entity.items.get(item) > 0 }))
        })

        table.add<MultiReqImage?>(image).size((8 * 4).toFloat())
    }

    public override fun efficiency(entity: T): Float {
        val cons = this.cons
        if (cons.size == 0) return 1f
        val curr = getCurrCons(entity)
        if (curr == null) return 0f

        for (con in cons) {
            if (curr === con.item) return 1f
        }
        return 0f
    }

    public override fun filter(): Seq<Content?>? {
        return Seq.with<ItemStack?>(*this.cons).map<Content?>(Func { s: ItemStack? -> s!!.item })
    }
}