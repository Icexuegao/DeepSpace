package universecore.world.consumers.cons

import arc.func.Boolf
import arc.func.Cons
import arc.func.Floatf
import arc.math.Mathf
import arc.scene.ui.layout.Table
import arc.struct.Seq
import ice.ui.bundle.BaseBundle.Bundle.Companion.localizedName
import ice.world.meta.IStatValues
import ice.world.meta.IceStats
import mindustry.Vars
import mindustry.ctype.Content
import mindustry.gen.Building
import mindustry.type.Liquid
import mindustry.type.LiquidStack
import mindustry.ui.MultiReqImage
import mindustry.ui.ReqImage
import mindustry.world.meta.Stat
import mindustry.world.meta.Stats
import universecore.components.blockcomp.ConsumerBuildComp
import universecore.world.consumers.BaseConsume
import kotlin.math.max
import kotlin.math.min

open class ConsumeLiquidCond<T> : ConsumeLiquidBase<T>() where T : Building, T : ConsumerBuildComp {
  var minTemperature: Float = 0f
  var maxTemperature: Float = 0f
  var minFlammability: Float = 0f
  var maxFlammability: Float = 0f
  var minHeatCapacity: Float = 0f
  var maxHeatCapacity: Float = 0f
  var minViscosity: Float = 0f
  var maxViscosity: Float = 0f
  var minExplosiveness: Float = 0f
  var maxExplosiveness: Float = 0f
  var consGas: Int = -1
  var isCoolant: Boolean = false
  var usage: Float = 0.1f
  var filter: Boolf<Liquid> = Boolf { !it.hidden }
  var usageMultiplier: Floatf<Liquid> = Floatf { _: Liquid -> 1f }
  var liquidEfficiency: Floatf<Liquid> = Floatf { _: Liquid -> 1f }

  fun getCurrCons(entity: Building?): Liquid? {
    for (liquid in consLiquids!!) {
      if (entity!!.liquids.get(liquid.liquid) > 0.001f) return liquid.liquid
    }
    return null
  }

  val cons: Array<LiquidStack>
    get() {
      if (consLiquids == null) {
        val seq = Seq<LiquidStack?>()
        for (liquid in Vars.content.liquids()) {
          if (!filter.get(liquid)) continue

          if (minTemperature != maxTemperature) {
            if (liquid.temperature !in minTemperature..maxTemperature) continue
          }
          if (minFlammability != maxFlammability) {
            if (liquid.flammability !in minFlammability..maxFlammability) continue
          }
          if (minHeatCapacity != maxHeatCapacity) {
            if (liquid.heatCapacity !in minHeatCapacity..maxHeatCapacity) continue
          }
          if (minExplosiveness != maxExplosiveness) {
            if (liquid.explosiveness !in minExplosiveness..maxExplosiveness) continue
          }
          if (minViscosity != maxViscosity) {
            if (liquid.viscosity !in minViscosity..maxViscosity) continue
          }

          if (isCoolant && !liquid.coolant) continue

          if ((consGas == 1 && !liquid.gas) || (consGas == 0 && liquid.gas)) continue

          seq.add(LiquidStack(liquid, usage * usageMultiplier.get(liquid)))
        }

        consLiquids = seq.toArray(LiquidStack::class.java)
      }

      return consLiquids!!
    }

  override fun buildIcons(table: Table) {
    buildLiquidIcons(table, this.cons, true, displayLim)
  }

  override fun merge(other: BaseConsume<T>) {
    if (other is ConsumeLiquidCond<*>) {
      minTemperature = min(other.minTemperature, minTemperature)
      minFlammability = min(other.minFlammability, minFlammability)
      minHeatCapacity = min(other.minHeatCapacity, minHeatCapacity)
      minViscosity = min(other.minViscosity, minViscosity)
      minExplosiveness = min(other.minExplosiveness, minExplosiveness)

      maxTemperature = max(other.maxTemperature, maxTemperature)
      maxFlammability = max(other.maxFlammability, maxFlammability)
      maxHeatCapacity = max(other.maxHeatCapacity, maxHeatCapacity)
      maxViscosity = max(other.maxViscosity, maxViscosity)
      maxExplosiveness = max(other.maxExplosiveness, maxExplosiveness)

      usage += other.usage
      val mul = usageMultiplier
      val mulO = other.usageMultiplier
      usageMultiplier = Floatf { l: Liquid? -> mul.get(l) * mulO.get(l) }

      consLiquids = null
      this.cons
    } else throw IllegalArgumentException("only merge consume with same type")
  }

  override fun consume(entity: T) {}

  override fun update(entity: T) {
    val cons = this.cons
    if (cons.isEmpty()) return
    val curr = getCurrCons(entity) ?: return

    for (con in cons) {
      if (con.liquid === curr) {
        entity.liquids.remove(con.liquid, con.amount * parent!!.delta(entity) * multiple(entity))
        return
      }
    }
  }

  override fun display(stats: Stats) {
    stats.add(Stat.input) { table: Table ->
      table.row()
      table.table { t: Table ->
        t.defaults().left().padLeft(6f)

        t.add("${IceStats.流体.localizedName}:").expandY().top()

        val tables = Array(cons.size / 4 + 1) {
          Table()
        }
        var index = 0
        for ((count, stack) in this.cons.withIndex()) {
          // if (count != 0) t.add("[gray]/[]")

          if (count != 0 && count % 4 == 0) index++

          tables[index].add(IStatValues.displayLiquid(stack.liquid, stack.amount * 60, true)).row()
        }
        tables.forEach(t::add)

      }.left().padLeft(5f)
    }
  }

  override fun build(entity: T, table: Table) {
    val seq = Seq<Liquid>()
    cons.forEach {
      seq.add(it.liquid)
    }
    val list = seq.select { l: Liquid -> !l.isHidden && filter.get(l) }
    val image = MultiReqImage()
    list.each(Cons { liquid: Liquid -> image.add(ReqImage(liquid.uiIcon) { entity.liquids != null && entity.liquids.get(liquid) > 0 }) })

    table.add(image).size((8 * 4).toFloat())
  }

  override fun efficiency(entity: T): Float {
    val cons = this.cons
    if (cons.isEmpty()) return 1f
    val curr = getCurrCons(entity) ?: return 0f

    for (stack in cons) {
      if (curr === stack.liquid) {
        return liquidEfficiency.get(stack.liquid) * Mathf.clamp(entity.liquids.get(stack.liquid) / stack.amount)
      }
    }
    return 0f
  }

  override fun filter(): Seq<Content>? {
    return Seq.with(*this.cons).map { s: LiquidStack -> s.liquid }
  }
}