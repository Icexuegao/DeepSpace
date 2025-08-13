package ice.library.baseContent.blocks.environment

import arc.graphics.g2d.Draw
import arc.math.Mathf
import mindustry.world.Tile

class TiledFloor(name: String, val sizeR: Int, variants: Int) : IceFloor(name, variants) {

    override fun drawBase(tile: Tile) {
        if (tile.x % (sizeR + 1) == 0 && tile.y % (sizeR + 1) == 0) {
            Draw.rect(name, tile.worldx(), tile.worldy())
        } else if (tile.x % (sizeR + 1) == 0) {
            Draw.rect(
                "$name-edge-verti-${tile.y % (sizeR + 1)}",
                tile.worldx(),
                tile.worldy()
            )
        } else if (tile.y % (sizeR + 1) == 0) {
            Draw.rect(
                "$name-edge-hori-${tile.x % (sizeR + 1)}",
                tile.worldx(),
                tile.worldy()
            )
        } else {
            Mathf.rand.setSeed(tile.pos().toLong())
            Draw.rect(variantRegions[variant(tile.x.toInt(), tile.y.toInt())], tile.worldx(), tile.worldy())
        }

        Draw.alpha(1.0f)
        this.drawEdges(tile)
        this.drawOverlay(tile)
    }
}