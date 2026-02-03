package singularity.world.consumers

import arc.func.Cons
import arc.math.Mathf
import arc.scene.ui.Image
import arc.scene.ui.layout.Table
import arc.struct.Seq
import arc.util.Scaling
import mindustry.core.UI
import mindustry.ctype.Content
import mindustry.gen.Building
import mindustry.ui.Styles
import mindustry.world.meta.Stats
import singularity.graphic.SglDrawConst
import singularity.world.components.NuclearEnergyBuildComp
import singularity.world.meta.SglStat
import singularity.world.meta.SglStatUnit
import universecore.components.blockcomp.ConsumerBuildComp
import universecore.world.consumers.BaseConsume
import universecore.world.consumers.ConsumeType

class SglConsumeEnergy<T>(var usage: Float) : BaseConsume<T>() where T : Building, T : NuclearEnergyBuildComp, T : ConsumerBuildComp {
    var buffer: Boolean = false

    fun buffer() {
        this.buffer = true
    }

    public override fun type(): ConsumeType<SglConsumeEnergy<*>> {
        return SglConsumeType.energy
    }

    public override fun buildIcons(table: Table) {
        buildNuclearIcon(table, usage)
    }

    public override fun merge(baseConsume: BaseConsume<T>) {
        if (baseConsume is SglConsumeEnergy<*>) {
            buffer = buffer or baseConsume.buffer
            usage += baseConsume.usage

            return
        }
        throw IllegalArgumentException("only merge consume with same type")
    }

    public override fun consume(entity: T) {
        if (buffer) entity!!.handleEnergy(-usage * 60 * multiple(entity))
    }

    public override fun update(entity: T) {
        if (!buffer) {
            entity!!.handleEnergy(-usage * parent!!.delta(entity))
        }
    }

    public override fun display(stats: Stats) {
        stats.add(SglStat.consumeEnergy, usage * 60, SglStatUnit.neutronFluxSecond)
    }

    public override fun build(entity: T, table: Table) {
        table.row()
    }

    public override fun efficiency(entity: T): Float {
        if (entity!!.energy() == null) return 0f
        if (buffer) {
            return (if (entity.energy()!!.energy >= usage * 60 * multiple(entity)) 1 else 0).toFloat()
        }
        return Mathf.clamp(entity.energy()!!.energy / (usage * 12.5f * multiple(entity)))
    }

    public override fun filter(): Seq<Content?>? {
        return null
    }

    companion object {
        fun buildNuclearIcon(table: Table, amount: Float) {
            table.stack(
                Table(Cons { o: Table? ->
                    o!!.left()
                    o.add<Image?>(Image(SglDrawConst.nuclearIcon)).size(32f).scaling(Scaling.fit)
                }),
                Table(Cons { t: Table? ->
                    t!!.left().bottom()
                    t.add(if (amount * 60 >= 1000) UI.formatAmount((amount * 60).toLong()) + "NF/s" else (amount * 60).toString() + "NF/s").style(Styles.outlineLabel)
                    t.pack()
                })
            )
        }
    }
}