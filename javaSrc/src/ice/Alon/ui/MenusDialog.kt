package ice.Alon.ui

import arc.Core
import arc.flabel.FLabel
import arc.graphics.Color
import arc.scene.ui.TextButton.TextButtonStyle
import arc.scene.ui.layout.Table
import ice.Alon.asundry.BaseTool.ToolUi
import ice.Alon.content.items.IceItems
import ice.Alon.music.IceMusics.IceMusics
import mindustry.Vars
import mindustry.gen.Icon
import mindustry.ui.Fonts
import java.util.*


val deepSpace = IceDialog()
var pcGraphicsSize = floatArrayOf((Core.graphics.width - 20).toFloat(), (Core.graphics.height - 10).toFloat())
var andriodGraphicsSize = floatArrayOf((Core.graphics.height - 20).toFloat(), (Core.graphics.width - 10).toFloat())

var graphicsSize = if (!Vars.mobile) floatArrayOf(pcGraphicsSize[0], pcGraphicsSize[1]) else floatArrayOf(
    andriodGraphicsSize[0], andriodGraphicsSize[1]
)


fun init() {
    val button = object : TextButtonStyle() {}.apply {
        up = ToolUi.createFlatDown("ice-flat-up-base1")
        over = up
        down = ToolUi.createFlatDown("ice-flat-down-base1")
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
    deepSpace.getCell(deepSpace.cont).size(graphicsSize[0], graphicsSize[1])
    deepSpace.cont.table { t ->
        t.table { p ->
            p.image(IceItems.leadIngot.fullIcon).left()
            p.button({ b ->
                b.setStyle(button)
                b.image(Icon.copy).left()
                b.add("@close")
            }) { deepSpace.hide() }
            k(p)
        }.pad(2f).expand().fill().row()

        t.table { p ->
            p.image(IceItems.copperIngot.fullIcon).left()
            p.button({ b ->
                b.setStyle(button)
                b.image(Icon.copy).left()
                b.add("@close")
            }) { deepSpace.hide() }
            k(p)
        }.pad(2f).expand().fill().row()

        t.table { p ->
            p.margin(10f)
            p.button({ b ->
                b.setStyle(button)
                b.image(Icon.settings).left()
                b.add("@settings")
            }) { deepSpace.hide() }.fill().expand()
            k(p)
        }.pad(2f).expand().fill().row()

        t.button({ b ->
            b.setStyle(button)
            b.image(Icon.settings).left()
            b.add("@settings")
        }) {}.pad(2f).expand().fill().row()

        t.button({ b ->
            b.setStyle(button)
            b.image(Icon.bookOpen).left()
            b.add("@ExpressOne.sThanks")
        }) {
            if (!IceMusics.expressOne.isPlaying) {
                IceMusics.expressOne.play()
            }
        }.pad(2f).expand().fill().row()

        t.button({ b ->
            b.setStyle(button)
            b.image(Icon.exit).left()
            b.add("@close")
        }) {
            deepSpace.hide()
            IceMusics.expressOne.stop()
        }.pad(2f).expand().fill().row()


    }.left().width(200f).expandY().fillY()

    deepSpace.cont.table { t ->
        t.add(
            FLabel(
                """{rainbow}
                    eeeeeeeeeeeeeeeeeeeeeeeee
                    """.trimIndent()
            )
        ).row()
        t.button("1234") {}
    }.expand().fill()

    deepSpace.cont.table { t ->
        t.table { p ->
            p.image(Core.atlas.find("ice-flower"))
        }.expandY().bottom()
    }.width(333f).right().expandY().fillY()


    val color = arrayOf("#C384B7", "red")
    Vars.ui.menufrag.addButton(
        "[" + color[Random().nextInt(color.size)] + "]" + "DeepSpace[]", Icon.book
    ) { deepSpace.show() }

}