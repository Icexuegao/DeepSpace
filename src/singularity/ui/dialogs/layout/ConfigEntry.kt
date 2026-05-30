package singularity.ui.dialogs.layout

import arc.Core
import arc.func.Boolp
import arc.func.Cons
import arc.func.Prov
import arc.scene.ui.Label
import arc.scene.ui.Tooltip
import arc.scene.ui.layout.Table
import ice.graphics.IceColor.b4

abstract class ConfigEntry(name: String) :ConfigLayout(name) {
  var str: Prov<String>? = null
  var tip: Prov<String>? = null
  var disabled: Boolp = Boolp { false }

  init {
    if (Core.bundle.has("settings.tip.$name")) {
      tip = Prov { Core.bundle.get("settings.tip.$name") }
    }
  }

  override fun build(table: Table) {
    table.left().add(name).color(b4).left().padLeft(4f)
    table.table { t: Table ->

      if (str != null) {
        t.add("").color(b4).update { l: Label? ->
          l!!.setText(str!!.get())
        }
      }
      buildCfg(t)
    }.expandX().right().height(60f).padRight(4f)

    if (tip != null) {
      table.addListener(object :Tooltip(Cons { ta: Table? -> ta!!.add(tip!!.get()).update { l: Label? -> l!!.setText(tip!!.get()) } }) {
        init {
          allowMobile = true
        }
      })
    }
  }

  abstract fun buildCfg(table: Table)
}