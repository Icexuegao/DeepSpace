package ice.ui.dialog

import arc.graphics.Color
import arc.scene.style.TextureRegionDrawable
import arc.scene.ui.Button
import arc.scene.ui.Image
import arc.scene.ui.Label
import arc.scene.ui.TextButton
import arc.scene.ui.layout.Table
import arc.struct.OrderedMap
import arc.struct.Seq
import arc.util.Scaling
import ice.library.scene.element.IceDialog
import ice.library.scene.tex.Colors
import ice.library.scene.tex.IStyles
import ice.library.baseContent.BaseContentSeq
import ice.ui.*
import mindustry.ctype.UnlockableContent
import mindustry.type.Item
import mindustry.type.Liquid
import mindustry.world.meta.Stat
import mindustry.world.meta.StatValue

object DataDialog {
    val cont = MenusDialog.cont
    var cContents = Button.物品
    var cContent: UnlockableContent = Button.物品.content.first()
    var searchField = ""
    lateinit var flun: () -> Unit
    fun show() {
        cont.iTableGX { ta ->
            Button.entries.forEach {
                val textButton = TextButton(it.name, IStyles.button1)
                textButton.changed {
                    cContents = it
                }
                textButton.update {
                    textButton.isChecked = cContents == it
                }
                ta.add(textButton).pad(1f).grow()
            }
        }.height(60f).row()
        cont.add(Image(IStyles.whiteui)).color(Colors.b1).height(3f).growX().row()
        cont.iTableG { ta ->
            ta.iTableGY {
                it.iTableGX { search ->
                    search.image(IStyles.search).size(33f).padRight(8f)
                    search.field("") { s ->
                        searchField = s
                        flun()
                    }.width(300f)
                    val button = Button(IStyles.button).apply {
                        add("?")

                        changed {
                            IceDialog("介绍").apply {
                                cont.add($$"""
                                默认搜索name和localizedName
                                加[#]搜索简介
                                加[$]搜索吐槽
                                例如: #矿石 $恶心
                            """.trimIndent()).color(Colors.b4)
                                addCloseButton()
                            }.show()
                        }
                    }
                    search.add(button).size(40f).pad(8f)
                }.height(60f).row()
                it.iPaneG { p ->
                    p.top()
                    var tmp = cContents
                    p.setRowsize(5)
                    flun = {
                        p.clear()
                        cContents.content.select { it ->
                            if (searchField.isNotEmpty()) {
                                val substring = searchField.substring(1)
                                when (searchField[0]) {
                                    '#' -> {
                                        it.description ?: return@select false
                                        return@select it.description.contains(substring)
                                    }

                                    '$' -> {
                                        it.details ?: return@select false
                                        return@select it.details.contains(substring)

                                    }

                                    else -> {
                                        return@select (it.name.contains(searchField) || it.localizedName.contains(
                                            searchField))

                                    }
                                }
                            }
                            return@select true
                        }.forEach { content ->
                            p.button(TextureRegionDrawable(content.uiIcon), IStyles.button, 40f) {
                                cContent = content
                            }.size(60f).pad(2f).margin(5f).itooltip(content.localizedName)
                        }
                    }
                    flun()
                    p.update {
                        if (tmp != cContents) {
                            tmp = cContents
                            flun()
                        }
                    }
                }
            }.width(400f)

            ta.add(Image(IStyles.whiteui)).color(Colors.b1).width(3f).growY()
            ta.iPaneG { p ->
                var tmp = cContent
                val r = {
                    p.clear()
                    val background31 = IStyles.background31
                    p.iTableGX {
                        it.iTableGY(background31) { it1 ->
                            val color = unlockableContentColor(cContent)
                            it1.iTableG { it2 ->
                                val d = Image(TextureRegionDrawable(cContent.uiIcon), Scaling.bounded)

                                it2.add(d).expand()
                                it2.add(cContent.localizedName).pad(2f).color(color).grow()
                            }.row()

                            it1.add(cContent.description).pad(2f).growX().color(color).height(150f).wrap().row()
                            it1.add(cContent.details).pad(2f).growX().color(color).height(150f).wrap().row()
                        }.margin(22f).width(300f)
                        it.iTableG(background31) { it2 ->
                            it2.icePane { it1 ->
                                it1.add(cContent.name).color(Color.valueOf("b7e1fb")).padBottom(3f).row()
                                val stats = cContent.stats
                                cContent.checkStats()
                                stats.toMap().keys().forEach { cat ->
                                    val map: OrderedMap<Stat, Seq<StatValue>> = stats.toMap().get(cat)
                                    if (map.size == 0) return@forEach
                                    if (stats.useCategories) {
                                        it1.add(cat.localized()).color(Color.valueOf("b7e1fb")).padTop(5f).fillX()
                                        it1.row()
                                    }

                                    for (stat in map.keys()) {
                                        it1.table { inset: Table ->
                                            inset.left()
                                            inset.add(stat.localized() + ": ").left().top()
                                            val arr = map[stat]
                                            for (value in arr) {
                                                value.display(inset)
                                                val element = inset.children[0]
                                                if (element is Label) {
                                                    element.color.set(Colors.b4)
                                                }
                                                inset.add().size(10f)
                                            }
                                        }.fillX().pad(1f).padLeft(10f)
                                        it1.row()
                                    }
                                }
                            }
                        }.margin(22f)
                    }.height(500f).row()

                    p.iTableG(background31) {}.margin(22f)
                }
                r()
                p.update {
                    if (tmp != cContent) {
                        tmp = cContent
                        r()
                    }
                }
            }
        }
    }

    fun showBlock(block: UnlockableContent) {
        MenusDialog.show()
        cContent = block
        MenusDialog.button = MenusDialog.MeunsButton.数据
        show()
    }

    fun unlockableContentColor(content: UnlockableContent): Color {
        return when (content) {
            is Item -> content.color
            is Liquid -> content.color
            else -> {
                Colors.b4
            }
        }
    }

    enum class Button(val content: Seq<UnlockableContent>) {
        物品(Seq<UnlockableContent>().addAll(BaseContentSeq.items)),
        流体(Seq<UnlockableContent>().addAll(BaseContentSeq.liquids)),
        建筑(Seq<UnlockableContent>().addAll(BaseContentSeq.blocks)),
        状态(Seq<UnlockableContent>().addAll(BaseContentSeq.status)),
        单位(Seq<UnlockableContent>().addAll(BaseContentSeq.units)),/* 天气(ContentType.weather),
        战役(ContentType.sector),
        星球(ContentType.planet)*/
    }
}


