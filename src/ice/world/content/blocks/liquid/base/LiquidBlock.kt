package ice.world.content.blocks.liquid.base

import arc.Core
import arc.func.Prov
import arc.graphics.g2d.Draw
import arc.graphics.g2d.TextureRegion
import ice.world.draw.DrawBuild
import ice.world.draw.DrawMulti
import mindustry.graphics.Drawf
import mindustry.world.meta.BlockGroup
import mindustry.world.meta.Env
import singularity.world.blocks.SglBlock

open class LiquidBlock(name: String) : SglBlock(name) {
    val liquidRegion: TextureRegion by lazy { Core.atlas.find("${this.name}-liquid") }
    val topRegion: TextureRegion by lazy { Core.atlas.find("${this.name}-top") }
    val bottomRegion: TextureRegion by lazy { Core.atlas.find("${this.name}-bottom") }

    init {
        update = true
        solid = true
        hasLiquids = true
        group = BlockGroup.liquids
        outputsLiquid = true
        envEnabled = envEnabled or (Env.space or Env.underwater)
        buildType = Prov(::LiquidBuild)
        drawers= DrawMulti(DrawBuild<LiquidBuild> {
            val rotation = if (rotate) rotdeg() else 0f
            Draw.rect(bottomRegion, x, y, rotation)

            if (liquids.currentAmount() > 0.001f) {
                Drawf.liquid(liquidRegion, x, y, liquids.currentAmount() / liquidCapacity, liquids.current().color)
            }

            Draw.rect(topRegion, x, y, rotation)
        })
    }

    open inner class LiquidBuild : SglBuilding()
}