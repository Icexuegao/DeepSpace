package ice.ui.menusDialog

import arc.scene.ui.Image
import arc.scene.ui.layout.Table
import arc.util.Scaling
import ice.graphics.IStyles
import ice.library.struct.asDrawable
import ice.ui.dialog.BaseMenusDialog
import ice.world.meta.IceStats

object PublicInfoDialog : BaseMenusDialog(IceStats.公告.localized(), IStyles.menusButton_publicInfo) {
    override fun build(cont: Table) {
      cont.top()
      val element = Image(IStyles.publicInfoIcon.asDrawable(0.5f).apply {

      }, Scaling.fit)
      cont.add(element)
    }
}