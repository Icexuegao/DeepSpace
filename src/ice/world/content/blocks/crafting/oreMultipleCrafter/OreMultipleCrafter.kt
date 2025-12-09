package ice.world.content.blocks.crafting.oreMultipleCrafter

import arc.func.Prov
import arc.math.Rand
import arc.scene.ui.layout.Table
import arc.struct.Seq
import arc.util.Strings
import arc.util.io.Reads
import arc.util.io.Writes
import ice.library.scene.element.display.ItemDisplay
import ice.library.scene.element.display.LiquidDisplay
import ice.graphics.IceColor
import ice.graphics.IStyles
import ice.world.content.blocks.abstractBlocks.IceBlock
import ice.world.meta.IceStats
import ice.library.scene.ui.iTableG
import mindustry.content.Items
import mindustry.gen.Building
import mindustry.type.Category
import mindustry.type.Item
import mindustry.type.ItemStack
import mindustry.type.Liquid
import mindustry.ui.Styles
import mindustry.world.consumers.ConsumeItems
import mindustry.world.consumers.ConsumeLiquids
import mindustry.world.meta.StatValue

open class OreMultipleCrafter(name: String) : IceBlock(name) {
    var oreFormula = Seq<OreFormula>()

    init {
        size = 4
        update = true
        health = 500
        hasItems = true
        itemCapacity = 50
        configurable = true
        liquidCapacity = 100f
        buildType = Prov(::OreBlockBuild)
        requirements(Category.crafting, ItemStack.with(Items.copper, 1))
        config(Int::class.javaObjectType) { b: OreBlockBuild, i: Int ->
            b.formula = oreFormula.get(i)
        }
    }


    override fun setStats() {
        super.setStats()
        stats.add(IceStats.配方, formulasUi())
    }

    private fun formulasUi(): StatValue {
        return StatValue { table ->
            table.table { pan ->
                pan.margin(10f)
                oreFormula.forEach { ore ->
                    pan.iTableG { t ->
                        t.table { input ->
                            input.add("输入:").pad(5f).expandX().row()
                            ore.input.forEach { items ->
                                if (items is ConsumeItems) {
                                    items.items.forEach { item ->
                                        input.add(ItemDisplay(item.item, item.amount)).pad(5f).grow().row()
                                    }
                                } else if (items is ConsumeLiquids) {
                                    items.liquids.forEach { item ->
                                        input.add(LiquidDisplay(item.liquid, item.amount)).pad(5f).grow().row()
                                    }
                                }
                            }
                        }.padRight(5f).margin(10f)

                        t.iTableG { time ->
                            time.table {
                                it.image(IStyles.time).size(21f).expandY().bottom()
                                it.add(Strings.format("@ @", ore.crftTime / 60f, "秒")).expandY().bottom()
                            }.grow().row()
                            time.table {
                                it.table { it1 ->
                                    it1.image(IStyles.arrow).color(IceColor.灰1).grow()
                                }.size(275f / 2, 79f / 2f).expandY().top()
                            }.grow()
                        }.padRight(5f).margin(10f)

                        t.iTableG { output ->
                            output.add("输出:").pad(5f).expandX().row()
                            ore.output.forEach { (items, i) ->
                                output.table { ores ->
                                    ores.add("${i}%").padRight(5f)
                                    ores.add(ItemDisplay(items.item, items.amount)).pad(5f)
                                }.grow().row()
                            }
                        }.margin(10f)
                        t.cells.forEach { it.table.background = Styles.grayPanel }
                    }.padBottom(10f).row()
                }
            }
        }
    }


    inner class OreBlockBuild : IceBuild() {
        var formula: OreFormula = oreFormula.first()
        var progress = 0f
        val ranf = Rand()

        override fun config(): Int {
            return oreFormula.indexOf(formula)
        }

        override fun buildConfiguration(table: Table) {
            oreFormula.forEach { ore ->
                table.button({ t ->
                    t.table { input ->
                        input.add("输入:").pad(5f).expandX().row()
                        ore.input.forEach { items ->
                            if (items is ConsumeItems) {
                                items.items.forEach { item ->
                                    input.add(ItemDisplay(item.item, item.amount)).pad(5f).grow().row()
                                }
                            } else if (items is ConsumeLiquids) {
                                items.liquids.forEach { item ->
                                    input.add(LiquidDisplay(item.liquid, item.amount)).pad(5f).grow().row()
                                }
                            }

                        }
                    }.width(150f).padRight(5f).margin(10f)

                    t.iTableG { time ->
                        time.table {
                            it.image(IStyles.time).size(21f).expandY().bottom()
                            it.add("${ore.crftTime / 60f}秒").expandY().bottom()
                        }.grow().row()
                        time.table {
                            it.table { it1 ->
                                it1.image(IStyles.arrow).grow()
                            }.size(275f / 2, 79f / 2f).expandY().top()
                        }.grow()
                    }.padRight(5f)

                    t.table { output ->
                        output.add("输出:").pad(5f).expandX().row()
                        val output1 = ore.output
                        output1.forEach { (items, i) ->
                            output.table { ores ->
                                ores.add("${i}%").padRight(5f)
                                ores.add(ItemDisplay(items.item, items.amount)).pad(5f)
                            }.grow().row()
                        }
                    }.width(150f).margin(10f)

                }, IStyles.backgroundButton) {
                    formula = ore
                }.update {
                    val b = ore == formula
                    it.isChecked = b
                }.grow().pad(2f).row()
            }
        }

        override fun acceptItem(source: Building, item: Item): Boolean {
            var acc = false
            formula.input.select { it is ConsumeItems }.forEach { it ->
                val consumeItems = it as ConsumeItems
                consumeItems.items.forEach {
                    acc = acc || it.item == item && items.get(item) < itemCapacity
                }
            }
            return acc
        }

        override fun acceptLiquid(source: Building?, liquid: Liquid?): Boolean {
            return liquids.get(liquid) < liquidCapacity
        }

        override fun updateTile() {
            progress += getProgressIncrease(formula.crftTime)
            if (progress >= 1) {
                progress = 0f
                var cons = true
                formula.input.forEach {
                    if (it is ConsumeItems) {
                        it.items.forEach { item ->
                            cons = cons && items.has(item.item, item.amount)
                        }
                    } else if (it is ConsumeLiquids) {
                        it.liquids.forEach { item ->
                            cons = cons && (liquids.get(item.liquid) > item.amount)
                        }
                    }
                }
                if (cons) {
                    craft()
                }
            }
            dumps()
        }


        fun dumps() {
            formula.output.forEach {
                dump(it.key.item)
            }
        }


        fun craft() {
            formula.output.forEach {
                if (items.get(it.key.item) >= getMaximumAccepted(it.key.item)) return
            }
            formula.output.forEach {
                val value = it.value
                val random = ranf.random(0, 100)
                if (value > random) {
                    items.add(it.key.item, it.key.amount)
                }
            }
            formula.input.forEach { cons ->
                cons.trigger(this)
                if (cons is ConsumeLiquids) {
                    cons.liquids.forEach {
                        liquids.set(it.liquid, liquids.get(it.liquid) - it.amount)
                    }
                }
            }

        }

        override fun read(read: Reads, revision: Byte) {
            super.read(read, revision)
            formula = oreFormula.get(read.i())
        }

        override fun write(write: Writes) {
            super.write(write)
            write.i(oreFormula.indexOf(formula))
        }
    }

}

