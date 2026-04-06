package ice.ui.menusDialog.data

import arc.graphics.Color
import ice.world.content.BaseContentSeq
import mindustry.type.StatusEffect

class StatusContentDialog:ContentDialogBase<StatusEffect>("状态", BaseContentSeq.status) {
  override fun getColor(): Color {
    return currentContent.color
  }
}