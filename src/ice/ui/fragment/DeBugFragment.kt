package ice.ui.fragment

import arc.Events
import arc.func.Boolp
import arc.graphics.Color
import arc.scene.Group
import arc.scene.actions.Actions
import arc.scene.event.Touchable
import arc.scene.style.Drawable
import arc.scene.style.TextureRegionDrawable
import arc.scene.ui.Image
import arc.scene.ui.TextField
import arc.scene.ui.layout.Table
import arc.scene.ui.layout.WidgetGroup
import arc.struct.Seq
import ice.DFW
import ice.Ice
import ice.library.pathfindAlgorithm.PFPathFind
import ice.library.scene.listener.DragInputListener
import ice.library.scene.texs.Colors
import ice.library.scene.texs.Texs
import ice.library.type.Incident
import ice.library.util.isNumericWithSign
import ice.ui.*
import ice.vars.SettingValue
import mindustry.Vars
import mindustry.ctype.UnlockableContent
import mindustry.game.Gamemode
import mindustry.game.Rules
import mindustry.game.Team
import mindustry.gen.Icon
import mindustry.gen.Tex
import mindustry.ui.Styles
import mindustry.world.meta.BuildVisibility

object DeBugFragment {
    val debug = Table()
    private lateinit var cont: Table
    private var index = 0
    private var check: String? = null
    private var windowWidth = 400f
    var group: Group = WidgetGroup(debug).apply {
        name = "debug"
        setFillParent(true)
        touchable = Touchable.childrenOnly
        visibility = Boolp { Vars.ui.hudfrag.shown && SettingValue.debugMode }
    }

    fun build(parent: Group) {
        parent.addChild(group)
        debug.setSize(530f, windowWidth)
        buildPane(debug)
        buildWindow(debug)
    }

    private fun buildWindow(table: Table) {
        var windowVisibility = false
        var contvisibe = false
        table.table(Texs.background41) { ta ->
            ta.setColor(Colors.b4)
            ta.icePane {
                cont = it
            }.grow().visible {
                contvisibe
            }
            ta.setSize(windowWidth)

            debug.update {
                if (check == null) {
                    if (!windowVisibility) return@update
                    windowVisibility = false
                    ta.actions(Actions.fadeOut(0.25f), Actions.run { contvisibe = false })
                } else {
                    if (windowVisibility) return@update
                    windowVisibility = true
                    ta.actions(Actions.run { contvisibe = true }, Actions.fadeIn(0.25f))
                }
            }
            ta.color.a = 0f
        }.margin(4f).grow()
    }

    private fun buildPane(table: Table) {
        val pane = Table()
        pane.setPositions(500f, 100f).setSize(130f, 250f)
        pane.add(Image(Tex.whiteui).addListeners(DragInputListener(table))).color(Colors.b4).height(40f).growX()
            .row()
        pane.icePane {
            fun button(name: String, icon: Drawable, runnable: Runnable = Runnable {}) {
                it.image(Tex.underlineWhite, Colors.b3).height(3f).growX().row()
                it.imageButton(name, icon, Texs.txtCleari, 20f) {
                    check = when (check) {
                        name -> null
                        null -> name
                        else -> name
                    }
                    cont.clear()
                    runnable.run()
                }.height(42f).growX().row()
            }
            button("游戏模式", Icon.editSmall, ::gmode)
            button("队伍", Icon.adminSmall, ::team)
            button("解禁", Icon.cancelSmall, ::allBlock)
            button("科技", Icon.treeSmall, ::unlock)
            button("物品", Icon.pasteSmall, ::items)
            button("清除日志", Icon.fileTextSmall, Vars.ui.consolefrag::clearMessages)
            button("传教", Icon.bookOpenSmall) {
                Incident.announce("[red]<<传教>> 你的信仰疑似有点动摇[]", 9f)
            }
            button("剧情", Icon.bookOpenSmall) {
                ScenarioFragment.flun()
            }
            button("人称", Icon.bookOpenSmall) {
                ConversationFragment.juqing = Seq<String>().apply {
                    """
        我喜欢你
        你喜欢我
        444444444444444444
        55555555555555555555
        66666666666666666666
        我不喜欢你
        你不喜欢我
    """.trimIndent() .split("\n").forEach {
                        add(it)
                    }
                }.iterator()
            }
            button("血肉文本", Icon.fileTextSmall) {
                FleshFragment.addText()
            }
            button("游戏胜利", Icon.bookOpenSmall) {
                Vars.state.won = true
                Vars.state.rules.winWave
                Vars.state.rules.waves = false
                Vars.state.gameOver = true
            }
            button("df", Icon.up) {
                VoiceoverFragment.flun()
            }
            button("df", Icon.units){
                val dfw = DFW()
                dfw.set(8*50f,8*50f)
                dfw.add()
            }


            button("df", Icon.units){
                val pfPathFind = PFPathFind(Vars.world.tile(1, 1), Vars.world.tile(100, 100))
                Events.run(mindustry.game.EventType.Trigger.draw){
                    pfPathFind.draw()
                }
                table.update {
                    table.update {
                        pfPathFind.update()
                    }
                }
            }
            button("Df", Icon.bookOpenSmall){
            }
        }.width(130f).height(211f)
        table.add(pane).expandY().top()
    }

