package ice.ui.dialogs

import arc.flabel.FLabel
import arc.graphics.Color
import arc.scene.style.Drawable
import arc.scene.ui.ScrollPane
import arc.scene.ui.layout.Cell
import arc.scene.ui.layout.Table
import arc.util.Align
import ice.graphics.IceColors.getRand
import ice.library.file.BundleDetection
import ice.library.file.IceFiles
import ice.music.IceMusics
import ice.scene.MyInputListener.TouchDownInputListener
import ice.ui.DisplayName
import ice.ui.Tex.IceTex
import mindustry.Vars
import mindustry.content.Items
import mindustry.gen.Icon

class MenusDialog internal constructor() {
    private val deepSpace = IceDialog()
    private lateinit var cont: Table
    fun load() {
        deepSpace.cont.table { t ->
            fun createLeftButton(name: String, icon: Drawable, string: String, run: Runnable) {
                t.button({ it.image(icon).left().table.add(name) }, IceTex.ui按钮) { run.run() }.pad(2f).expand().fill()
                    .tooltip(string).row()
            }
            createLeftButton("@schedule", Icon.chartBar, "进度") {}
            createLeftButton("@tree", Icon.tree, "科技树") {}
            createLeftButton("@Mode", Icon.book, "模块") {}
            createLeftButton("@achievement", Icon.star, "成就", ::achievement)
            createLeftButton("@logs", Icon.fileText, "更新日志") { deepSpace.cont.cells.each { it.table.invalidate() } }
            createLeftButton("@bundle", Icon.wrench, "Bundle检验", BundleDetection::load)
            createLeftButton("@settings", Icon.filters, "配置设置") {}
            createLeftButton("@tanks", Icon.bookOpen, "感谢名单", ::tanks)
            createLeftButton("@close", Icon.exit, "关闭窗口", ::close)
        }.width(200f).grow()

        deepSpace.cont.table {
            it.table { i ->
                i.image(Items.copper.fullIcon).expand().right()
                i.add(DisplayName.massageRand)
                i.image(Items.carbide.fullIcon).expand().left()
                i.addListener(TouchDownInputListener {
                    DisplayName.flun()
                    (i.cells[1] as Cell<*>).setElement(FLabel(DisplayName.massageRand))
                })
            }.size(1280f, 60f).top().row()
            it.table { i ->
                i.add("111111111111111111")
                cont = i
            }.grow()
        }.grow()

        deepSpace.cont.table { t ->
            t.table { it.image(IceFiles.findPng("deepSpace.DC差纵GZ")).expand().top() }.grow().row()
            t.table { it.image(IceFiles.findPng("flower")).expand().bottom() }.grow()
        }.width(200f).grow()

        deepSpace.cont.cells.each {
            val t = it.get() as Table
            t.background = IceTex.background
            t.margin(10f, 10f, 10f, 10f)
        }

        Vars.ui.menufrag.addButton("[#${getRand()}]DeepSpace[]", Icon.menu, deepSpace::show)
    }


    private fun close() {
        deepSpace.hide()
        IceMusics.expressOne.stop()
    }

    private fun achievement() {
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
                        jk
                        kj
                    """.trimIndent()
                    ).wrap().grow().top().left().labelAlign(Align.topLeft)
                    b.background = IceTex.background
                }.height(128f).expandX().fillX().pad(5f).margin(8f)
                if (i % 3 == 0) k.row()
            }
        }.expand().fill()
        val scrollPane = ScrollPane(t1, ScrollPane.ScrollPaneStyle())
        deepSpace.cont.addChild(scrollPane)
    }

    private fun tanks() {
        if (!IceMusics.expressOne.isPlaying) IceMusics.expressOne.play() else {
            IceMusics.expressOne.stop()
        }
    }
}
