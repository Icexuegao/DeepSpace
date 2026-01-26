package singularity.world.consumers

import arc.Core
import arc.func.Boolp
import arc.func.Cons
import arc.graphics.Color
import arc.math.Mathf
import arc.scene.ui.Image
import arc.scene.ui.layout.Stack
import arc.scene.ui.layout.Table
import arc.struct.Seq
import arc.util.Strings
import mindustry.Vars
import mindustry.core.UI
import mindustry.ctype.Content
import mindustry.gen.Building
import mindustry.ui.ReqImage
import mindustry.ui.Styles
import mindustry.world.meta.StatUnit
import mindustry.world.meta.StatValue
import mindustry.world.meta.Stats
import singularity.Singularity
import singularity.world.components.MediumBuildComp
import singularity.world.meta.SglStat
import universecore.components.blockcomp.ConsumerBuildComp
import universecore.world.consumers.BaseConsume
import universecore.world.consumers.ConsumeType

class SglConsumeMedium<T>(var request: Float) : BaseConsume<T>() where T : Building, T : MediumBuildComp, T : ConsumerBuildComp {
    public override fun type(): ConsumeType<*>? {
        return SglConsumeType.medium
    }

    public override fun hasIcons(): Boolean {
        return false
    }

    public override fun buildIcons(table: Table) {
    }

    public override fun merge(baseConsume: BaseConsume<T>) {
        if (baseConsume is SglConsumeMedium<*>) {
            request += baseConsume.request

            return
        }
        throw IllegalArgumentException("only merge consume with same type")
    }

    public override fun consume(t: T) {}

    public override fun update(entity: T) {
        entity!!.removeMedium(request * parent!!.delta(entity) * multiple(entity))
    }

    public override fun display(stats: Stats) {
        stats.add(SglStat.special, StatValue { table: Table? ->
            table!!.row()
            table.table(Cons { t: Table? ->
                t!!.defaults().left().fill().padLeft(6f)
                t.add(Core.bundle.get("misc.input") + ":").left()
                val display = request * 60
                t.table(Cons { icon: Table? ->
                    icon!!.add(object : Stack() {
                        init {
                            add(Image(Singularity.getModAtlas("medium")))

                            if (request != 0f) {
                                val t = Table().left().bottom()
                                t.add(if (display > 1000) UI.formatAmount((display as Number).toLong()) else Strings.autoFixed(display, 2) + "").style(Styles.outlineLabel)
                                add(t)
                            }
                        }
                    }).size(Vars.iconMed).padRight((3 + (if (request != 0f && Strings.autoFixed(display, 2).length > 2) 8 else 0)).toFloat())
                    icon.add(StatUnit.perSecond.localized()).padLeft(2f).padRight(5f).color(Color.lightGray).style(Styles.outlineLabel)
                    icon.add(Core.bundle.get("misc.medium"))
                })
            }).left().padLeft(5f)
        })
    }

    public override fun build(entity: T, table: Table) {
        table.add<ReqImage?>(
            ReqImage(
                Singularity.getModAtlas("medium"),
                Boolp { entity!!.mediumContains > request * parent!!.delta(entity) * multiple(entity) + 0.0001f })
        ).padRight(8f)
    }

    public override fun efficiency(t: T): Float {
        return Mathf.clamp(t!!.mediumContains / (request * multiple(t)))
    }

    public override fun filter(): Seq<Content?>? {
        return null
    }
}