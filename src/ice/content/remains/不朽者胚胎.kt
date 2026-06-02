package ice.content.remains

import arc.scene.ui.layout.Table
import ice.core.IFiles.appendModName
import ice.graphics.IceColor
import ice.type.Remains
import ice.ui.menusDialog.RemainsDialog.slotPos
import mindustry.Vars
import universecore.ui.widgets.typinglabel.TLabel
import universecore.ui.effects.DynamicTextureDrawable

class 不朽者胚胎 :Remains("remains_immortal_embryo") {
  val pos = 2

  init {
    localization {
      zh_CN {
        localizedName = "不朽者胚胎"
        effect = "遗物槽位+[$pos]"
      }
    }

    icon = DynamicTextureDrawable(name.appendModName()) {
      it.frameCount = 16
      it.frameDuration = 60f / 4f
    }

    level = 1
    remainsColor = IceColor.r2
    install = {
      slotPos += pos
    }
    uninstall = {
      slotPos -= pos
    }

    disabled = {
      Vars.state.isGame || (getEnableds().contains(this) && getEnableds().size > slotPos - pos)
    }
  }

  override fun customTable(table: Table) {
    val text = """
      一个被囚禁的血肉胚胎
      拥抱我,我将赐你永恒
      不必畏惧刀剑与瘟疫,不必屈服于时光与死亡
      用你的过去,换取未来
      用你的灵魂,换取存在
      直至你我合而为一
    """.trimIndent()

    for(string in text.split("\n")) {
      table.add(TLabel(string)).pad(5f).color(remainsColor).row()
    }

  }
}