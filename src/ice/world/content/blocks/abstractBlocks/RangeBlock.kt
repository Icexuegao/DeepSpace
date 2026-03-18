package ice.world.content.blocks.abstractBlocks

import arc.func.Prov
import mindustry.Vars
import mindustry.graphics.Drawf
import mindustry.logic.Ranged
import singularity.world.blocks.SglBlock

open class RangeBlock(name: String) : SglBlock(name) {
  var range = 0f

  init {
    update = true
    buildType = Prov(::RangeBlockBuild)
  }

  override fun drawPlace(x: Int, y: Int, rotation: Int, valid: Boolean) {
    super.drawPlace(x, y, rotation, valid)
    if (range != 0f) Drawf.circles((x * Vars.tilesize).toFloat(), (y * Vars.tilesize).toFloat(), range, blockColor)
  }

  open inner class RangeBlockBuild : SglBuilding() {
    override fun drawSelect() {
      Drawf.circles(x, y, range, blockColor)
    }

    override fun drawConfigure() {
      super.drawConfigure()
      Drawf.circles(x, y, range, blockColor)
    }
  }
}