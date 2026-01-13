package ice.ui.menusDialog

import arc.scene.ui.layout.Table
import ice.graphics.IStyles
import ice.ui.dialog.BaseMenusDialog
import ice.world.meta.IceStats

object PublicInfoDialog : BaseMenusDialog(IceStats.公告.localized(), IStyles.menusButton_publicInfo) {
    override fun build(cont: Table) {

    }
}