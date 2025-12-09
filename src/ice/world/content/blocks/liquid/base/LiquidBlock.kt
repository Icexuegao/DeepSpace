package ice.world.content.blocks.liquid.base

import arc.Core
import arc.func.Prov
import arc.graphics.g2d.Draw
import arc.graphics.g2d.TextureRegion
import ice.world.content.blocks.abstractBlocks.IceBlock
import ice.world.draw.DrawBuild
import mindustry.graphics.Drawf
import mindustry.world.meta.BlockGroup
import mindustry.world.meta.Env

open class LiquidBlock(name: String) : IceBlock(name) {

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
        setDrawMulti(DrawBuild<LiquidBuild>{
            val rotation = if (rotate) rotdeg() else 0f
            Draw.rect(bottomRegion, x, y, rotation)

            if (liquids.currentAmount() > 0.001f) {
                Drawf.liquid(liquidRegion, x, y, liquids.currentAmount() / liquidCapacity, liquids.current().color)
            }

            Draw.rect(topRegion, x, y, rotation)
        })
    }

    open inner class LiquidBuild : IceBuild()
}