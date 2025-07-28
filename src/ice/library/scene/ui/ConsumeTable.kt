package ice.library.scene.ui

import arc.scene.ui.layout.Table
import ice.library.scene.element.display.ItemDisplay
import ice.library.scene.element.display.LiquidDisplay
import ice.library.scene.element.display.PowerDisplay
import mindustry.Vars
import mindustry.world.consumers.*

object ConsumeTable {
    val renderers = HashMap<Class<*>, (Consume, Table) -> Unit>()

    init {
        putcs<ConsumeItems> { table ->
            items.forEach { itemStack ->
                table.add(ItemDisplay(itemStack.item, itemStack.amount)).pad(5f)
            }
        }
        putcs<ConsumeLiquids> { table ->
            liquids.forEach { liquidStack ->
                table.add(LiquidDisplay(liquidStack.liquid, liquidStack.amount * 60)).pad(5f)
            }
        }
        putcs<ConsumeItemFilter> { table ->
            val select = Vars.content.items().select(filter)
            for ((index1, item) in select.withIndex()) {
                if (index1 < 4) {
                    table.image(item.fullIcon).size(32f).pad(5f)
                }
            }
            if (select.size > 4) table.add("...")
        }
        putcs<ConsumePower> { table ->
            table.add(PowerDisplay(usage * 60f)).height(32f).pad(5f)
        }
    }

    inline fun <reified T : Consume> putcs(noinline func: T.(Table) -> Unit) {
        renderers[T::class.java] = { consume, table ->
            (consume as T).func(table)
        }
    }

    fun Consume.display(table: Table) {
        renderers[this::class.java]?.invoke(this, table) ?: error(
            "未注册的消耗器${this::class.simpleName}")
    }
}