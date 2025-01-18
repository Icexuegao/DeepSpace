package ice.ui.dialogs

import arc.graphics.Color
import arc.scene.actions.Actions
import arc.scene.style.Drawable
import arc.scene.style.TextureRegionDrawable
import arc.scene.ui.Image
import arc.scene.ui.TextField
import arc.scene.ui.layout.Table
import arc.util.Log
import ice.Ice
import ice.library.tool.StringTool
import ice.type.Incident
import ice.ui.TableExtend
import ice.ui.TableExtend.addListeners
import ice.ui.TableExtend.icePane
import ice.ui.TableExtend.imageButton
import ice.ui.TableExtend.setPositions
import ice.ui.TableExtend.tableG
import ice.ui.menus.SettingValue
import ice.ui.scene.listener.DragInputListener
import ice.ui.tex.Colors
import ice.ui.tex.IceTex
import mindustry.Vars
import mindustry.ctype.UnlockableContent
import mindustry.game.Gamemode
import mindustry.game.Rules
import mindustry.gen.Icon
import mindustry.gen.Tex
import mindustry.ui.Styles
import mindustry.world.meta.BuildVisibility

object DeBugDialog {
    private var debug = Table()
    private lateinit var cont: Table
    private var index = 0
    private val mar = 4f
    private var check: String? = null
    private var checklast: String? = null
    private var windowWidth = 400f
    private lateinit var windowTable: Table
    private var windowVisibility = false

    fun show() {
        debug.visible(SettingValue::getDebugMode)
        debug.setSize(530f, windowWidth)
        Vars.ui.hudGroup.addChild(debug)
        buildPane(debug)
        buildWindow(debug)
    }


    private fun buildWindow(table: Table) {
        table.table(IceTex.pane2) { ta ->
            ta.setColor(Colors.b4)
            ta.icePane {
                cont = it
            }.grow()
            ta.setSize(windowWidth)
            ta.visible { windowVisibility }
            ta.color.a = 0f
            windowTable = ta
        }.margin(mar).grow()
    }

    private fun buildPane(table: Table) {
        val pane = Table()
        pane.setPositions(500f, 100f).setSize(130f, 250f)
        pane.add(Image(Tex.whiteui).addListeners(DragInputListener(table))).color(Colors.b4).height(40f).growX().row()
        pane.icePane {
            fun button(name: String, icon: Drawable, runnable: Runnable = Runnable {}) {
                it.image(Tex.underlineWhite, Colors.b3).height(3f).growX().row()
                it.imageButton(name, icon, IceTex.txtCleari, 20f) {
                    checklast = check
                    check = name
                    runnable.run()
                    Log.info(checklast + "" + check)
                }.height(42f).growX().row()
            }
            button("游戏模式", Icon.editSmall, DeBugDialog::gmode)
            button("解禁", Icon.cancelSmall, DeBugDialog::allBlock)
            button("解锁科技", Icon.treeSmall, DeBugDialog::unlock)
            button("锁定科技", Icon.treeSmall, DeBugDialog::clearUnlock)
            button("物品", Icon.pasteSmall, DeBugDialog::items)
            button("重置ui", Icon.homeSmall, DeBugDialog::home)
            button("传教", Icon.bookOpenSmall) {
                Incident.announce("[red]<<传教>> 你的信仰疑似有点动摇[]", 9f)
            }
            button(("清除日志"), Icon.fileTextSmall, Vars.ui.consolefrag::clearMessages)
        }.width(130f).height(211f)
        table.add(pane).expandY().top()
    }


    private fun addbutton(t: Table, content: UnlockableContent, run: Runnable) {
        index++
        t.button(
            TextureRegionDrawable(content.uiIcon), Styles.clearNonei, 24f, run
        ).size(40f).tooltip(content.localizedName)
        if (index % ((windowWidth - mar * 2) / 40).toInt() == 0) t.row()
    }

    private fun clean() {
        cont.clear()
        if (checklast == null) {
            windowVisibility = true
            windowTable.actions(
                Actions.fadeOut(0f), Actions.fadeIn(0.25f)
            )
        } else if (check != null && check==checklast) {
            windowTable.actions(Actions.fadeOut(0.25f), Actions.run {
                check = null
                checklast = null
                windowVisibility = false
            })
        }
    }

    private fun gmode() {
        clean()
        cont.tableG {
            TableExtend.addLinet(it, "游戏模式", Color.white)
            it.table { ta ->
                Gamemode.entries.forEach { m ->
                    ta.button(m.name, Styles.flatBordert) {
                        Vars.state.rules = m.apply(Rules())
                    }.grow()
                }
            }.growX()
        }
    }

    private fun items() {
        clean()
        var size = ""
        var add = true
        cont.tableG {
            TableExtend.addLinet(it, "物品", Color.white)
            it.tableG { ita ->
                Vars.content.items().forEach { item ->
                    addbutton(ita, item) {
                        if (add) {
                            Vars.player.core().items.add(item, if (StringTool.isNumeric4(size)) size.toInt() else 1000)
                        } else {
                            Vars.player.core().items.remove(
                                item, if (StringTool.isNumeric4(size)) size.toInt() else 1000
                            )
                        }
                    }
                }
            }.row()
            it.tableG { t ->
                t.button("添加", Styles.flatBordert) {
                    add = !add
                }.apply {
                    get().update {
                        get().setText(if (add) "添加" else "删除")
                    }
                }.growX()
                t.add("物品数量: ")
                t.add(TextField("1000").apply {
                    messageText = "1000"
                    update { size = text }
                })
            }
        }
        index = 0
    }

    private fun unlock() {
        clean()
        cont.tableG {
            TableExtend.addLinet(it, "解锁科技", Color.white)
            it.tableG {
                Vars.content.each { cotent ->
                    if (cotent is UnlockableContent) {
                        addbutton(it, cotent, cotent::unlock)
                    }
                }
            }
        }
        index = 0
    }

    private fun allBlock() {
        clean()
        if (SettingValue.getDebugMode()) {
            Vars.content.blocks().forEach {
                if (it.minfo.mod == Ice.ice) it.buildVisibility = BuildVisibility.shown
            }
        }
        cont.tableG {
            TableExtend.addLinet(it, "解禁方块", Color.white)
            it.tableG {
                Vars.content.blocks().forEach { block ->
                    if (block is UnlockableContent) {
                        addbutton(it, block) {
                            block.buildVisibility = BuildVisibility.shown
                            Vars.control.input.block = block
                        }
                    }
                }
            }
        }
        index = 0
    }

    private fun clearUnlock() {
        clean()
        cont.tableG {
            TableExtend.addLinet(it, "锁定科技", Color.white)
            it.tableG {
                Vars.content.each { content ->
                    if (content is UnlockableContent) {
                        addbutton(it, content, content::clearUnlock)
                    }
                }
            }
        }
        index = 0
    }

    private fun home(){
        Vars.ui.hudGroup.removeChild(debug)
        debug=Table()
        checklast=null
        check=null
        index=0
        show()
    }
}