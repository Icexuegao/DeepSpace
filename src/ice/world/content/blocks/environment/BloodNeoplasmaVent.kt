package ice.world.content.blocks.environment

import arc.graphics.g2d.Draw
import arc.graphics.g2d.Fill
import arc.util.Time
import arc.util.Timer
import ice.content.IUnitTypes
import ice.content.block.EnvironmentBlocks
import ice.graphics.IceColor
import mindustry.Vars
import mindustry.content.Blocks
import mindustry.content.Fx
import mindustry.entities.Effect
import mindustry.game.Team
import mindustry.graphics.Layer

class BloodNeoplasmaVent(name: String) : SteamVent(name) {
    init {
        parent = EnvironmentBlocks.肿瘤地
        effect = Effect(140f) { e ->
            Draw.color(e.color, IceColor.r1, e.fin())
            Draw.alpha(e.fslope() * 0.78f)
            val length = 3f + e.finpow() * 20f
            Fx.rand.setSeed(e.id.toLong())
            (0..<Fx.rand.random(8, 13)).forEach { i ->
                Fx.v.trns(Fx.rand.random(360f), Fx.rand.random(length))
                Fill.circle(e.x + Fx.v.x, e.y + Fx.v.y, Fx.rand.random(1.2f, 3.5f) + e.fslope() * 1.1f)
            }
        }.layer(Layer.darkness - 1)
        effectColor = IceColor.r2
        effectSpacing = 5 * 60f
    }

    override fun renderUpdate(state: UpdateRenderState) {
        return
        if (state.tile.nearby(-1, -1) != null && state.tile.nearby(-1, -1).block() === Blocks.air && (Time.delta.let { state.data += it; state.data }) >= effectSpacing) {
            Timer.schedule({
                effect.at((state.tile.x * Vars.tilesize - Vars.tilesize).toFloat(), (state.tile.y * Vars.tilesize - Vars.tilesize).toFloat(), effectColor)
                effect.at((state.tile.x * Vars.tilesize - Vars.tilesize).toFloat(), (state.tile.y * Vars.tilesize - Vars.tilesize).toFloat(), effectColor)
                Timer.schedule({
                    IUnitTypes.青壤.spawn(Team.crux, state.tile.x * 8f, state.tile.y * 8f)
                }, 1f)
                effect.at((state.tile.x * Vars.tilesize - Vars.tilesize).toFloat(), (state.tile.y * Vars.tilesize - Vars.tilesize).toFloat(), effectColor)
            }, 1f)
            state.data = 0f
        }
    }
}