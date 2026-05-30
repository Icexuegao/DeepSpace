package singularity.ui.dialogs.layout

import arc.graphics.Color
import arc.scene.ui.layout.Table
import ice.graphics.IceColor.b4
import mindustry.ui.Styles

class ConfigSepLine(name: String, var string: String?) :ConfigLayout(name) {
  var lineColor: Color = b4
  var lineColorBack: Color = lineColor.cpy().mul(0.8f, 0.8f, 0.8f, 1f)

  override fun build(table: Table) {
    table.stack(Table { t: Table ->
      t.image().color(lineColor).pad(0f).grow()
      t.row()
      t.image().color(lineColorBack).pad(0f).height(4f).growX()
    }, Table { t: Table ->
      t.left().add(string, Styles.outlineLabel).fill().left().padLeft(5f)
    }).grow().pad(-5f).padBottom(4f).padTop(4f)
    table.row()
  }
}