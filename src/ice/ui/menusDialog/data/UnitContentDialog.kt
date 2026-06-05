package ice.ui.menusDialog.data

import arc.scene.style.TextureRegionDrawable
import arc.scene.ui.layout.Table
import ice.audio.ISounds
import ice.graphics.IStyles
import ice.ui.UI
import ice.world.content.BaseContentSeq
import mindustry.type.UnitType
import universecore.ui.widgets.tables.addLine
import universecore.ui.widgets.tables.iTableG
import universecore.ui.widgets.tables.itooltip
import universecore.ui.widgets.tables.ITable

class UnitContentDialog :ContentDialogBase<UnitType>("单位", BaseContentSeq.units) {
  override fun listTable(): Table {
    val list = Table()
    val types = arrayOf("ground", "air", "naval")
    val tables = Array(types.size) { ITable().apply { setRowsize(5) } }

    contetnArray.select { content ->
      searchSelect(content)
    }.forEach { content ->
      fun dfw(table: Table) {
        table.button(TextureRegionDrawable(content.uiIcon), IStyles.button, 40f) {
          currentContent.update { content }
          UI.showUISoundCloseV(ISounds.数据板块内个体反馈)
        }.size(60f).pad(2f).margin(5f).itooltip(content.localizedName)
      }
      if (content.flying) dfw(tables[1]) else if (content.naval) dfw(tables[2]) else dfw(tables[0])
    }

    types.forEach { name ->
      val child = tables[types.indexOf(name)]
      if (child.children.size == 0) return@forEach
      list.iTableG { it1 ->
        it1.addLine(name).padBottom(5f)
        it1.add(child).grow().row()
      }.row()
    }
    return list
  }
}