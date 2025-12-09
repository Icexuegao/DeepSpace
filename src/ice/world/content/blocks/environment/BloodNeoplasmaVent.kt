package ice.world.content.blocks.environment

import arc.graphics.Color
import arc.graphics.g2d.Draw
import arc.graphics.g2d.Fill
import arc.math.Mathf
import arc.math.geom.Point2
import arc.struct.EnumSet
import arc.util.Time
import arc.util.Timer
import ice.content.IUnitTypes
import ice.content.block.Environment
import ice.graphics.IceColor
import mindustry.Vars
import mindustry.content.Blocks
import mindustry.content.Fx
import mindustry.entities.Effect
import mindustry.game.Team
import mindustry.graphics.Layer
import mindustry.world.Tile
import mindustry.world.blocks.environment.Floor
import mindustry.world.meta.BlockFlag
import kotlin.math.max

class BloodNeoplasmaVent(name: String) : ice.world.content.blocks.environment.Floor(name) {
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

    var parent: Floor = Environment.肿瘤地
    var effect: Effect = Effect(140f) { e ->
        Draw.color(e.color, IceColor.r1, e.fin())
        Draw.alpha(e.fslope() * 0.78f)
        val length = 3f + e.finpow() * 20f
        Fx.rand.setSeed(e.id.toLong())
        (0..<Fx.rand.random(8, 13)).forEach { i ->
            Fx.v.trns(Fx.rand.random(360f), Fx.rand.random(length))
            Fill.circle(e.x + Fx.v.x, e.y + Fx.v.y, Fx.rand.random(1.2f, 3.5f) + e.fslope() * 1.1f)
        }
    }.layer(Layer.darkness - 1)
    var effectColor: Color = IceColor.r2
    var effectSpacing: Float = 5 * 60f

    init {
        flags = EnumSet.of(BlockFlag.steamVent)
    }

    override fun drawMain(tile: Tile) {
        parent.drawMain(tile)
        if (checkAdjacent(tile)) {
            Draw.rect(variantRegions[Mathf.randomSeed(tile.pos().toLong(), 0, max(0, variantRegions.size - 1))],
                tile.worldx() - Vars.tilesize, tile.worldy() - Vars.tilesize)
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
        return
        if (state.tile.nearby(-1, -1) != null && state.tile.nearby(-1, -1)
                .block() === Blocks.air && (Time.delta.let { state.data += it; state.data }) >= effectSpacing
        ) {
            Timer.schedule({
                effect.at((state.tile.x * Vars.tilesize - Vars.tilesize).toFloat(),
                    (state.tile.y * Vars.tilesize - Vars.tilesize).toFloat(), effectColor)
                effect.at((state.tile.x * Vars.tilesize - Vars.tilesize).toFloat(),
                    (state.tile.y * Vars.tilesize - Vars.tilesize).toFloat(), effectColor)
                Timer.schedule({
                    IUnitTypes.青壤.spawn(Team.crux, state.tile.x * 8f, state.tile.y * 8f)
                }, 1f)
                effect.at((state.tile.x * Vars.tilesize - Vars.tilesize).toFloat(),
                    (state.tile.y * Vars.tilesize - Vars.tilesize).toFloat(), effectColor)
            }, 1f)
            state.data = 0f
        }
    }

    //请注意，只有右上角的图块适用于此;渲染订单原因。
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