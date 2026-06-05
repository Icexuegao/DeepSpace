package ice.ui.menusDialog.data

import arc.scene.style.TextureRegionDrawable
import arc.scene.ui.layout.Table
import ice.audio.ISounds
import ice.graphics.IStyles
import ice.ui.UI
import ice.world.content.BaseContentSeq
import mindustry.type.Category
import mindustry.world.Block
import universecore.ui.widgets.tables.ITable
import universecore.ui.widgets.tables.addLine
import universecore.ui.widgets.tables.iTableG
import universecore.ui.widgets.tables.itooltip

class BlockContentDialog :ContentDialogBase<Block>("建筑", BaseContentSeq.blocks) {
  override fun listTable(): Table {
    val list= ITable().apply { setRowsize(5) }
    val values = Category.entries.toTypedArray()
    val tables = Array(values.size) { ITable().apply { setRowsize(5) } }

    contetnArray.select { searchSelect(it) }.forEach { content ->
      values.forEach { category ->
        val child = tables[category.ordinal]
        if (content.category == category) {
          child.button(TextureRegionDrawable(content.uiIcon), IStyles.button, 40f) {
            currentContent.update { content }
            UI.showUISoundCloseV(ISounds.数据板块内个体反馈)
          }.size(60f).pad(2f).margin(5f).itooltip(content.localizedName)
        }
      }
    }


    values.forEach { category ->
      val child = tables[category.ordinal]
      if (child.children.size == 0) return@forEach
      list.iTableG { it1 ->
        it1.addLine(category.name).padBottom(5f)
        it1.add(child).grow().row()
      }.row()
    }
    return list
  }
}