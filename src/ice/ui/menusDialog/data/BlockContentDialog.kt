package ice.ui.menusDialog.data

import arc.scene.style.TextureRegionDrawable
import ice.audio.ISounds
import ice.graphics.IStyles
import universecore.scene.ui.addLine
import universecore.scene.ui.iTableG
import universecore.scene.ui.itooltip
import universecore.scene.ui.layout.ITable
import ice.ui.UI
import ice.world.content.BaseContentSeq
import mindustry.type.Category
import mindustry.world.Block

class BlockContentDialog :ContentDialogBase<Block>("建筑", BaseContentSeq.blocks) {
  override fun flunList() {
    list.clearChildren()

    val values = Category.entries.toTypedArray()
    val tables = Array(values.size) { ITable().apply { setRowsize(5) } }

    contetnArray.select { content ->
      searchSelect(content)
    }.forEach { content ->
      values.forEach { category ->
        val child = tables[category.ordinal]
        if (content.category.name == category.name) {
          child.button(TextureRegionDrawable(content.uiIcon), IStyles.button, 40f) {
            currentContent = content
            flunInfo()
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
  }
}