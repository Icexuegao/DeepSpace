package ice.Alon.ui

import ice.Alon.ui.dialogs.ContentInfoDialog
import ice.Alon.ui.dialogs.IcePausedDialog
import ice.Alon.ui.dialogs.MenusDialog
import mindustry.Vars
import mindustry.mod.Mod


object Ui{
    val DisplayName = DisplayName()
    val contentInfoDialog = ContentInfoDialog()
    val menusDialog = MenusDialog()
    val icePausedDialog = IcePausedDialog()
    fun load() {
        Vars.ui.paused= icePausedDialog
        Vars.ui.content = contentInfoDialog
        DisplayName.load()
        menusDialog.load()
        SectorID.load()
        updateZoom()
    }
    private fun updateZoom() {
        val minZoomLim = 0.5f
        val maxZoomLim = 40f
        Vars.renderer.minZoom = minZoomLim
        Vars.renderer.maxZoom = maxZoomLim
    }
}

