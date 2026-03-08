package ice.world.draw

import arc.Core
import arc.graphics.g2d.TextureRegion
import mindustry.world.Block
import mindustry.world.draw.DrawBlock

class DrawFull(val name: String) : DrawBlock() {
  lateinit var region: TextureRegion
  override fun load(block: Block) {
    super.load(block)
    region = Core.atlas.find(block.name+"-$name")
  }

  override fun icons(block: Block?): Array<out TextureRegion?> {
    return arrayOf(region)
  }
}