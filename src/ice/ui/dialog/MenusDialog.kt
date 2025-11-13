package ice.ui.dialog

import arc.graphics.g2d.Draw
import arc.math.Interp
import arc.scene.Scene
import arc.scene.actions.Actions
import arc.scene.ui.Dialog
import arc.scene.ui.Image
import arc.scene.ui.layout.Table
import arc.util.Align
import ice.library.meta.stat.IceStats
import ice.library.scene.tex.IStyles
import ice.library.scene.tex.IStyles.deepSpaceVer
import ice.library.scene.tex.IceColor
import ice.ui.dialog.research.ResearchDialog
import ice.ui.iPaneG
import mindustry.gen.Icon

object MenusDialog : Dialog() {
    const val backMargin = 10f
    val back = IStyles.background11
    var button: BaseDialog? = null
    lateinit var conts: Table

    init {
        clearChildren()
        margin(0f)
        defaults().pad(0f)
        setFillParent(true)
        //setOrigin(Align.center)
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
                        BaseDialog.dalogs.forEach { mb ->
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
                            b.image(Icon.exit).color(IceColor.b5).table.add(IceStats.关闭.localized())
                                .color(IceColor.b5)
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
    }

    override fun show(stage: Scene): MenusDialog {
        show(stage, Actions.sequence(Actions.alpha(0f), Actions.fadeIn(0.4f, Interp.fade)))
        centerWindow()
        return this
    }

    override fun hide() {
        if (!isShown) return
        setOrigin(Align.center)
        setClip(false)
        isTransform = true
        hide(Actions.fadeOut(0.4f, Interp.fade))
    }

}