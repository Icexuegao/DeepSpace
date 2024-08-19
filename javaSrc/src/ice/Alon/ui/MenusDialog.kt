package ice.Alon.ui

import arc.Core
import arc.flabel.FLabel
import arc.graphics.Color
import arc.input.KeyCode
import arc.scene.event.InputEvent
import arc.scene.event.InputListener
import arc.scene.ui.Button
import arc.scene.ui.TextButton.TextButtonStyle
import arc.scene.ui.layout.Table
import ice.Alon.asundry.BaseTool.ToolUi
import ice.Alon.content.items.IceItems
import mindustry.Vars
import mindustry.gen.Icon
import mindustry.gen.Sounds
import mindustry.ui.Fonts
import java.util.*


fun init() {
    val f: TextButtonStyle = object : TextButtonStyle() {
        init {
            over = ToolUi.createFlatDown("ice-flat-down-base")
            up = ToolUi.createFlatDown("ice-flat-down-base1")
            down = up
            font = Fonts.def
            fontColor = Color.valueOf("97abb7")
            disabledFontColor = Color.gray
        }
    }
    val createFlatDown = ToolUi.createFlatDown("ice-1")
    val h = IceDialog()
    h.cont.setBackground(createFlatDown)
    h.cont.margin(10f, 10f, 10f, 10f)
    h.getCell(h.cont).size((Core.graphics.width - 20).toFloat(), (Core.graphics.height - 10).toFloat())
    h.cont.table { t: Table ->

        t.table { p: Table ->
            p.image(IceItems.leadIngot.fullIcon).left()
            p.button({ b: Button ->
                b.setStyle(f)
                b.image(Icon.copy).left()
                b.add("@close")
            }) { h.hide() }
            p.background = createFlatDown
        }.pad(2f).fill().expand().row()
        t.table { p: Table ->
            p.image(IceItems.copperIngot.fullIcon).left()
            p.button({ b: Button ->
                b.setStyle(f)
                b.image(Icon.copy).left()
                b.add("@close")
            }) { h.hide() }
            p.background = createFlatDown
        }.pad(2f).fill().expand().row()

        t.table { p: Table ->
            p.image(IceItems.sphalerite.fullIcon).left()
            p.button({ b: Button ->
                b.setStyle(f)
                b.image(Icon.copy).left()
                b.add("@close")
            }) { h.hide() }
            p.background = createFlatDown
        }.pad(2f).fill().expand().row()
    }.left().width(200f).fillY().expandY()

    h.cont.table { t: Table ->
        t.add(
            FLabel(
                """
                    ddfeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeee
                    eeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeee
                    eeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeee
                    eeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeee
                    eeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeee
                    eeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeee
                    eeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeee
                    eeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeee
                    eeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeee
                    eeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeee
                    eeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeee
                    eeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeee
                    eeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeee
                    eeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeee
                    eeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeee
                    """.trimIndent()
            )
        )
        t.button("1234") {}.table.addListener(object : InputListener() {
            val l = Sounds.breaks
            override fun touchDown(
                event: InputEvent, x: Float, y: Float, pointer: Int, button: KeyCode
            ): Boolean {
                l.play()
                return super.touchDown(event, x, y, pointer, button)
            }

            override fun touchUp(
                event: InputEvent, x: Float, y: Float, pointer: Int, button: KeyCode
            ) {
                l.stop()
                super.touchUp(event, x, y, pointer, button)
            }
        })
    }.fill().expand()
    val color = arrayOf("#C384B7", "red")
    Vars.ui.menufrag.addButton(
        "[" + color[Random().nextInt(color.size)] + "]" + "DeepSpace[]", Icon.book
    ) { h.show() }

}