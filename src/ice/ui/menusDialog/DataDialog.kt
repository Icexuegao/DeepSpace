@file:Suppress("UNCHECKED_CAST")

package ice.ui.menusDialog

import arc.scene.actions.Actions
import arc.scene.ui.Image
import arc.scene.ui.TextButton
import arc.scene.ui.layout.Table
import ice.audio.ISounds
import ice.graphics.IStyles
import ice.graphics.IceColor
import ice.ui.MenusDialog
import ice.ui.UI
import ice.ui.dialog.BaseMenusDialog
import ice.ui.menusDialog.data.*
import ice.world.meta.IceStats
import mindustry.ctype.UnlockableContent
import universecore.scene.ui.iTableGX
import universecore.ui.reactive.ReactiveState
import universecore.ui.reactive.react

object DataDialog :BaseMenusDialog(IceStats.数据.localized(), IStyles.menusButton_database) {
  init {
    ItemContentDialog()
    LiquidContentDialog()
    BlockContentDialog()
    StatusContentDialog()
    UnitContentDialog()
  }

  var contentDialog: ReactiveState<ContentDialogBase<*>> = ReactiveState(ContentDialogBase.contentDialog.first())

  fun toggleMenu(contentDialog: ContentDialogBase<*>) {
    this.contentDialog.update { contentDialog }
  }

  override fun build(cont: Table) {
    cont.iTableGX { ta ->
      ContentDialogBase.contentDialog.forEach {
        val textButton = TextButton(it.cName, IStyles.button1)
        textButton.changed {
          if (contentDialog.get() == it) return@changed
          toggleMenu(it)
          UI.showUISoundCloseV(ISounds.数据板块顶部选择按钮反馈)
        }
        textButton.update {
          textButton.isChecked = contentDialog.get() == it
        }
        ta.add(textButton).pad(1f).grow()
      }
    }.height(60f).row()

    cont.add(Image(IStyles.whiteui)).color(IceColor.b1).height(3f).growX().row()

    cont.react(contentDialog) {
      it.actions(Actions.alpha(0f),Actions.alpha(1f, 0.15f))
      contentDialog.get().build(it)
    }.grow().get()
  }

  fun showUnlockableContent(block: UnlockableContent) {
    if (block.isHidden) return
    var ha: ContentDialogBase<*>? = null

    ContentDialogBase.contentDialog.forEach {
      if (it.contetnArray.contains(block)) ha = it
    }
    if (ha != null) {
      toggleMenu(ha)
      contentDialog.get().setCurrent(block)
    } else return

    if (!MenusDialog.isShown) MenusDialog.show()

    MenusDialog.button = this
    hide()
    build(MenusDialog.conts)

  }

}