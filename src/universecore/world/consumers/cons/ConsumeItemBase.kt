package universecore.world.consumers.cons

import arc.scene.ui.layout.Cell
import arc.scene.ui.layout.Table
import ice.world.meta.IStatValues
import mindustry.type.ItemStack
import universecore.components.blockcomp.ConsumerBuildComp
import universecore.world.consumers.BaseConsume
import universecore.world.consumers.ConsumeType

abstract class ConsumeItemBase<T : ConsumerBuildComp> : BaseConsume<T>() {
  companion object {
    fun buildItemIcons(table: Table, items: Array<out ItemStack>, or: Boolean, limit: Int) {
      var count = 0
      for (stack in items) {
        count++
        if (count > 0 && or) table.add("/").set(Cell.defaults()).fill()
        if (limit in 0..count) {
          table.add("...")
          break
        }

        table.add(IStatValues.stack(stack))
      }
    }
  }

  var consItems: Array<ItemStack>? = null
  var displayLim: Int = 4

  override fun type() = ConsumeType.item
}