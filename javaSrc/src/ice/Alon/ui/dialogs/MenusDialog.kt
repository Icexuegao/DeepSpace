package ice.Alon.ui.dialogs

import arc.Core
import arc.graphics.Color
import arc.scene.ui.ScrollPane
import arc.scene.ui.TextButton.TextButtonStyle
import arc.scene.ui.layout.Table
import arc.util.Align
import ice.Alon.asundry.BaseTool.ToolUi.createFlatDown
import ice.Alon.music.IceMusics.IceMusics
import mindustry.Vars
import mindustry.content.Items
import mindustry.gen.Icon
import mindustry.ui.Fonts
import java.util.*


val deepSpace = IceDialog()
var graphicsSize = if (!Vars.mobile) floatArrayOf(
    (Core.graphics.width - 20).toFloat(), (Core.graphics.height - 10).toFloat()
) else floatArrayOf(
    (Core.graphics.height - 20).toFloat(), (Core.graphics.width - 10).toFloat()
)
val deepSpaceColor = arrayOf("#C384B7", "red", "#1DFF00", "#FFECF8FF")
lateinit var cont: Table


 val createFlatDown = createFlatDown("ice-1")!!
lateinit var button: TextButtonStyle
fun intro(table: Table) {
    deepSpace.show()
    table.clear()
    table.add("DeepSpace")
}

fun load() {
    button = object : TextButtonStyle() {}.apply {
        up = createFlatDown("ice-flat-up-base1")
        over = up
        down = createFlatDown("ice-flat-down-base1")
        font = Fonts.def
        fontColor = Color.valueOf("97abb7")
        disabledFontColor = Color.gray
    }

    deepSpace.cont.setBackground(createFlatDown)
    deepSpace.cont.margin(10f, 10f, 10f, 10f)
    deepSpace.getCell(deepSpace.cont).size(graphicsSize[0], graphicsSize[1])
    Vars.ui.menufrag.addButton(
        "[${deepSpaceColor[Random().nextInt(deepSpaceColor.size)]}]DeepSpace[]", Icon.menu
    ) { intro(cont) }

    deepSpace.cont.table { t ->

        t.button({ b ->
            b.image(Icon.chartBar).left().table.add("@schedule")
        }, button) { schedule() }.pad(2f).expand().fill().row()

        t.button({ b ->
            b.image(Icon.tree).left().table.add("@tree")
        }, button) { tree() }.pad(2f).expand().fill().row()

        t.button({ b ->
            b.image(Icon.book).left().table.add("@Mode")
        }, button) {}.pad(2f).expand().fill().row()

        t.button({ b ->
            b.image(Icon.star).left().table.add("@achievement")
        }, button) {
            achievement()
        }.pad(2f).expand().fill().row()

        t.button({ b ->
            b.image(Icon.fileText).left().table.add("@logs")
        }, button) {}.pad(2f).expand().fill().row()

        t.button({ b ->
            b.image(Icon.filters).left().table.add("@settings")
        }, button) {}.pad(2f).expand().fill().row()

        t.button({ b ->
            b.image(Icon.bookOpen).left().table.add("@ExpressOne.sThanks")
        }, button) {
            if (!IceMusics.expressOne.isPlaying) {
                IceMusics.expressOne.play()
            } else {
                IceMusics.expressOne.stop()
            }
        }.pad(2f).expand().fill().row()

        t.button({ b ->
            b.image(Icon.exit).left().table.add("@close")
        }, button, ::close).pad(2f).expand().fill().row()

    }.left().width(200f).expandY().fillY()

    deepSpace.cont.table { t ->
        cont = t
    }.expand().fill()

    deepSpace.cont.table { t ->
        t.table { p ->
            p.image(Core.atlas.find("ice-deepSpace.DC差纵GZ"))
        }.grow().row()
        t.table { p ->
            p.image(Core.atlas.find("ice-flower")).expand().bottom()
        }.grow()
    }.width(200f).right().expandY().fillY()
}

/**  每个按钮按下前要执行的方法,通常用于清空其他按钮的遗留问题*/
private fun init() {
    cont.clear()
}

fun close() {
    init()
    deepSpace.hide()
    IceMusics.expressOne.stop()
}


fun schedule() {

}

fun tree() {
    init()/*

     val l = ResearchDialog()

     cont.table {
         it.add(l)
         Lines.stroke(Scl.scl(4f), Color.green)
     }
     l.show()*/
}

fun achievement() {
    init()
    val t1 = Table()
    t1.margin(10f)
    t1.table { t ->
        t.table {
            it.image(Icon.star).left().color(Color.yellow).fontScale(2f)
            it.add("@achievement").fontScale(2f)
        }.expand().fill()
    }.expandX().fillX().height(90f).row()

    t1.table { k ->
        for (i in 1 until 100) {
            k.table { b ->
                b.image(Items.coal.fullIcon).size(64f).left()
                b.button("$i") {}.left()
                b.add(
                    """  
                    we,12,32
                    32
                     """.trimIndent()
                ).wrap().grow().top().left().labelAlign(Align.topLeft)
                b.background = createFlatDown
            }.height(128f).expandX().fillX().pad(5f).margin(8f)
            if (i % 3 == 0) k.row()
        }
    }.expand().fill()
    val scrollPane = ScrollPane(t1, ScrollPane.ScrollPaneStyle().apply {})
    cont.add(scrollPane).expand().fill().height(966f)
}
