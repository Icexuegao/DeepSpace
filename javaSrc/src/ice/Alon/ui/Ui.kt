package ice.Alon.ui

import ice.Alon.ui.dialogs.ContentInfoDialog
import ice.Alon.ui.dialogs.MenusDialog
import mindustry.Vars


object Ui {
    val DisplayName = DisplayName()
    val contentInfoDialog = ContentInfoDialog()
    val menusDialog = MenusDialog()
    fun load() {
        Vars.ui.content = contentInfoDialog
        DisplayName.load()
        menusDialog.load()
    }
}

