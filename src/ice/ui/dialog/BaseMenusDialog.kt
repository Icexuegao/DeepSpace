package ice.ui.dialog

import arc.scene.actions.Actions
import arc.scene.style.Drawable
import arc.scene.ui.layout.Table
import arc.struct.Seq
import ice.ui.MenusDialog
import universecore.ui.widgets.tables.actionsR
import universecore.ui.widgets.tables.clearR

abstract class BaseMenusDialog(val name: String, val icon: Drawable) {
  companion object {
    val dalogs = Seq<BaseMenusDialog>()
  }

  abstract fun build(cont: Table)

  open fun hide() {
    MenusDialog.conts.clearR().actionsR(Actions.fadeOut(0f), Actions.fadeIn(0.5f))
  }
}