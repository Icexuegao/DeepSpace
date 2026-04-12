package singularity.world.draw

import arc.Core
import arc.graphics.g2d.Draw
import arc.graphics.g2d.TextureRegion
import arc.util.Eachable
import mindustry.entities.units.BuildPlan
import mindustry.gen.Building
import mindustry.graphics.Layer
import mindustry.world.Block
import mindustry.world.draw.DrawBlock
import singularity.Singularity

class DrawBottom :DrawBlock() {
  lateinit var bottom: TextureRegion

  override fun load(block: Block) {
    bottom = Core.atlas.find(block.name + "_bottom", Singularity.getModAtlas("bottom_" + block.size))
  }

  override fun draw(build: Building) {
    val z = Draw.z()
    Draw.z(Layer.blockUnder)
    Draw.rect(bottom, build.x, build.y)
    Draw.z(z)
  }

  override fun drawPlan(block: Block, plan: BuildPlan, list: Eachable<BuildPlan>) {
    Draw.rect(bottom, plan.drawx(), plan.drawy())
  }

  override fun icons(block: Block) = arrayOf(bottom)
}