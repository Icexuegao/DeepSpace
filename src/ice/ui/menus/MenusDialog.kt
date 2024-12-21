package ice.ui.menus

import arc.flabel.FLabel
import arc.scene.Scene
import arc.scene.style.Drawable
import arc.scene.ui.Image
import arc.scene.ui.ScrollPane
import arc.scene.ui.layout.Table
import arc.util.Align
import ice.graphics.IceColors
import ice.library.file.BundleDetection
import ice.music.IceMusics
import ice.ui.dialogs.IceDialog
import ice.ui.tex.IceTex
import ice.ui.tex.IceTex.deepSpaceVer
import mindustry.Vars
import mindustry.content.Items
import mindustry.gen.Icon
import mindustry.ui.Styles

object MenusDialog {
    fun show() {
        load()
        Vars.ui.menufrag.addButton("[#${IceColors.getRand()}]DeepSpace[]", Icon.menu, deepSpace::toggle)
    }

    val deepSpace = object : IceDialog() {
        override fun show(stage: Scene): IceDialog {
            load()
            if (SettingValue.menuMusic) IceMusics.get("title").play()
            return super.show(stage)
        }

        override fun toggle() {
            cont.clear()
            super.toggle()
        }
    }
    private lateinit var cont: Table


    fun load() {
        deepSpace.cont.clear()
        deepSpace.cont.table(IceTex.background) {
            it.add("May we love each other today").color(IceColors.b4)
        }.height(60f).growX().row()
        deepSpace.cont.table { table ->

            table.table { t ->
                t.margin(10f, 10f, 10f, 10f)
                fun createLeftButton(name: String, icon: Drawable, string: String, run: Runnable) {
                    t.button(
                        { it.image(icon).color(IceColors.b5).left().table.add(name).color(IceColors.b5) },
                        IceTex.rootButton
                    ) { run.run() }.pad(2f).expand().fill().tooltip(string).row()
                }
                createLeftButton("@schedule", Icon.chartBar, "进度") {}
                createLeftButton("@tree", Icon.tree, "科技树") {}
                createLeftButton("@data", Icon.book, "数据") {}
                createLeftButton("@achievement", Icon.star, "成就", MenusDialog::achievement)
                createLeftButton("@logs", Icon.fileText, "更新日志") {}
                createLeftButton("@bundle", Icon.wrench, "Bundle检验", BundleDetection::load)
                createLeftButton("@settings", Icon.filters, "配置设置") { SettingDialog.set(cont) }
                createLeftButton("@tanks", Icon.bookOpen, "感谢名单", MenusDialog::tanks)
                createLeftButton("@close", Icon.exit, "关闭窗口", MenusDialog::close)
            }.width(200f).growY()

            table.table {
                it.table { i ->
                    i.add("111111111111111111").row()
                    cont = i.margin(10f)
                }.grow()
            }.grow()

            table.table { t ->
                t.margin(10f, 10f, 10f, 10f)
                t.table { it.image(deepSpaceVer).color(IceColors.b5).expand().top() }.grow().row()
                t.table { it.add(Image(IceTex.flower)).color(IceColors.b5).expand().bottom() }.grow()
            }.width(200f).growY()
            table.cells.each {
                val t = it.get() as Table
                t.background = IceTex.background
            }
        }.grow().row()
        deepSpace.cont.setScale(1f, 1f)

    }


    private fun close() {
        IceMusics.get("thanks").stop()
        IceMusics.get("title").stop()
        deepSpace.hide()
    }

    private fun achievement() {
        cont.clear()
        val t1 = Table()
        val scr = ScrollPane(t1, Styles.noBarPane)

        cont.add(scr).grow()
        t1.table { t ->
            t.table {
                it.image(Icon.star).left().color(IceColors.b5).fontScale(2f)
                it.add(FLabel("@achievement")).fontScale(2f)
            }.grow()
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
        }.grow()
    }

    private fun tanks() {
    }
}
