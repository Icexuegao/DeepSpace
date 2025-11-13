package ice.library.draw.drawer

import arc.Core
import arc.graphics.g2d.TextureRegion
import mindustry.gen.Building
import mindustry.graphics.Drawf
import mindustry.type.Liquid
import mindustry.world.Block
import mindustry.world.draw.DrawBlock

class DrawLiquidRegion(drawLiquid: Liquid? = null) : DrawBlock() {
    var drawLiquid: Liquid? = null
    var liquid: TextureRegion? = null
    var suffix: String = "-liquid"
    var alpha: Float = 1f

    init {
        this.drawLiquid = drawLiquid
    }

    override fun draw(build: Building) {
        val drawn: Liquid = drawLiquid ?: build.liquids.current()
        Drawf.liquid(liquid, build.x, build.y, build.liquids.get(drawn) / build.block.liquidCapacity * alpha,
            drawn.color)
    }

    override fun load(block: Block) {
        liquid = Core.atlas.find(block.name + suffix)
    }
}
