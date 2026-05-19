package ice.ui.menusDialog

import arc.scene.ui.Image
import arc.scene.ui.layout.Table
import arc.util.Scaling
import ice.graphics.IStyles
import ice.graphics.IceColor
import ice.ui.dialog.BaseMenusDialog
import ice.world.meta.IceStats
import universecore.scene.ui.iPane
import universecore.struct.texture.asDrawable

object PublicInfoDialog :BaseMenusDialog(IceStats.公告.localized(), IStyles.menusButton_publicInfo) {
  lateinit var conts: Table
  fun addLabel(a: String) {
    conts.add(a).growX().wrap().pad(10f).padLeft(20f).padRight(20f).color(IceColor.b4).row()
  }

  override fun build(cont: Table) {

    cont.top()
    val element = Image(IStyles.publicInfoIcon.asDrawable(0.75f), Scaling.fit)
    cont.add(element).row()
    cont.iPane {
      conts = it
    }.grow()
    addLabel("因科技树未完善,所以模组科技默认全解锁")
    addLabel("矩阵待修复")
  }
}