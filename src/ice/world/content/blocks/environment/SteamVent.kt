package ice.world.content.blocks.environment

import arc.graphics.Color
import arc.graphics.g2d.Draw
import arc.math.Mathf
import arc.math.geom.Point2
import arc.struct.EnumSet
import arc.util.Time
import mindustry.Vars
import mindustry.Vars.tilesize
import mindustry.content.Blocks
import mindustry.content.Fx
import mindustry.entities.Effect
import mindustry.graphics.Pal
import mindustry.world.Block
import mindustry.world.Tile
import mindustry.world.meta.BlockFlag
import kotlin.math.max

open class SteamVent(name: String) : Floor(name) {
    companion object {
        val offsets: Array<Point2> = arrayOf<Point2>(
            Point2(0, 0),
            Point2(1, 0),
            Point2(1, 1),
            Point2(0, 1),
            Point2(-1, 1),
            Point2(-1, 0),
            Point2(-1, -1),
            Point2(0, -1),
            Point2(1, -1),
        )

        init {
            for (p in offsets) {
                p.sub(1, 1)
            }
        }
    }
    var parent: Block? = Blocks.air
    var effect: Effect = Fx.ventSteam
    var effectColor: Color? = Pal.vent
    var effectSpacing: Float = 15f

    init {
        flags = EnumSet.of(BlockFlag.steamVent)
    }

    override fun drawMain(tile: Tile) {
        if (parent is Floor) {
            (parent as mindustry.world.blocks.environment.Floor).drawMain(tile)
        }

        if (checkAdjacent(tile)) {
            Draw.rect(variantRegions[Mathf.randomSeed(tile.pos().toLong(), 0, max(0, variantRegions.size - 1))],
                tile.worldx() - tilesize, tile.worldy() - tilesize)
        }
    }

    override fun updateRender(tile: Tile): Boolean {
        return checkAdjacent(tile)
    }

    override fun shouldIndex(tile: Tile): Boolean {
        return isCenterVent(tile)
    }

    fun isCenterVent(tile: Tile): Boolean {
        val topRight = tile.nearby(1, 1)
        return topRight != null && topRight.floor() === tile.floor() && checkAdjacent(topRight)
    }

    override fun renderUpdate(state: UpdateRenderState) {
        if (state.tile.nearby(-1, -1) != null && state.tile.nearby(-1, -1)
                .block() === Blocks.air && (Time.delta.let { state.data += it; state.data }) >= effectSpacing
        ) {
            effect.at(state.tile.x * tilesize - tilesize.toFloat(), state.tile.y * tilesize - tilesize.toFloat(),
                effectColor)
            state.data = 0f
        }
    }

    //note that only the top right tile works for this; render order reasons.
    fun checkAdjacent(tile: Tile): Boolean {
        for (point in offsets) {
            val other = Vars.world.tile(tile.x + point.x, tile.y + point.y)
            if (other == null || other.floor() !== this) {
                return false
            }
        }
        return true
    }


}