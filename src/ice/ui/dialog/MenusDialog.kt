package ice.ui.dialog

import arc.graphics.g2d.Draw
import arc.scene.ui.Image
import arc.scene.ui.layout.Table
import ice.graphics.IStyles
import ice.graphics.IStyles.deepSpaceVer
import ice.graphics.IceColor
import ice.library.scene.ui.Dialog
import ice.library.scene.ui.esc
import ice.library.scene.ui.iPaneG
import ice.ui.dialog.research.ResearchDialog
import ice.world.meta.IceStats
import mindustry.gen.Icon

object MenusDialog : Dialog() {
    const val backMargin = 10f
    val back = IStyles.background11
    var button: BaseMenusDialog? = null
    lateinit var conts: Table

    init {
        clearChildren()
        setFillParent(true)
        table(back) { table ->
            table.table(back) {
                it.add("May we love each other today").color(IceColor.b4).get().setFontScale(1.3f)
            }.margin(10f).height(60f).growX().row()

            table.table { it1 ->
                val middle = object : Table(back) {
                    override fun drawBackground(x: Float, y: Float) {
                        if (background == null) return
                        val color = this.color
                        Draw.color(color.r, color.g, color.b, 1 * parentAlpha)
                        background.draw(x, y, width, height)
                    }
                }.let {
                    conts = it
                    conts.margin(backMargin)
                }

                it1.table(back) {
                    it.margin(backMargin)
                    //排序
                    run {
                        ResearchDialog
                        DataDialog
                        AchievementDialog
                        RemainsDialog
                        LogDialog
                        SettingDialog
                        ThankDialog
                    }
                    it.iPaneG { pan ->
                        pan.top()
                        BaseMenusDialog.dalogs.forEach { mb ->
                            pan.button({ b ->
                                b.image(mb.icon).color(IceColor.b5).table.add(mb.name).color(IceColor.b5)
                            }, IStyles.rootButton) {
                                if (button == mb) return@button
                                button = mb
                                mb.hide()
                                mb.build()
                            }.update { b ->
                                b.isChecked = button == mb
                            }.pad(2f).growX().minHeight(100f).maxHeight(120f).row()
                        }
                        pan.button({ b ->
                            b.image(Icon.exit).color(IceColor.b5).table.add(IceStats.关闭.localized()).color(IceColor.b5)
                        }, IStyles.rootCleanButton) {
                            hide()
                        }.pad(2f).growX().minHeight(100f).maxHeight(120f).row()
                    }
                }.width(200f).margin(10f).growY()
                button?.build()
                it1.add(middle).grow().margin(backMargin)
                it1.table(back) { it2 ->
                    it2.table { it.image(deepSpaceVer).color(IceColor.b5).expand().top() }.grow().row()
                    it2.table { it.add(Image(IStyles.flower)).color(IceColor.b5).expand().bottom() }.grow()
                }.margin(backMargin).width(200f).growY()
            }.grow()
        }.grow()
        esc {
            hide()
        }
    }
}