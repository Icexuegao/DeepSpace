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
import ice.core.SettingValue
import ice.graphics.IStyles
import ice.graphics.IceColor
import ice.library.scene.element.IceDialog
import ice.library.scene.ui.*
import ice.world.content.BaseContentSeq
import ice.world.meta.IceStats
import mindustry.ctype.UnlockableContent
import mindustry.gen.Icon
import mindustry.type.Item
import mindustry.type.Liquid
import mindustry.world.meta.Stat
import mindustry.world.meta.StatValue

object DataDialog : BaseMenusDialog(IceStats.数据.localized(), Icon.book) {
    private var cContents = Button.物品
    var cContent: UnlockableContent = Button.物品.content.first()
    var searchField = ""
    lateinit var flun: () -> Unit
    override fun build() {
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
        cont.add(Image(IStyles.whiteui)).color(IceColor.b1).height(3f).growX().row()
        cont.iTableG { ta ->
            ta.table {
                it.iTableGX { search ->
                    search.image(IStyles.search).color(IceColor.b4).size(33f).padLeft(15f).padRight(8f)
                    search.field(searchField) { s ->
                        searchField = s
                        flun()
                    }.growX()
                    val button = Button(IStyles.button).apply {
                        add("?")
                        changed {
                            IceDialog("介绍").apply {
                                root.addCR("默认搜索name和localizedName")
                                root.addCR("加[#]搜索简介")
                                root.addCR("加[%]搜索吐槽")
                                root.addCR("例如: #矿 %饿")
                                addCloseButton()
                            }.show()
                        }
                    }
                    search.add(button).size(40f).pad(8f)
                }.minHeight(60f).row()
                it.iPaneG { p ->
                    p.top()
                    var tmp = cContents
                    p.setRowsize(5)
                    flun = {
                        p.clear()
                        cContents.content.select {
                            if (searchField.isNotEmpty()) {
                                val substring = searchField.substring(1)
                                when (searchField[0]) {
                                    '#' -> {
                                        it.description ?: return@select false
                                        return@select it.description.contains(substring)
                                    }

                                    '%' -> {
                                        it.details ?: return@select false
                                        return@select it.details.contains(substring)
                                    }

                                    else -> {
                                        return@select (it.name.contains(searchField) || it.localizedName.contains(
                                            searchField
                                        ))
                                    }
                                }
                            }
                            if (SettingValue.启用调试模式 && it.isHidden) return@select false
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
            }.minWidth(350f).growY()
            ta.add(Image(IStyles.whiteui)).color(IceColor.b1).width(3f).growY()
            ta.iTableG { p ->
                var tmp = cContent
                val r = {
                    p.clear()
                    val background31 = IStyles.background31
                    p.iTableG {
                        it.iTableG(background31) { it1 ->
                            val color = unlockableContentColor(cContent)
                            it1.iTableG { it2 ->
                                it2.image(cContent.uiIcon).size(112f).scaling(Scaling.fit).row()
                                it2.add(cContent.localizedName).fontScale(1.5f).pad(2f).color(color)
                            }.row()

                            it1.add(cContent.description).pad(2f).growX().color(color).height(150f).wrap().row()
                            it1.add(cContent.details).pad(2f).growX().color(color.cpy().a(0.5f)).height(150f).wrap().row()
                        }.margin(22f).minWidth(150f)
                        it.iTableG(background31) { it2 ->
                            it2.icePane { it1 ->
                                it1.add(cContent.name).color(Color.valueOf("b7e1fb")).padBottom(3f).row()
                                val stats = cContent.stats
                                cContent.checkStats()
                                val toMap = stats.toMap()
                                toMap.keys().forEach { cat ->
                                    val map: OrderedMap<Stat, Seq<StatValue>> = toMap.get(cat)
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
                                                    element.color.set(IceColor.b4)
                                                }
                                                inset.add().size(10f)
                                            }
                                        }.fillX().pad(1f).padLeft(10f)
                                        it1.row()
                                    }
                                }
                            }
                        }.margin(22f).minWidth(400f)
                    }.minHeight(300f).row()

                    p.iTableG(background31) {}.margin(22f).minHeight(350f)
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
        if (!MenusDialog.isShown()) MenusDialog.show()
        cContent = block
        MenusDialog.button = this
        hide()
        build()
    }

    fun unlockableContentColor(content: UnlockableContent): Color {
        return when (content) {
            is Item -> content.color
            is Liquid -> content.color
            else -> {
                IceColor.b4
            }
        }
    }

    private enum class Button(val content: Seq<UnlockableContent>) {
        物品(Seq<UnlockableContent>().addAll(BaseContentSeq.items)), 流体(Seq<UnlockableContent>().addAll(BaseContentSeq.liquids)), 建筑(Seq<UnlockableContent>().addAll(BaseContentSeq.blocks)), 状态(Seq<UnlockableContent>().addAll(BaseContentSeq.status)), 单位(Seq<UnlockableContent>().addAll(BaseContentSeq.units)),/* 天气(ContentType.weather),
        战役(ContentType.sector),
        星球(ContentType.planet)*/
    }
}


