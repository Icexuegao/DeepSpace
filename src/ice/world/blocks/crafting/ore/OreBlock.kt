package ice.world.blocks.crafting.ore

import arc.func.Prov
import arc.util.Strings
import ice.ui.tex.Colors
import ice.ui.tex.IceTex
import ice.world.meta.stat.IceStat
import mindustry.content.Items
import mindustry.gen.Building
import mindustry.type.Category
import mindustry.type.ItemStack
import mindustry.ui.ItemDisplay
import mindustry.ui.Styles
import mindustry.world.Block
import mindustry.world.meta.StatValue


open class OreBlock(name: String) : Block(name) {
    private val interval = 807f
    var formula = OreFormula.OreFormulaStack()

    init {
        health = 500
        size = 4
        hasItems = true
        update = true
        itemCapacity = 50
        buildType = Prov(OreBlock::OreBlockBuild)
        this.requirements(Category.crafting, ItemStack.with(Items.copper, 1))
    }

    override fun setStats() {
        super.setStats()
        stats.add(IceStat.配方, formulasUi())
    }

    private fun formulasUi(): StatValue {
        return StatValue { table ->
            table.table { pan ->
                pan.margin(10f)
                formula.oreFormula.each { ore ->
                    pan.table { t ->
                        t.table { input ->
                            input.add("输入:").pad(5f).expandX().row()
                            ore.input.forEach { items ->
                                input.add(ItemDisplay(items.item, items.amount)).pad(5f).grow().row()
                            }
                        }.grow().left().padRight(5f).margin(10f).width(interval / 3)

                        t.table { time ->
                            time.table {
                                it.image(IceTex.time).size(21f).expandY().bottom()
                                it.add(Strings.format("@ @", ore.crftTime / 60f, "秒")).expandY().bottom()
                            }.grow().row()
                            time.table {
                                it.table { it1 ->
                                    it1.image(IceTex.arrow).color(Colors.灰色).grow()
                                }.size(275f / 2, 79f / 2f).expandY().top()
                            }.grow()
                        }.grow().padRight(5f).width(interval / 3).margin(10f)

                        t.table { output ->
                            output.add("输出:").pad(5f).expandX().row()
                            val output1 = ore.output
                            output1.forEach { (items, i) ->
                                output.table { ores ->
                                    ores.add("${i}%").padRight(5f)
                                    ores.add(ItemDisplay(items.item, items.amount)).pad(5f)
                                }.grow().row()
                            }
                        }.grow().right().margin(10f).width(interval / 3)

                        t.cells.each {
                            it.table.background =Styles.grayPanel
                        }

                    }.padBottom(10f).row()
                }
            }
        }
    }

    class OreBlockBuild : Building()
}

