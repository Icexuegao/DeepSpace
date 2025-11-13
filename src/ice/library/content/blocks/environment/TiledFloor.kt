package ice.library.content.blocks.environment

import arc.Core
import arc.graphics.g2d.Draw
import arc.graphics.g2d.TextureRegion
import mindustry.world.Tile

class TiledFloor(name: String, val sizeR: Int) : Floor(name) {
    var verti = arrayOfNulls<TextureRegion>(sizeR)
    val hori = arrayOfNulls<TextureRegion>(sizeR)
    override fun drawBase(tile: Tile) {
        val offxy = sizeR + 1
        if (tile.x % offxy == 0 && tile.y % offxy == 0) {
            Draw.rect(name, tile.worldx(), tile.worldy())
        } else if (tile.x % offxy == 0) {
            Draw.rect(verti[tile.y % sizeR], tile.worldx(), tile.worldy())
        } else if (tile.y % offxy == 0) {
            Draw.rect(hori[tile.y % sizeR], tile.worldx(), tile.worldy())
        } else {
            Draw.rect(variantRegions[variant(tile.x.toInt(), tile.y.toInt())], tile.worldx(), tile.worldy())
        }

        Draw.alpha(1.0f)
        this.drawEdges(tile)
        this.drawOverlay(tile)
    }

    override fun load() {
        super.load()
        (1..sizeR).forEach {
            verti[it - 1] = Core.atlas.find("$name-edge-verti-$it")
            hori[it - 1] = Core.atlas.find("$name-edge-hori-$it")
        }
    }
}