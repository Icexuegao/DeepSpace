package ice.library.baseContent.blocks.environment

import arc.graphics.g2d.Draw
import arc.math.Mathf
import mindustry.type.Item
import mindustry.world.Tile
import mindustry.world.blocks.environment.OreBlock
import kotlin.math.max

class IceOreBlock(name: String, ore: Item) : OreBlock("ORE$name", ore) {
    init {
        useColor = true
        variants = 3
        mapColor = itemDrop.color
    }
    override fun drawBase(tile: Tile) {
        if (Mathf.randomSeed(tile.pos().toLong(),1,2)==1){
            Draw.rect(
                variantRegions[Mathf.randomSeed(
                    tile.pos().toLong(), 0, max(0, (variantRegions.size - 1))
                )], tile.worldx(), tile.worldy()
            )
        }
    }
}