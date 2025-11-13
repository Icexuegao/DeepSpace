package ice.ui.dialog

import arc.scene.actions.Actions
import arc.scene.style.Drawable
import arc.struct.Seq
import ice.ui.actionsR
import ice.ui.clearR

abstract class BaseDialog(val name: String, val icon: Drawable) {
    companion object {
        val dalogs = Seq<BaseDialog>()
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