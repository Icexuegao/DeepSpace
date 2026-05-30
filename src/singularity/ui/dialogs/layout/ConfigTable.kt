package singularity.ui.dialogs.layout

import arc.func.Cons
import arc.scene.ui.layout.Table

open class ConfigTable(name: String, var builder: Cons<Table>) :ConfigLayout(name) {
  override fun build(table: Table) {
    builder.get(table)
  }
}
