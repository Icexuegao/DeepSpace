package ice.world.content.unit.entity

import arc.Core
import arc.graphics.g2d.Draw
import arc.graphics.g2d.TextureRegion
import arc.util.Interval
import ice.library.IFiles.appendModName
import ice.world.content.unit.entity.base.FleshEntity

class Flies : FleshEntity() {
  companion object {
    val flies: Array<TextureRegion> by lazy {
      Array(4) {
        Core.atlas.find(("flies-${it + 1}").appendModName())
      }
    }
  }

  val interval = Interval(2)
  var index = 0
  override fun drawBodyRegion(rotation: Float) {
    super.drawBodyRegion(rotation)
    Draw.rect(flies[index % 3], x, y, rotation)
  }

  override fun drawShadowRegion(x: Float, y: Float, rotation: Float) {
    Draw.rect(flies[index % 3], x, y, rotation)
  }

  override fun update() {
    super.update()
    if (interval.get(5f)) {
      index++
      if (index > 1000) index = 0
    }
  }
}