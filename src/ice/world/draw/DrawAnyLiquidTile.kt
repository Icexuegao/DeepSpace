package ice.world.draw

import mindustry.gen.Building
import mindustry.world.Block
import mindustry.world.blocks.liquid.LiquidBlock
import mindustry.world.draw.DrawBlock

open class DrawAnyLiquidTile : DrawBlock {
    var padding: Float = 0f
    var padLeft: Float = -1f
    var padRight: Float = -1f
    var padTop: Float = -1f
    var padBottom: Float = -1f
    var alpha: Float = 1f

    constructor(padding: Float) {
        this.padding = padding
    }

    constructor()

    override fun draw(build: Building) {
        build.tile.floor().liquidDrop?.let {
            LiquidBlock.drawTiledFrames(build.block.size, build.x, build.y, padLeft, padRight, padTop, padBottom, it, build.warmup() * alpha)
        }
    }

    override fun load(block: Block?) {
        if (padLeft < 0) padLeft = padding
        if (padRight < 0) padRight = padding
        if (padTop < 0) padTop = padding
        if (padBottom < 0) padBottom = padding
    }
}