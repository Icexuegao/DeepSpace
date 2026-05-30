package singularity.ui.dialogs.layout

import arc.func.Prov
import arc.scene.ui.Button
import arc.scene.ui.layout.Table

class ConfigButton(name: String, var button: Prov<Button>) :ConfigEntry(name) {
  var minHieght = 80f
  override fun buildCfg(table: Table) {
    table.add(button.get()).width(180f).growY().pad(4f).get().setDisabled(disabled)
  }

  override fun getHieght() = minHieght
}