package ice.world.content.blocks.environment

import arc.graphics.g2d.Draw
import arc.graphics.g2d.TextureRegion
import arc.math.Mathf
import arc.math.geom.Vec2
import arc.util.Time
import mindustry.graphics.BlockRenderer
import mindustry.world.Tile
import kotlin.math.max

class Seaweed(name: String) : Prop(name) {
    val rotmag = 3f
    val rotscl = 0.5f
    val scl = 30f
    val mag = 0.3f
    override fun drawBase(tile: Tile) {
        val region = if (variants > 0) variantRegions[Mathf.randomSeed(tile.pos().toLong(), 0, max(0, variantRegions.size - 1))] else this.region
        drawBase(tile, region)
    }

    fun drawBase(tile: Tile, region: TextureRegion) {
        val x = tile.worldx()
        val y = tile.worldy()
        val rot = Mathf.randomSeedRange(tile.pos().toLong(), 20f) - 45 + Mathf.sin(Time.time + x, 50f * rotscl, 0.5f * rotmag) + Mathf.sin(Time.time - y, 65f * rotscl, 0.9f * rotmag) + Mathf.sin(Time.time + y - x, 85f * rotscl, 0.9f * rotmag)
        val w = region.width * region.scl()
        val h = region.height * region.scl()

        Draw.rectv(region, x, y, w, h, rot) { vec: Vec2 ->
            vec.add(Mathf.sin(vec.y * 3 + Time.time, scl, mag) + Mathf.sin(vec.x * 3 - Time.time, 70f, 0.8f), Mathf.cos(vec.x * 3 + Time.time + 8, scl + 6f, mag * 1.1f) + Mathf.sin(vec.y * 3 - Time.time, 50f, 0.2f))
        }
    }

    override fun drawShadow(tile: Tile) {
        val region1 = if (variants == 0) customShadowRegion else variantShadowRegions[Mathf.randomSeed(tile.pos().toLong(), 0, max(0, variantShadowRegions.size - 1))]
        Draw.color(0f, 0f, 0f, BlockRenderer.shadowColor.a)
        drawBase(tile, region1)
        Draw.color()
    }
}
