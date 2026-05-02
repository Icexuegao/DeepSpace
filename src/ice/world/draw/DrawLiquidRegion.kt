package ice.world.draw

import arc.Core
import arc.graphics.g2d.TextureRegion
import mindustry.gen.Building
import mindustry.graphics.Drawf
import mindustry.type.Liquid
import mindustry.world.Block
import mindustry.world.draw.DrawBlock

open class DrawLiquidRegion(var drawLiquid: Liquid? = null, var suffix: String = "-liquid") :DrawBlock() {
  var liquid: TextureRegion? = null
  var alpha: Float = 1f

  override fun draw(build: Building) {
    val drawn: Liquid = drawLiquid ?: build.liquids?.current() ?: return
    Drawf.liquid(
      liquid, build.x, build.y, build.liquids.get(drawn) / build.block.liquidCapacity * alpha, drawn.color
    )
  }

  override fun load(block: Block) {
    liquid = Core.atlas.find(block.name + suffix)
  }
}
