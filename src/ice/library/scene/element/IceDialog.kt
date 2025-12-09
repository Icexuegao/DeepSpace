package ice.library.scene.element

import arc.input.KeyCode
import arc.scene.event.InputEvent
import arc.scene.event.InputListener
import arc.scene.ui.Label
import arc.scene.ui.Label.LabelStyle
import arc.scene.ui.layout.Table
import arc.util.Align
import ice.graphics.IStyles
import ice.graphics.IceColor
import ice.library.scene.ui.Dialog
import ice.library.scene.ui.iTableG
import mindustry.gen.Sounds
import mindustry.ui.Fonts

class IceDialog(title: String = "") : Dialog() {
    private var style: DialogStyle = DialogStyle().apply {
        background = IStyles.background121
        titleFont = Fonts.def
        titleFontColor = IceColor.b4
        stageBackground = null
    }
    var buttons: Table
    val title: Label = Label(title, LabelStyle(style.titleFont, style.titleFontColor))
    val titleTable = Table()
    lateinit var cont: Table

    init {
        this.title.setEllipsis(true)
        titleTable.add(this.title).expandX().fillX().minWidth(0f).row()
        titleTable.image(mindustry.gen.Tex.whiteui, IceColor.b4).growX().height(3f).pad(4f)
        root.add(titleTable).growX().row()

        setStyle(style)
        setWidth(150f)
        setHeight(150f)

        addCaptureListener(object : InputListener() {
            override fun touchDown(event: InputEvent, x: Float, y: Float, pointer: Int, button: KeyCode): Boolean {
                toFront()
                return false
            }
        })
        root.iTableG {
            cont = it
        }.row()

        root.add(Table().also { buttons = it }).fillX()

        margin(8f)

        buttons.defaults().pad(3f)

        shown { this.updateScrollFocus() }

        setFillParent(true)
        this.title.setAlignment(Align.center)

        hidden { Sounds.back.play() }
    }

    fun addCloseButton(width: Float = 210f) {
        buttons.defaults().size(width, 64f)
        buttons.button("返回", IStyles.rootCleanButton, ::hide).size(width, 64f)

        addCloseListener()
    }

    fun addCloseListener() {
        closeOnBack()
    }
}