package ice.ui.dialog

import arc.scene.actions.Actions
import arc.scene.style.Drawable
import arc.scene.ui.layout.Table
import arc.struct.Seq
import ice.library.scene.ui.actionsR
import ice.library.scene.ui.clearR
import ice.library.world.Load
import ice.ui.MenusDialog

abstract class BaseMenusDialog(val name: String, val icon: Drawable): Load{
    companion object {
        val dalogs = Seq<BaseMenusDialog>()
    }

    abstract fun build(cont: Table)

    open fun hide() {
      MenusDialog.conts.clearR().actionsR(Actions.fadeOut(0f), Actions.fadeIn(0.5f))
    }
}