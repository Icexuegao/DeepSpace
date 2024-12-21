package ice.ui

import ice.ui.dialogs.IceContentInfoDialog
import ice.ui.dialogs.SectorID
import ice.ui.menus.MenusDialog
import mindustry.Vars


object Ui {
    fun load() {
        DisplayName.flun()
        Vars.ui.content = IceContentInfoDialog()
        MenusDialog.show()
        SectorID.load()
    }
}
