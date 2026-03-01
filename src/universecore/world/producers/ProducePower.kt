package universecore.world.producers

import arc.Core
import arc.func.Floatp
import arc.scene.ui.layout.Table
import arc.util.Strings
import mindustry.gen.Building
import mindustry.graphics.Pal
import mindustry.ui.Bar
import mindustry.world.meta.Stat
import mindustry.world.meta.StatUnit
import mindustry.world.meta.Stats
import universecore.components.blockcomp.ProducerBuildComp
import universecore.world.consumers.cons.ConsumePower
import universecore.world.consumers.ConsumeType
import kotlin.math.max

class ProducePower<T>(var powerProduction: Float) : BaseProduce<T>() where T : Building, T : ProducerBuildComp {
    var showIcon: Boolean = true

    public override fun type(): ProduceType<ProducePower<*>> {
        return ProduceType.power
    }

    public override fun hasIcons(): Boolean {
        return showIcon
    }

    public override fun buildIcons(table: Table) {
        if (showIcon) {
            ConsumePower.buildPowerImage(table, powerProduction)
        }
    }

    public override fun merge(other: BaseProduce<T>) {
        if (other is ProducePower<*>) {
            powerProduction += other.powerProduction
            return
        }
        throw IllegalArgumentException("only merge production with same type")
    }

    public override fun produce(entity: T) {
        /*不在此更新能量生产*/
    }

    public override fun update(entity: T) {
        /*此处不进行能量更新*/
    }

    public override fun buildBars(entity: T, bars: Table) {
        val prod = Floatp { entity.powerProdEfficiency * entity!!.producer!!.current!!.get(ProduceType.power)!!.powerProduction }
        val cons = Floatp {
            // 正确的写法
            val cp = if (entity!!.block.consumesPower && entity.consumer!!.current != null)
                entity.consumer!!.current!!.get(ConsumeType.power) as? ConsumePower<Building>
            else null

            if (cp != null) cp.usage * cp!!.multiple(entity) else 0f

        }
        bars.add(
            Bar(
                { Core.bundle.format("bar.poweroutput", Strings.fixed(max(prod.get() - cons.get(), 0f) * 60 * entity!!.timeScale(), 1)) },
                { Pal.powerBar },
                entity!!::powerProdEfficiency
            )
        ).growX()
        bars.row()
    }

    public override fun display(stats: Stats) {
        stats.add(Stat.basePowerGeneration, powerProduction * 60.0f, StatUnit.powerSecond)
    }

    public override fun valid(entity: T): Boolean {
        return entity!!.power != null
    }
}