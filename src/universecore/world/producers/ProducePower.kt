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
import universecore.world.consumers.ConsumeType
import universecore.world.consumers.cons.ConsumePower
import kotlin.math.max
import kotlin.math.roundToInt

class ProducePower<T>(var powerProduction: Float) : BaseProduce<T>() where T : Building, T : ProducerBuildComp {
  var showIcon: Boolean = true

  override fun type(): ProduceType<ProducePower<*>> {
    return ProduceType.power
  }

  override fun hasIcons(): Boolean {
    return showIcon
  }

  override fun buildIcons(table: Table) {
    if (showIcon) {
      ConsumePower.buildPowerImage(table, powerProduction)
    }
  }

  override fun merge(other: BaseProduce<T>) {
    if (other is ProducePower<*>) {
      powerProduction += other.powerProduction
      return
    }
    throw IllegalArgumentException("only merge production with same type")
  }

  override fun produce(entity: T) {/*不在此更新能量生产*/
  }

  override fun update(entity: T) {/*此处不进行能量更新*/
  }

  override fun buildBars(entity: T, bars: Table) {
    val prod = Floatp { entity.powerProdEfficiency * entity.producer!!.current!!.get(ProduceType.power)!!.powerProduction }
    val cons = Floatp {
      // 正确的写法
      val cp =
        if (entity.block.consumesPower && entity.consumer.current != null) entity.consumer.current!!.get(ConsumeType.power)
        else null
      cp?.let {
        @Suppress("UNCHECKED_CAST")
        (it as ConsumePower<T>).usage * it.multiple(entity)
      }?:0f
        
    }
    bars.add(
      Bar(
        { Core.bundle.format("bar.poweroutput", Strings.fixed(max(prod.get() - cons.get(), 0f) * 60 * entity.timeScale(), 1)) },
        { Pal.powerBar },
        entity::powerProdEfficiency
      )
    ).growX()
    bars.row()
  }

  override fun display(stats: Stats) {
    stats.add(Stat.basePowerGeneration, (powerProduction * 60f).roundToInt().toFloat(), StatUnit.powerSecond)
  }

  override fun valid(entity: T): Boolean {
    return entity.power != null
  }
}