package ice.ui.menus

import arc.Core
import arc.func.Cons
import arc.func.Prov
import arc.graphics.g2d.Draw
import arc.math.Interp
import arc.scene.Action
import arc.scene.actions.Actions
import arc.scene.event.VisibilityEvent
import arc.scene.style.Drawable
import arc.scene.ui.Image
import arc.scene.ui.layout.Table
import arc.util.Align
import ice.music.IceMusics
import ice.ui.TableExtend.actionsR
import ice.ui.TableExtend.clearR
import ice.ui.menus.data.DataDialog
import ice.ui.tex.Colors
import ice.ui.tex.IceTex
import ice.ui.tex.IceTex.deepSpaceVer
import mindustry.gen.Icon


object MenusDialog : Table() {
    private val defaultShowAction = Prov<Action> {
        Actions.sequence(
            Actions.alpha(0f), Actions.fadeIn(0.4f, Interp.fade)
        )
    }
    private var defaultHideAction = Prov<Action> { Actions.fadeOut(0.4f, Interp.fade) }
    private lateinit var cont: Table
    private lateinit var button: Table
    private val back = IceTex.background
    val backMargin = 10f
    private val gwidth: Float = Core.graphics.width.toFloat()
    private val gheight: Float = Core.graphics.height.toFloat()

    init {
        table(back) { table ->
            table.table(back) {
                it.add("May we love each other today").color(Colors.b4)
            }.height(60f).width(gwidth).margin(10f).row()
            table.table(back) { it1 ->
                it1.table(back) { it2 ->
                    button = it2
                    createLeftButton("@schedule", Icon.chartBar, "进度") {}
                    createLeftButton("@tree", Icon.tree, "科技树") {}
                    createLeftButton("@data", Icon.book, "数据", DataDialog::set)
                    createLeftButton("@achievement", Icon.star, "成就", AchievementDialog::set)
                    createLeftButton("@logs", Icon.fileText, "更新日志") {}
                    createLeftButton("@bundle", Icon.wrench, "Bundle检验", BundleDetection::set)
                    createLeftButton("@settings", Icon.filters, "配置设置", SettingDialog::set)
                    createLeftButton("@tanks", Icon.bookOpen, "感谢名单", ThankDialog::set)
                    createLeftButton("@close", Icon.exit, "关闭窗口") { hide() }
                }.width(200f).margin(backMargin).growY()

                val value = object : Table(back) {
                    override fun drawBackground(x: Float, y: Float) {
                        if (background == null) return
                        val color = this.color
                        Draw.color(color.r, color.g, color.b, 1 * parentAlpha)
                        background.draw(x, y, width, height)
                    }
                }
                cont = value/* it1.table(back) { it2 ->
                     it2.add("111111111111111111").row()
                     cont = it2
                 }.grow().margin(10f)*/

                it1.add(value).grow().margin(backMargin)

                it1.table(back) { it2 ->
                    it2.table { it.image(deepSpaceVer).color(Colors.b5).expand().top() }.grow().row()
                    it2.table { it.add(Image(IceTex.flower)).color(Colors.b5).expand().bottom() }.grow()
                }.width(200f).margin(backMargin).growY()
            }.minSize(gwidth, gheight - 60f)
        }
    }

    private fun clean() {
        IceMusics.toggle("title", SettingValue.getMenuMusic())
        cont.clearR().actionsR(Actions.fadeOut(0f), Actions.fadeIn(1.5f))
    }

    private fun createLeftButton(name: String, icon: Drawable, string: String, const: Cons<Table>) {
        button.button(
            {
                it.image(icon).color(Colors.b5).left().table.add(name).color(
                    Colors.b5
                )
            }, IceTex.rootButton
        ) { clean();const.get(cont) }.pad(2f).expand().fill().tooltip(string).row()
    }


    fun show() {
        IceMusics.toggle("title", SettingValue.getMenuMusic())
        setOrigin(Align.center)
        setClip(false)
        isTransform = true
        fire(VisibilityEvent(false))
        clearActions()
        pack()
        Core.scene.add(this)
        addAction(defaultShowAction.get())
        pack()
        centerWindow()
    }

    private fun hide() {
        IceMusics.stopAll()

        if (!isShown()) return
        setOrigin(Align.center)
        setClip(false)
        isTransform = true
        hide(defaultHideAction.get())
    }

    private fun hide(action: Action?) {
        fire(VisibilityEvent(true))
        if (action != null) {
            addAction(Actions.sequence(action, Actions.run(cont::clear), Actions.remove()))
        } else remove()

    }

    private fun isShown(): Boolean {
        return scene != null
    }

    private fun centerWindow() {
        setPosition(
            Math.round((Core.scene.width - Core.scene.marginLeft - Core.scene.marginRight - getWidth()) / 2).toFloat(),
            Math.round((Core.scene.height - Core.scene.marginTop - Core.scene.marginBottom - getHeight()) / 2).toFloat()
        )
    }
}