package ice.content.remains

import ice.content.IUnitTypes
import ice.core.IFiles.appendModName
import ice.type.Remains
import mindustry.world.meta.Stats
import universecore.ui.effects.DynamicTextureDrawable

class 流光罗盘 :Remains("remains_flowing_compass") {
  init {
    localization {
      zh_CN {
        localizedName = "流光罗盘"
        description = "表面刻有古老的符文,会发出淡淡的光芒"
        effect = "核心机增加[1]速度"
      }
    }
    icon = DynamicTextureDrawable(name.appendModName()) {
      it.frameCount = 19
      it.frameDuration = 60f / 7f
    }

    val lucifer = IUnitTypes.路西法
    install = {
      lucifer.speed += 1f
      lucifer.stats = Stats()
      lucifer.checkStats()
    }
    uninstall = {
      lucifer.speed -= 1f
      lucifer.stats = Stats()
      lucifer.checkStats()
    }
  }
}