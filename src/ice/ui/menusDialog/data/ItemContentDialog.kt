package ice.ui.menusDialog.data

import arc.graphics.Color
import ice.world.content.BaseContentSeq
import ice.world.content.item.IceItem

class ItemContentDialog:ContentDialogBase<IceItem>("物品", BaseContentSeq.items) {
  override fun getColor(): Color {
    return currentContent.color
  }
}