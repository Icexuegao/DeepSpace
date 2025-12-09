package ice.ui.dialog

import arc.scene.actions.Actions
import arc.scene.style.Drawable
import arc.struct.Seq
import ice.library.scene.ui.actionsR
import ice.library.scene.ui.clearR

abstract class BaseMenusDialog(val name: String, val icon: Drawable) {
    companion object {
        val dalogs = Seq<BaseMenusDialog>()
    }

    init {
        dalogs.add(this)
    }

    var cont = MenusDialog.conts
    open fun build() {

    }

    open fun hide() {
        cont.clearR().actionsR(Actions.fadeOut(0f), Actions.fadeIn(0.5f))
    }
}