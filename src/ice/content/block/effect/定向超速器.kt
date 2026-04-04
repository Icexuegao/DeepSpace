package ice.content.block.effect

import ice.ui.bundle.BaseBundle.Companion.desc
import ice.ui.bundle.BaseBundle.Companion.zh_CN
import ice.ui.bundle.bundle
import ice.world.content.blocks.effect.OrientationProjector

class 定向超速器:OrientationProjector("orientationProjector"){
  init{
    bundle {
      desc(zh_CN, "定向超速器")
    }
    size = 2
    buildSize = 5
    range = 8 * 20f

  }
}