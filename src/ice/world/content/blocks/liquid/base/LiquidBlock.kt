package ice.world.content.blocks.liquid.base

import arc.Core
import arc.func.Prov
import arc.graphics.g2d.TextureRegion
import universecore.world.draw.DrawBuild
import universecore.world.draw.DrawMulti
import mindustry.graphics.Drawf
import mindustry.world.draw.DrawRegion
import mindustry.world.meta.BlockGroup
import mindustry.world.meta.Env
import singularity.world.blocks.SglBlock

open class LiquidBlock(name: String) :SglBlock(name) {
  val liquidRegion: TextureRegion by lazy { Core.atlas.find("${this.name}-liquid") }

  init {
    update = true
    solid = true
    hasLiquids = true
    group = BlockGroup.liquids
    outputsLiquid = true
    envEnabled = envEnabled or (Env.space or Env.underwater)
    buildType = Prov(::LiquidBuild)
    drawers = DrawMulti(DrawRegion("-bottom"), DrawBuild<LiquidBuild> {
      if (liquids.currentAmount() > 0.001f) {
        Drawf.liquid(liquidRegion, x, y, liquids.currentAmount() / liquidCapacity, liquids.current().color)
      }
    }, DrawRegion("-top"))
  }

  open inner class LiquidBuild :SglBuilding()
}