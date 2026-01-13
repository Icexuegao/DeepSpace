package singularity.world.products

import arc.Core
import arc.func.Cons
import arc.graphics.Color
import arc.scene.ui.Image
import arc.scene.ui.layout.Stack
import arc.scene.ui.layout.Table
import arc.util.Strings
import mindustry.Vars
import mindustry.core.UI
import mindustry.gen.Building
import mindustry.ui.Styles
import mindustry.world.meta.StatUnit
import mindustry.world.meta.StatValue
import mindustry.world.meta.Stats
import singularity.Singularity
import singularity.world.components.MediumBuildComp
import singularity.world.meta.SglStat
import universecore.components.blockcomp.ProducerBuildComp
import universecore.world.producers.BaseProduce
import universecore.world.producers.ProduceType
import kotlin.math.min

class ProduceMedium<T>(var product: Float) : BaseProduce<T>() where T : Building?, T : ProducerBuildComp, T : MediumBuildComp {
    public override fun type(): ProduceType<*> {
        return SglProduceType.medium
    }

    public override fun hasIcons(): Boolean {
        return false
    }

    public override fun buildIcons(table: Table) {
    }

    public override fun merge(baseProduce: BaseProduce<T>) {
        if (baseProduce is ProduceMedium<*>) {
            product += baseProduce.product

            return
        }
        throw IllegalArgumentException("only merge product with same type")
    }

    public override fun produce(entity: T) {}

    public override fun update(entity: T) {
        entity!!.mediumContains(entity.mediumContains() + min(entity.remainingMediumCapacity(), product * parent!!.cons!!.delta(entity) * multiple(entity)))
    }

    public override fun display(stats: Stats) {
        stats.add(SglStat.special, StatValue { table: Table? ->
            table!!.row()
            table.table(Cons { t: Table? ->
                t!!.defaults().left().fill().padLeft(6f)
                t.add(Core.bundle.get("misc.output") + ":").left()
                val display = product * 60
                t.table(Cons { icon: Table? ->
                    icon!!.add(object : Stack() {
                        init {
                            add(Image(Singularity.getModAtlas("medium")))

                            if (product != 0f) {
                                val t = Table().left().bottom()

                                t.add(if (display > 1000) UI.formatAmount((display as Number).toLong()) else Strings.autoFixed(display, 2) + "").style(Styles.outlineLabel)
                                add(t)
                            }
                        }
                    }).size(Vars.iconMed).padRight((3 + (if (product != 0f && Strings.autoFixed(display, 2).length > 2) 8 else 0)).toFloat())
                    icon.add(StatUnit.perSecond.localized()).padLeft(2f).padRight(5f).color(Color.lightGray).style(Styles.outlineLabel)
                    icon.add(Core.bundle.get("misc.medium"))
                })
            }).left().padLeft(5f)
        })
    }

    public override fun dump(entity: T) {
        entity!!.dumpMedium()
    }

    public override fun valid(entity: T): Boolean {
        return entity!!.remainingMediumCapacity() > 0.001f
    }
}