    private fun addbutton(t: Table, content: UnlockableContent, run: Runnable) {
        index++
        t.button(TextureRegionDrawable(content.uiIcon), Texs.button, 24f, run).size(40f).pad(2f)
            .tooltip(content.localizedName)
        if (index % 8 == 0) t.row()
    }

    private fun gmode() {
        cont.iTableG {
            addLinet(it, "游戏模式", Color.white)
            it.table { ta ->
                Gamemode.entries.forEach { m ->
                    ta.button(m.name, Styles.flatBordert) {
                        Vars.state.rules = m.apply(Rules())
                    }.grow()
                }
            }.growX()
        }
    }

    private fun team() {
        index = 0
        cont.iTableG {
            Team.all.forEach { t ->
                index++
                it.button(t.name) {
                    Vars.player.team(t)
                }.size(100f)
                if (index % 4 == 0) it.row()
            }
        }
    }

    private fun items() {
        var size = ""
        var add = true
        cont.iTableG {
            addLinet(it, "物品", Color.white)
            it.iTableG { ita ->
                Vars.content.items().forEach { item ->
                    addbutton(ita, item) {
                        if (add) {
                            Vars.player.core().items.add(item, if (size.isNumericWithSign()) size.toInt() else 1000)
                        } else {
                            Vars.player.core().items.remove(item,
                                if (size.isNumericWithSign()) size.toInt() else 1000)
                        }
                    }
                }
            }.row()
            it.iTableG { t ->
                t.button("添加", Styles.flatBordert) {
                    add = !add
                }.apply {
                    get().update {
                        get().setText(if (add) "添加" else "删除")
                    }
                }.growX().height(45f)
                t.add("物品数量: ")
                t.add(TextField("1000").apply {
                    messageText = "1000"
                    update { size = text }
                })
            }.row()
            it.iTableG { t ->
                t.button("添加全部", Styles.flatBordert) {
                    Vars.content.items().forEach { item ->
                        Vars.player.core()?.items?.add(item, 2000)
                    }
                }.growX().pad(2f).height(45f).row()
                t.button("清除全部", Styles.flatBordert) {
                    Vars.player.core()?.items?.clear()
                }.growX().pad(2f).height(45f).row()
            }.row()
        }
        index = 0
    }

    private fun unlock() {
        cont.iTableG { ta ->
            ta.iTableG {
                it.button("解锁全部", Styles.flatBordert) {
                    Vars.content.each { cotent ->
                        (cotent as? UnlockableContent)?.unlock()
                    }
                }.size(160f, 45f)
                it.button("锁定全部", Styles.flatBordert) {
                    Vars.content.each { cotent ->
                        (cotent as? UnlockableContent)?.clearUnlock()
                    }
                }.size(160f, 45f).row()
            }.row()
            addLinet(ta, "科技", Color.white)
            ta.iTableG {
                Vars.content.each { cotent ->
                    if (cotent is UnlockableContent) {
                        addbutton(it, cotent) {
                            if (cotent.locked()) {
                                cotent.unlock()
                            } else {
                                cotent.clearUnlock()
                            }
                        }
                    }
                }
            }
        }
        index = 0
    }

    private fun allBlock() {
        Vars.content.blocks().forEach {
            if (it.minfo.mod == Ice.ice) it.buildVisibility = BuildVisibility.shown
        }
        cont.iTableG {
            addLinet(it, "解禁方块", Color.white)
            it.iTableG {
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
}