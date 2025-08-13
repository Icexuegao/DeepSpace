package ice.ui.dialog

import arc.Core
import arc.func.Prov
import arc.graphics.g2d.Draw
import arc.math.Interp
import arc.scene.Action
import arc.scene.actions.Actions
import arc.scene.event.Touchable
import arc.scene.event.VisibilityEvent
import arc.scene.style.Drawable
import arc.scene.ui.Image
import arc.scene.ui.layout.Table
import arc.util.Align
import ice.library.meta.stat.IceStats
import ice.library.scene.tex.Colors
import ice.library.scene.tex.IStyles
import ice.library.scene.tex.IStyles.deepSpaceVer
import ice.music.IceMusics
import ice.ui.actionsR
import ice.ui.clearR
import ice.ui.dialog.research.ResearchDialog
import ice.vars.SettingValue
import mindustry.gen.Icon

object MenusDialog : Table() {
    private val defaultShowAction = Prov<Action> {
        Actions.sequence(Actions.alpha(0f), Actions.fadeIn(0.4f, Interp.fade))
    }
    private var defaultHideAction = Prov<Action> { Actions.fadeOut(0.4f, Interp.fade) }
    lateinit var cont: Table
    val back = IStyles.background11
    val backMargin = 10f
    var button = MeunsButton.日志

    enum class MeunsButton(val names: String, val icon: Drawable, val run: Runnable = Runnable {}) {
        进度(IceStats.进度.localized(), Icon.chartBar),
        科技(IceStats.科技.localized(), Icon.tree, { ResearchDialog.show() }),
        数据(IceStats.数据.localized(), Icon.book, { DataDialog.show() }),
        成就(IceStats.成就.localized(), Icon.star, { AchievementDialog.build() }),
        日志(IceStats.日志.localized(), Icon.fileText, { LogDIalog.show() }),
        配置(IceStats.设置.localized(), Icon.filters, { SettingDialog.show() }),
        鸣谢(IceStats.鸣谢.localized(), Icon.bookOpen, { ThankDialog.show() }),
        关闭(IceStats.关闭.localized(), Icon.exit, { hide() })
    }

    init {
        setFillParent(true)
        table(back) { table ->
            table.table(back) {
                it.add("May we love each other today").color(Colors.b4)
            }.margin(10f).height(60f).growX().row()

            table.table { it1 ->
                it1.table(back) {
                    MeunsButton.entries.forEach { mb ->
                        it.button({ b ->
                            b.image(mb.icon).color(Colors.b5).table.add(mb.names).color(Colors.b5)
                        }, IStyles.rootButton) {
                            cont.margin(backMargin)
                            if (mb != MeunsButton.关闭) {
                                clean()
                                button = mb
                            }
                            mb.run.run()
                        }.update { b ->
                            b.isChecked = button == mb
                        }.pad(2f).grow().row()
                    }
                }.width(200f).margin(10f).growY()
                val value = object : Table(back) {
                    init {
                        touchable = Touchable.enabled
                    }

                    override fun drawBackground(x: Float, y: Float) {
                        if (background == null) return
                        val color = this.color
                        Draw.color(color.r, color.g, color.b, 1 * parentAlpha)
                        background.draw(x, y, width, height)
                    }
                }
                cont = value
                button.run.run()

                it1.add(value).grow().margin(backMargin)
                it1.table(back) { it2 ->
                    it2.table { it.image(deepSpaceVer).color(Colors.b5).expand().top() }.grow().row()
                    it2.table { it.add(Image(IStyles.flower)).color(Colors.b5).expand().bottom() }.grow()
                }.margin(backMargin).width(200f).growY()
            }.grow()
        }.size(1707f, 996f).expand()
    }

    fun clean() {
        cont.clearR().actionsR(Actions.fadeOut(0f), Actions.fadeIn(0.5f))
    }

    fun show() {
        Core.scene.add(this)
        SettingValue.menuMusicVolume
        align(Align.bottomLeft)
        if (SettingValue.menuMusic) {
            IceMusics.title.play()
        }
        IceMusics.title.isLooping = true
        addAction(defaultShowAction.get())
        fire(VisibilityEvent(false))
    }

    private fun hide() {
        IceMusics.title.pause(true)
        fire(VisibilityEvent(true))
        addAction(Actions.sequence(defaultHideAction.get(), Actions.remove()))
    }
}