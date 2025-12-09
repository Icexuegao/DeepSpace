package ice.world.content.blocks.environment

import arc.graphics.g2d.Draw
import arc.math.Mathf
import ice.world.content.blocks.abstractBlocks.IceBlock
import mindustry.graphics.Layer
import mindustry.world.Tile
import kotlin.math.max

open class TallBlock(name: String) : IceBlock(name) {
    var shadowOffset: Float = -3f
    var layer: Float = Layer.power + 1
    var shadowLayer: Float = Layer.power - 1
    var rotationRand: Float = 20f
    var shadowAlpha: Float = 0.6f

    init {
        solid = true
        clipSize = 90f
    }

    override fun drawBase(tile: Tile) {
        val rot = Mathf.randomSeedRange((tile.pos() + 1).toLong(), rotationRand)

        Draw.z(shadowLayer)
        Draw.color(0f, 0f, 0f, shadowAlpha)
        Draw.rect(if (variants > 0) variantShadowRegions[Mathf.randomSeed(tile.pos().toLong(), 0,
            max(0, variantShadowRegions.size - 1))] else customShadowRegion, tile.worldx() + shadowOffset,
            tile.worldy() + shadowOffset, rot)

        Draw.color()

        Draw.z(layer)
        Draw.rect(if (variants > 0) variantRegions[Mathf.randomSeed(tile.pos().toLong(), 0,
            max(0, variantRegions.size - 1))] else region, tile.worldx(), tile.worldy(), rot)
    }

    override fun drawShadow(tile: Tile) {
    }

}