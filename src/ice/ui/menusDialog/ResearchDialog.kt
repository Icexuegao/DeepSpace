package ice.ui.menusDialog

import arc.scene.event.Touchable
import arc.scene.ui.layout.Stack
import arc.scene.ui.layout.Table
import ice.graphics.IStyles
import ice.library.scene.ui.iTableG
import ice.ui.dialog.BaseMenusDialog
import ice.world.meta.IceStats
import singularity.ui.dialogs.SglTechTreeDialog

@Suppress("unused")
object ResearchDialog : BaseMenusDialog(IceStats.研究.localized(), IStyles.menusButton_tech_point) {

  var techTreeDialog = SglTechTreeDialog()
  override fun build(cont: Table) {
    cont.touchable = Touchable.childrenOnly
    cont.iTableG { t ->
      t.clip = true
      val element = Stack(techTreeDialog)
      t.add(element).grow()
      t.touchable = Touchable.enabled
    }
  }
}

