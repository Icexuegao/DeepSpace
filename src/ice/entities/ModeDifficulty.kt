package ice.entities

import arc.graphics.Color
import ice.graphics.IceColor
import ice.ui.bundle.Localizable

enum class ModeDifficulty(var color: Color) :Localizable {

  神赐(IceColor.y1) {


    init {
      localization {
        zh_CN {
          name = "神赐"
          description = "{SICK=2}圣水淅沥,与神同行,乐园就在此处..."
        }
      }
    }
  },
  洗礼(IceColor.b4) {
    init {
      localization {
        zh_CN {
          name = "洗礼"
          description = "{HANG=2;2}福祸未分,命途难测,{ENDHANG}{JUMP}神谕者缄口不言"
        }
      }
    }
  },
  棘罪(IceColor.r1) {
    init {
      localization {
        zh_CN {
          name = "棘罪"
          description = "{SHAKE}{SPEED=0.4}圣光暗淡,神像蒙尘,亵渎者又将何去何从?"
        }
      }
    }
  };

  @JvmField var localizedName = ""
  @JvmField  var description = ""
  @JvmField var details = ""
  override fun setLocalizedName(localizedName: String) {
    this.localizedName = localizedName
  }

  override fun setDescription(description: String) {
    this.description = description
  }

  override fun setDetails(details: String) {
    this.details = details
  }
}