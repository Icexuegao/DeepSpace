package singularity.ui.dialogs.layout

import arc.func.Cons
import arc.scene.ui.layout.Cell
import arc.scene.ui.layout.Table

class ConfigTableCfg(name: String, var table: Cons<Table>, var handler: Cons<Cell<Table>>) :ConfigEntry(name) {
  override fun buildCfg(table: Table) {
    handler.get(table.table { t: Table ->
      t.setClip(false)
      this.table.get(t)
    })
  }
}
