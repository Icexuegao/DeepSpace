package ice.library.type.meta.stat

import arc.scene.ui.layout.Table
import ice.library.type.baseContent.blocks.crafting.multipleCrafter.Formula
import ice.library.type.baseContent.blocks.crafting.multipleCrafter.FormulaStack
import mindustry.graphics.Pal
import mindustry.ui.Styles
import mindustry.world.Block
import mindustry.world.meta.StatValue

object IceStatValues {
    fun formulas(formulas: FormulaStack, block: Block): StatValue {
        return StatValue { t: Table ->
            t.table { table: Table ->
                table.left()
                for (f in 0 until formulas.size()) {
                    table.table(Styles.grayPanel) { tab: Table ->
                        tab.left()
                        formula(formulas.getFormula(f), block).display(tab)
                    }.growX().pad(5f).row()
                }
            }.left()
        }
    }

    fun formula(formula: Formula, block: Block): StatValue {
        return StatValue { t: Table ->
            t.table { table: Table ->
                val stats = Stats()
                  formula.display(stats, block)
                displayStats(stats, table)
            }.left().margin(10f)
            t.row()
        }
    }

    fun displayStats(stats: Stats, table: Table) {
        for (cat in stats.toMap().keys()) {
            val map = stats.toMap()[cat]

            if (map.size == 0) continue

            if (stats.useCategories) {
                table.add("@category." + cat.name).color(Pal.accent).fillX()
                table.row()
            }

            for (stat in map.keys()) {
                table.table { inset: Table ->
                    inset.left()
                    inset.add("[lightgray]" + stat.localized() + ":[] ").left().top()
                    val arr = map[stat]
                    for (value in arr) {
                        value.display(inset)
                        inset.add().size(10f)
                    }
                }.fillX()
                table.row()
            }
        }
    }
}
