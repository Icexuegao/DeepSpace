package ice.ui

import arc.Core
import arc.graphics.g2d.Draw
import arc.input.KeyCode
import arc.math.Interp
import arc.scene.Scene
import arc.scene.actions.Actions
import arc.scene.ui.Dialog
import arc.scene.ui.Image
import arc.scene.ui.layout.Table
import arc.util.Align
import ice.graphics.IStyles
import ice.graphics.IceColor
import ice.library.scene.ui.iPaneG
import ice.library.world.Load
import ice.ui.dialog.BaseMenusDialog
import ice.ui.menusDialog.*
import ice.world.meta.IceStats

object MenusDialog : Dialog(), Load {
  const val backMargin = 10f
  val back = IStyles.background11
  var button: BaseMenusDialog = PublicInfoDialog
  lateinit var conts: Table

  override fun init() {
    BaseMenusDialog.dalogs.add(PublicInfoDialog)
    BaseMenusDialog.dalogs.add(ResearchDialog)
    BaseMenusDialog.dalogs.add(DataDialog)
    BaseMenusDialog.dalogs.add(AchievementDialog)
    RemainsDialog.init()
    BaseMenusDialog.dalogs.add(RemainsDialog)
    BaseMenusDialog.dalogs.add(ConfigureDialog)
    BaseMenusDialog.dalogs.add(ModInfoDialog)
    BaseMenusDialog.dalogs.add(SponsoredDialog)
  }

  fun build() {
    reset()
    setFillParent(true)
    defaults().reset()
    table { table ->
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
          it.iPaneG { pan ->
            pan.top()
            BaseMenusDialog.dalogs.forEach { mb ->
              pan.button({ b ->
                b.image(mb.icon).size(50f).color(IceColor.b5).table.add(mb.name).color(IceColor.b5)
              }, IStyles.rootButton) {
                if (button == mb) return@button
                button.hide()
                button = mb
                mb.build(conts)
              }.update { b ->
                b.isChecked = button == mb
              }.pad(2f).margin(20f).growX().row()
            }
            pan.button({ b ->
              b.image(IStyles.menusButton_exit).size(50f).color(IceColor.b5).table.add(IceStats.关闭.localized()).color(IceColor.b5)
            }, IStyles.rootCleanButton) {
              hide()
            }.pad(2f).margin(20f).growX().row()
          }
        }.width(200f).margin(10f).growY()
        it1.add(middle).grow().margin(backMargin)
        it1.table(back) { it2 ->
          it2.table { it.image(IStyles.deepSpaceVer).color(IceColor.b5).expand().top() }.grow().row()
          it2.table { it.add(Image(IStyles.flower)).color(IceColor.b5).expand().bottom() }.grow()
        }.margin(backMargin).width(200f).growY()
      }.grow()
    }.pad(0f).grow()
    button.build(conts)
    keyDown(KeyCode.escape) {
      Core.app.post {
        hide()
      }
    }
  }

  override fun show(stage: Scene): Dialog {
    build()
    show(stage, Actions.sequence(Actions.alpha(0f), Actions.fadeIn(0.4f, Interp.fade)))
    centerWindow()
    return this
  }

  override fun hide() {
    if (!isShown) return
    setOrigin(Align.center)
    clip = false
    isTransform = true

    hide(Actions.fadeOut(0.4f, Interp.fade))
  }
}