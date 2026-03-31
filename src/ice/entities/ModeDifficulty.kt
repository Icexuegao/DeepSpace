package ice.entities

import arc.graphics.Color
import ice.graphics.IceColor
import ice.ui.bundle.Bundle
import ice.ui.bundle.bundle

enum class ModeDifficulty(var color: Color) : Bundle {
  神赐(IceColor.y1) {
    init {
      bundle {
        desc(zh_CN, "神赐", "{SICK=2}圣水淅沥,与神同行,乐园就在此处...")
      }
    }
  },
  洗礼(IceColor.b4) {
    init {
      bundle {
        desc(zh_CN, "洗礼", "{HANG=2;2}福祸未分,命途难测,{ENDHANG}{JUMP}神谕者缄口不言")
      }
    }
  },
  棘罪(IceColor.r1) {
    init {
      bundle {
        desc(zh_CN, "棘罪", "{SHAKE}{SPEED=0.4}圣光暗淡,神像蒙尘,亵渎者又将何去何从?")
      }
    }
  }
}