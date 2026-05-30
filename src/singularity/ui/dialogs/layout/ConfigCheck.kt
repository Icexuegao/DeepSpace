package singularity.ui.dialogs.layout

import arc.func.Boolc
import arc.func.Boolp
import arc.scene.ui.CheckBox
import arc.scene.ui.layout.Table
import ice.graphics.IStyles.checkCheckBoxStyle

open class ConfigCheck(name: String, var click: Boolc, var checked: Boolp) :ConfigEntry(name) {
  override fun buildCfg(table: Table) {
    val checkBox = table.check("", checked.get(), click).update { c: CheckBox -> c.setChecked(checked.get()) }.get()
    checkBox.setDisabled(disabled)
    checkBox.setStyle(checkCheckBoxStyle)
  }
}
