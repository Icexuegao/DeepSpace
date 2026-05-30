package ice.content.remains

import ice.core.IFiles.appendModName
import ice.graphics.IceColor
import ice.type.Remains
import universecore.scene.style.DynamicTextureDrawable

class 现彼岸 :Remains("remains_higanbana") {
  init {
    localization {
      zh_CN {
        localizedName = "现彼岸"
        description = "随风飘散的种子编制着你想要的模板"
      }
    }
    remainsColor = IceColor.r2
    icon = DynamicTextureDrawable(name.appendModName()) {
      it.frameCount = 8
      it.frameDuration = 60f / 3f
    }
  }
}