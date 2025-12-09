package ice.world.content.blocks.environment

import arc.func.Cons
import arc.graphics.g2d.Draw
import arc.math.Mathf
import arc.util.Time
import ice.world.content.blocks.abstractBlocks.IceBlock
import mindustry.graphics.Layer
import mindustry.world.Tile
import kotlin.math.max

class TreeBlock(name: String) : IceBlock(name) {
    var shadowOffset = -4f
    var updateFun: Cons<Tile> = Cons {}

    init {
        solid = true
        clipSize = 90f
    }

    fun setUpdate(update: Cons<Tile>) {
        this.updateFun = update
    }

    override fun drawBase(tile: Tile) {
        val x = tile.worldx()
        val y = tile.worldy()
        val rot = Mathf.randomSeed(tile.pos().toLong(), 0, 4) * 90 + Mathf.sin(Time.time + x, 50f, 0.5f) + Mathf.sin(
            Time.time - y, 65f, 0.9f) + Mathf.sin(Time.time + y - x, 85f, 0.9f)
        val w = region.width * region.scl()
        val h = region.height * region.scl()
        val scl = 30f
        val mag = 0.2f
        val shad = if (variants == 0) customShadowRegion else variantShadowRegions[Mathf.randomSeed(tile.pos().toLong(),
            0, max(0, variantShadowRegions.size - 1))]

        if (shad.found()) {
            Draw.z(Layer.power - 1)
            Draw.rect(shad, tile.worldx() + shadowOffset, tile.worldy() + shadowOffset, rot)
        }
        val reg = if (variants == 0) region else variantRegions[Mathf.randomSeed(tile.pos().toLong(), 0,
            max(0, variantRegions.size - 1))]

        Draw.z(Layer.power + 1)
        Draw.rectv(reg, x, y, w, h, rot) { vec ->
            vec.add(Mathf.sin(vec.y * 3 + Time.time, scl, mag) + Mathf.sin(vec.x * 3 - Time.time, 70f, 0.8f),
                Mathf.cos(vec.x * 3 + Time.time + 8, scl + 6f, mag * 1.1f) + Mathf.sin(vec.y * 3 - Time.time, 50f,
                    0.2f))
        }
        updateFun.get(tile)

    }

/**
 * 绘制阴影的方法
 * @param tile 需要绘制阴影的瓦片对象
 * @return Unit 该方法不返回任何值
 */
    override fun drawShadow(tile: Tile) = Unit
}