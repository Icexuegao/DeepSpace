package ice.Alon.ui

import arc.Core
import arc.flabel.FLabel
import arc.graphics.Color
import arc.input.KeyCode
import arc.scene.event.InputEvent
import arc.scene.event.InputListener
import arc.scene.ui.TextButton.TextButtonStyle
import arc.scene.ui.layout.Table
import ice.Alon.asundry.BaseTool.ToolUi
import ice.Alon.content.items.IceItems
import mindustry.Vars
import mindustry.gen.Icon
import mindustry.gen.Sounds
import mindustry.ui.Fonts
import java.util.*

val deepSpace = IceDialog()

fun init() {
    val sound = object : InputListener() {
        val l = Sounds.breaks
        override fun touchDown(event: InputEvent?, x: Float, y: Float, pointer: Int, button: KeyCode?): Boolean {
            l.play()
            return super.touchDown(event, x, y, pointer, button)
        }
    }
    val button = object : TextButtonStyle() {}.apply {
        over = ToolUi.createFlatDown("ice-do")
        up = ToolUi.createFlatDown("ice-de")
        down = up
        font = Fonts.def
        fontColor = Color.valueOf("97abb7")
        disabledFontColor = Color.gray
    }
    val f = object : TextButtonStyle() {}.apply {
        over = ToolUi.createFlatDown("ice-flat-down-base")
        up = ToolUi.createFlatDown("ice-flat-down-base1")
        down = up
        font = Fonts.def
        fontColor = Color.valueOf("97abb7")
        disabledFontColor = Color.gray
    }
    val createFlatDown = ToolUi.createFlatDown("ice-1")
    fun k(table: Table) {
        table.background = createFlatDown
    }
    deepSpace.cont.setBackground(createFlatDown)
    deepSpace.cont.margin(10f, 10f, 10f, 10f)
    deepSpace.getCell(deepSpace.cont).size((Core.graphics.width - 20).toFloat(), (Core.graphics.height - 10).toFloat())
    deepSpace.cont.table { t ->
        t.addListener(sound)
        t.table { p ->
            p.image(IceItems.leadIngot.fullIcon).left()
            p.button({ b ->
                b.setStyle(f)
                b.image(Icon.copy).left()
                b.add("@close")
            }) { deepSpace.hide() }
            k(p)
        }.pad(2f).fill().expand().row()

        t.table { p ->
            p.image(IceItems.copperIngot.fullIcon).left()
            p.button({ b ->
                b.setStyle(f)
                b.image(Icon.copy).left()
                b.add("@close")
            }) { deepSpace.hide() }
            k(p)
        }.pad(2f).fill().expand().row()

        t.table { p ->
            p.margin(10f)
            p.button({ b ->
                b.setStyle(f)
                b.image(Icon.settings).left()
                b.add("@settings")
            }) { deepSpace.hide() }.fill().expand()
            k(p)
        }.pad(2f).fill().expand().row()

        t.button({ b ->
            b.setStyle(button)
            b.image(Icon.settings).left()
            b.add("@settings")
        }) {}.pad(2f).fill().expand().row()

        t.button({ b ->
            b.setStyle(button)
            b.image(Icon.bookOpen).left()
            b.add("@ExpressOne.sThanks")
        }) { }.pad(2f).fill().expand().row()

        t.button({ b ->
            b.setStyle(button)
            b.image(Icon.exit).left()
            b.add("@close")
        }) { deepSpace.hide() }.pad(2f).fill().expand().row()


    }.left().width(200f).fillY().expandY()

    deepSpace.cont.table { t ->
        t.add(
            FLabel(
                """
                    eeeeeeeeeeeeeeeeeeeeeeeee
                    """.trimIndent()
            )
        ).row()
        t.button("1234") {}.table.addListener(sound)
    }.fill().expand()

    deepSpace.cont.table { t ->
        t.table { p ->
            p.image(Core.atlas.find("ice-hua"))
        }.expandY().bottom()
    }.width(333f).right().fillY()


    val color = arrayOf("#C384B7", "red")
    Vars.ui.menufrag.addButton(
        "[" + color[Random().nextInt(color.size)] + "]" + "DeepSpace[]", Icon.book
    ) { deepSpace.show() }

}