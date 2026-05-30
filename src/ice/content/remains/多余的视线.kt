package ice.content.remains

import ice.content.block.DefenseBlocks
import ice.core.IFiles.appendModName
import ice.graphics.IceColor
import ice.type.Remains
import mindustry.world.meta.Stats
import universecore.scene.style.DynamicTextureDrawable

class 多余的视线 :Remains("remains_extra_gaze") {
  init {
    remainsColor = IceColor.r2
    localization {
      zh_CN {
        localizedName = "多余的视线"
        description = "同一片神经系统的两个节点,我们相认的媒介"
        effect = "相控雷达锁定上限+[10]"
      }
    }
    icon = DynamicTextureDrawable(name.appendModName()) {
      it.frameCount = 24
      it.frameDuration = 15f
    }
    install = {
      DefenseBlocks.相控雷达.maxTargetSize += 10
      DefenseBlocks.相控雷达.stats = Stats()
      DefenseBlocks.相控雷达.checkStats()
    }
    uninstall = {
      DefenseBlocks.相控雷达.maxTargetSize -= 10
      DefenseBlocks.相控雷达.stats = Stats()
      DefenseBlocks.相控雷达.checkStats()
    }
  }
}