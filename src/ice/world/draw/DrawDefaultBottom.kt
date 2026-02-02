package ice.world.draw

import arc.graphics.g2d.Draw
import arc.graphics.g2d.TextureRegion
import arc.util.Eachable
import ice.library.IFiles
import mindustry.entities.units.BuildPlan
import mindustry.gen.Building
import mindustry.world.Block
import mindustry.world.draw.DrawBlock

class DrawDefaultBottom : DrawBlock() {
  lateinit var bottom: TextureRegion
  override fun draw(build: Building) {
    Draw.rect(bottom, build.x, build.y, build.rotation * 90f)
  }

  override fun load(block: Block) {
    bottom = IFiles.findModPng("bottom_" + block.size)
  }

  override fun drawPlan(block: Block, plan: BuildPlan, list: Eachable<BuildPlan>) {
    Draw.rect(bottom, plan.drawx(), plan.drawy())
  }

  override fun icons(block: Block): Array<out TextureRegion> {
    return arrayOf(bottom)
  }
}