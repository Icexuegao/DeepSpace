package ice.world.content.blocks.environment

import arc.Core
import arc.graphics.g2d.Draw
import arc.graphics.g2d.TextureRegion
import arc.math.Mathf
import arc.math.geom.Point2
import mindustry.Vars
import mindustry.content.Fx
import mindustry.graphics.CacheLayer
import mindustry.world.Block
import mindustry.world.Tile
import mindustry.world.blocks.environment.StaticWall
import kotlin.math.max

open class StaticWall(name: String) : Prop(name) {
    var large: TextureRegion? = null
    var split: Array<Array<TextureRegion>>? = null

    init {
        solid = true
        breakable = false
        instantBuild = true
        alwaysReplace = false
        placeableLiquid = true
        unitMoveBreakable = false
        ignoreBuildDarkness = true
        cacheLayer = CacheLayer.walls
        allowRectanglePlacement = true
        placeEffect = Fx.rotateBlock
    }

    override fun drawBase(tile: Tile) {
        val rx = tile.x / 2 * 2
        val ry = tile.y / 2 * 2

        if (split != null && eq(rx, ry) && Mathf.randomSeed(
                Point2.pack(rx, ry).toLong()) < 0.5 && split!!.size >= 2 && split!![0].size >= 2
        ) {
            Draw.rect(split!![tile.x % 2][1 - tile.y % 2], tile.worldx(), tile.worldy())
        } else if (variants > 0) {
            Draw.rect(variantRegions[Mathf.randomSeed(tile.pos().toLong(), 0, max(0, variantRegions.size - 1))],
                tile.worldx(), tile.worldy())
        } else {
            Draw.rect(region, tile.worldx(), tile.worldy())
        }

        if (tile.overlay().wallOre) {
            tile.overlay().drawBase(tile)
        }
    }

    override fun load() {
        super.load()
        if (Core.atlas.has("$name-large")) {
            large = Core.atlas.find("$name-large")?.let { lge ->
                val size = lge.width / 2
                split = lge.split(size, size)
                if (split != null) {
                    for (arr in split) {
                        for (reg in arr) {
                            reg.scale = region.scale
                        }
                    }
                }
                lge
            }
        }
    }

    override fun canReplace(other: Block?): Boolean {
        return other is StaticWall || super.canReplace(other)
    }

    fun eq(rx: Int, ry: Int): Boolean {
        val tile = Vars.world.tile(rx + 1, ry)
        val tile1 = Vars.world.tile(rx, ry)
        val tile2 = Vars.world.tile(rx, ry + 1)
        val tile3 = Vars.world.tile(rx + 1, ry + 1)
        return rx < Vars.world.width() - 1 && ry < Vars.world.height() - 1 &&
                tile
                    .block() === this && tile2.block() === this && tile1
            .block() === this && tile3.block() === this
    }
}