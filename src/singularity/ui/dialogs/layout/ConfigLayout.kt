package singularity.ui.dialogs.layout

import arc.scene.ui.layout.Table

abstract class ConfigLayout(val name: String) {
  abstract fun build(table: Table)

  open fun getHieght() = 50f
}