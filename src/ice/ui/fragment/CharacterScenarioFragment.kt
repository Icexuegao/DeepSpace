package ice.ui.fragment

import arc.Events
import arc.func.Boolp
import arc.graphics.Color
import arc.scene.Group
import arc.scene.actions.Actions
import arc.scene.event.Touchable
import arc.scene.ui.Button
import arc.scene.ui.Image
import arc.scene.ui.Label
import arc.scene.ui.TextButton
import arc.scene.ui.layout.Table
import arc.scene.ui.layout.WidgetGroup
import arc.struct.Queue
import arc.util.Scaling
import ice.audio.ISounds
import ice.graphics.Characters
import ice.graphics.IStyles
import ice.graphics.IStyles.background61
import ice.graphics.IStyles.background62
import ice.graphics.IceColor
import ice.library.scene.action.IceActions
import ice.library.scene.element.typinglabel.TLabel
import ice.library.scene.ui.icePane
import ice.ui.UI
import mindustry.game.EventType
import mindustry.ui.Fonts

object CharacterScenarioFragment {
    private var group: Group = WidgetGroup()
    private lateinit var TextPane: Table
    private lateinit var buttons: Table
    private var branchConver = BranchConver()
    private lateinit var image: Image
    private var tbs = TextButton.TextButtonStyle().apply {
        up = background61
        over = background61
        down = background62
        checked = background62
        font = Fonts.def
        fontColor = IceColor.b4
        overFontColor = IceColor.b4
        disabledFontColor = Color.gray
    }

    fun build(parent: Group) {
        group.setFillParent(true)
        group.touchable = Touchable.childrenOnly
        group.visibility = Boolp { true }
        parent.addChild(group)
    }

    private class BranchConver {
        var texts = Queue<Pair<String, () -> Unit>>()
        var temp = Queue<Pair<String, () -> Unit>>()
        var character = Characters.娜雅
        fun addFirstLast(text: String, run: () -> Unit = {}) {
            temp.add(Pair("$text ", run))
        }

        fun addFirsEnd() {
            while (texts.any()) {
                temp.add(texts.removeFirst())
            }
            texts = temp
            temp = Queue<Pair<String, () -> Unit>>()
        }

        fun addText(text: String, run: () -> Unit = {}) {
            if (text.contains("\n")) {
                text.trimIndent().split("\n").map { "$it " }.forEach(::addText)
            } else {
                texts.add(Pair("$text ", run))
            }
        }

        fun addTextButton(text: String, run: () -> Unit = {}) {
            addText(text) {
                buttons.actions(Actions.alpha(0f), Actions.alpha(1f, 2f))
                run.invoke()
            }
        }
    }

    fun addButtons(text: String, color: Color = IceColor.b4, run: () -> Unit): Button {
        image.touchable = Touchable.disabled
        val textButton = Button(tbs).apply {
            add(TLabel(text)).color(color)
        }
        textButton.tapped {
            ISounds.remainInstall.play()
            run.invoke()
            textButton.actions(
                IceActions.moveToAlphaAction(textButton.x + 300f, textButton.y, 2f, 0f), Actions.run {
                    image.touchable = Touchable.enabled
                    buttons.actions(Actions.alpha(0f, 2f), Actions.run {
                        buttons.clearChildren()})
                })
        }
        buttons.add(textButton).pad(10f).size(400f, 60f).row()
        return textButton
    }

    init {
        Events.on(EventType.ResetEvent::class.java) {
            TextPane.clear()
            branchConver = BranchConver().apply {
                addText("[#${character.color}]" + character.name + ":")
                addText("呃..你")
                addTextButton("{WAVE=0.1}为什么,{WAIT=2}我那么{SHAKE}相信{ENDSHAKE}你...") {
                    addButtons("{SHAKE}你还有何话说", IceColor.r1) {
                        addFirstLast("{SPEED=0.5}再无话说,{WAIT=1}请速动手")
                        addFirsEnd()
                    }
                    addButtons("{SHAKE}杀死她", IceColor.r1) {
                        addFirstLast("务实的选择,也是最明智的选择")
                        addFirstLast("在这里,我们都是为同一个目标而战")
                        addFirsEnd()
                    }
                    addButtons("{SHAKE}杀死她", IceColor.r1) {
                        addFirstLast("私人原因?哦,这倒让我更好奇了~")
                        addFirstLast("我真是对你越来越感兴趣了呢~")
                        addFirsEnd()
                    }
                }

                addText("嘻嘻，不试探你的小秘密咯~")
                addText("每个人都有不想说的过去,重要的是你现在站在这里,在教会,在我面前")
            }
        }
    }

    init {
        val table = Table(IStyles.background81)
        table.margin(20f)
        table.apply {
            table.table {
                it.icePane { pane ->
                    pane.top()
                    pane.left()
                    TextPane = pane
                }.grow().get().apply {
                    update {
                        amountY += 1f
                        clamp()
                    }
                }
            }.size(800f, 350f).row()
            table.image().growX().color(IceColor.b4).height(3f).row()
            table.table { t ->
                t.left()
                t.table {
                    val character = branchConver.character
                    image = Image(character.gal).apply {
                        setScaling(Scaling.fit)
                        tapped {
                            if (branchConver.texts.any()) {
                                val next = branchConver.texts.removeFirst()
                                addTLabel(next.first)
                                next.second.invoke()
                            }
                        }
                        update {
                            character.upfate(this)
                        }
                        it.add(this).grow()
                    }
                }.width(300f).growY()
                t.table {
                    buttons = it
                }.grow()
            }.height(250f).growX()
        }
        table.pack()
        table.setPosition((UI.cgwidth - table.width) / 2f, (UI.cgheight - table.height) / 2f)

        group.addChild(table)
    }

    private fun addTLabel(text: String, t: Boolean = true, color: Color = IceColor.b4) {
        val tLabel: Label = if (t) TLabel("{SPEED=0.3}$text") else Label(text)
        TextPane.add(tLabel).wrap().color(color).growX().fontScale(2f).row()
    }
}