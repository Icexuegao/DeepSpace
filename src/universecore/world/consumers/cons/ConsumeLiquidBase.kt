package universecore.world.consumers.cons

import arc.math.Mathf
import arc.scene.ui.Image
import arc.scene.ui.layout.Cell
import arc.scene.ui.layout.Table
import arc.util.Scaling
import mindustry.core.UI
import mindustry.gen.Building
import mindustry.type.LiquidStack
import mindustry.ui.Bar
import mindustry.ui.Styles
import universecore.components.blockcomp.ConsumerBuildComp
import universecore.world.consumers.BaseConsume
import universecore.world.consumers.ConsumeType
import kotlin.math.min

abstract class ConsumeLiquidBase<T> : BaseConsume<T>() where T : Building, T : ConsumerBuildComp {
  companion object {
    fun buildLiquidIcons(table: Table, liquids: Array<out LiquidStack>, or: Boolean, limit: Int) {
      var count = 0
      for (stack in liquids) {
        count++
        if (count > 0 && or) table.add("/").set(Cell.defaults()).fillX()
        if (limit in 0..count) {
          table.add("...")
          break
        }

        table.stack(
          Table { o: Table ->
            o.left()
            o.add(Image(stack.liquid.fullIcon)).size(32f).scaling(Scaling.fit)
          },
          Table { t: Table ->
            t.left().bottom()
            t.add(if (stack.amount * 60 >= 1000) UI.formatAmount((stack.amount * 60).toLong()) + "/s" else (Mathf.round(stack.amount * 600) / 10f).toString() + "/s").style(Styles.outlineLabel)
            t.pack()
          }
        )
      }
    }
  }

  var consLiquids: Array<LiquidStack>? = null
  var displayLim: Int = 4

  override fun type() = ConsumeType.liquid

  override fun buildBars(entity: T, bars: Table) {
    for (stack in consLiquids!!) {
      bars.add(
        Bar(
          { stack.liquid.localizedName },
          { stack.liquid.barColor ?: stack.liquid.color },
          { min(entity.liquids.get(stack.liquid) / entity.block.liquidCapacity, 1f) }
        ))
      bars.row()
    }
  }
}