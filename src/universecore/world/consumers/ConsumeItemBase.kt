package universecore.world.consumers

import arc.scene.ui.layout.Cell
import arc.scene.ui.layout.Stack
import arc.scene.ui.layout.Table
import mindustry.type.ItemStack
import mindustry.world.meta.StatValues
import universecore.components.blockcomp.ConsumerBuildComp

abstract class ConsumeItemBase<T : ConsumerBuildComp> : BaseConsume<T>() {
    var consItems: Array<ItemStack>? = null
    var displayLim: Int = 4

    public override fun type(): ConsumeType<*>? {
        return ConsumeType.item
    }

    companion object {
        fun buildItemIcons(table: Table, items: Array<ItemStack>, or: Boolean, limit: Int) {
            var count = 0
            for (stack in items) {
                count++
                if (count > 0 && or) table.add("/").set(Cell.defaults()).fill()
                if (limit >= 0 && count > limit) {
                    table.add("...")
                    break
                }

                table.add<Stack?>(StatValues.stack(stack))
            }
        }
    }
}