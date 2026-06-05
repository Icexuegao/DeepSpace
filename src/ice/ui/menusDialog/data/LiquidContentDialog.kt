package ice.ui.menusDialog.data

import arc.graphics.Color
import arc.scene.style.TextureRegionDrawable
import arc.scene.ui.layout.Table
import ice.audio.ISounds
import ice.graphics.IStyles
import ice.ui.UI
import ice.world.content.BaseContentSeq
import mindustry.type.Liquid
import universecore.ui.widgets.tables.addLine
import universecore.ui.widgets.tables.iTableGX
import universecore.ui.widgets.tables.itooltip
import universecore.ui.widgets.tables.ITable

class LiquidContentDialog :ContentDialogBase<Liquid>("流体", BaseContentSeq.liquids) {
  override fun getColor(): Color {
    return currentContent.get().color
  }

  override fun listTable(): Table {
    val list = ITable()
    val types = arrayOf("liquid", "gas")
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
      if (content.gas) dfw(tables[1]) else dfw(tables[0])
    }

    types.forEach { name ->
      val child = tables[types.indexOf(name)]
      if (child.children.size == 0) return@forEach
      list.iTableGX { it1 ->
        it1.addLine(name).padBottom(5f)
        it1.add(child).grow().row()
      }.row()
    }
    return list
  }
}