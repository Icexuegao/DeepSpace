package universecore.components.blockcomp

import arc.func.Cons
import arc.scene.ui.layout.Table
import mindustry.graphics.Pal
import mindustry.world.meta.Stats
import universecore.world.consumers.BaseConsumers
import universecore.world.producers.BaseProducers

/**工厂方块组件，描述[工厂建筑][FactoryBuildComp]中必要的一些属性
 *
 * @since 1.4
 * @author EBwilson
 */
interface FactoryBlockComp : ProducerBlockComp {
    /**方块的热机效率，由0-1的插值，为方块从启动到最大效率的速度，这是[arc.math.Mathf.lerpDelta]的插值 */
    var warmupSpeed: Float

    /**方块的冷却速度，由0-1的插值，为方块完全停机的速度，这是[arc.math.Mathf.lerpDelta]的插值 */ //  @Annotations.BindField("stopSpeed")
    var stopSpeed: Float

    companion object {
        fun buildRecipe(table: Table, consumers: BaseConsumers?, producers: BaseProducers?) {
            val stats = Stats()

            if (consumers != null) {
                consumers.display(stats)
            }
            if (producers != null) {
                producers.display(stats)
            }

            buildStatTable(table, stats)
        }

        fun buildStatTable(table: Table, stat: Stats) {
            for (cat in stat.toMap().keys()) {
                val map = stat.toMap().get(cat)
                if (map.size == 0) continue

                if (stat.useCategories) {
                    table.add("@category." + cat.name).color(Pal.accent).fillX()
                    table.row()
                }

                for (state in map.keys()) {
                    table.table(Cons { inset: Table? ->
                        inset!!.left()
                        inset.add("[lightgray]" + state.localized() + ":[] ").left()
                        val arr = map.get(state)
                        for (value in arr) {
                            value.display(inset)
                            inset.add().size(10f)
                        }
                    }).fillX().padLeft(10f)
                    table.row()
                }
            }
        }
    }
}