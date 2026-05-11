package ice.content.remains

import ice.content.IStatus
import ice.core.IFiles.appendModName
import ice.graphics.IceColor
import ice.type.Remains
import mindustry.world.meta.Stats
import universecore.scene.style.DynamicTextureDrawable

class 心跳鼓:Remains("remains_heartbeat_drum"){
  init  {
    remainsColor = IceColor.r2
    localization {
      zh_CN {
        this.localizedName = "心跳鼓"
        description = "弹性心肌隔膜,回响着怀念之音"
      }
    }

    effect = "使状态[${IStatus.回响.localizedName}]的影响提升[20%]"
    icon = DynamicTextureDrawable(name.appendModName()) {
      it.frameCount = 9
      it.frameDuration = 60f / 6f
    }
    install = {
      IStatus.回响.speedMultiplier += 0.2f
      IStatus.回响.stats = Stats()
      IStatus.回响.checkStats()
    }
    uninstall = {
      IStatus.回响.speedMultiplier -= 0.2f
      IStatus.回响.stats = Stats()
      IStatus.回响.checkStats()
    }
  }
}