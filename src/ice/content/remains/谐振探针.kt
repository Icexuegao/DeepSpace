package ice.content.remains

import ice.type.Remains
import ice.world.content.blocks.environment.IceOreBlock
import mindustry.Vars

class 谐振探针 :Remains("remains_resonance_probe") {
  init {
    localization {
      zh_CN {
        localizedName = "谐振探针"
        description = "一种用于探测矿物谐振频率的装置"
        effect = "矿物地板不再[隐藏]"
      }
    }

    install = {
      Vars.content.blocks().forEach {
        if (it is IceOreBlock) {
          it.display = true
          it.useColor = true
        }
      }
      Vars.renderer.blocks.floor.reload()
      Vars.renderer.minimap.reset()
    }
    uninstall = {
      Vars.content.blocks().forEach {
        if (it is IceOreBlock) {
          it.display = false
          it.useColor = false
        }
      }
      Vars.renderer.blocks.floor.reload()
      Vars.renderer.minimap.reset()
    }
  }
}