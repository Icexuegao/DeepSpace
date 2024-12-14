package ice.ui

import ice.ui.dialogs.ContentInfoDialog
import ice.ui.dialogs.IcePausedDialog
import ice.ui.dialogs.MenusDialog
import mindustry.Vars


object Ui {
    fun load() {
        Vars.ui.paused = IcePausedDialog()
        Vars.ui.content = ContentInfoDialog()
        MenusDialog().load()
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

