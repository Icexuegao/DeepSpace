package ice.library.draw.drawer

import arc.graphics.g2d.TextureRegion
import arc.struct.Seq
import arc.util.Eachable
import mindustry.entities.units.BuildPlan
import mindustry.gen.Building
import mindustry.world.Block
import mindustry.world.draw.DrawBlock

class IceDrawMulti(vararg var drawers: DrawBlock) : DrawBlock() {
    override fun getRegionsToOutline(block: Block, out: Seq<TextureRegion>) {
        for (draw in drawers) {
            draw.getRegionsToOutline(block, out)
        }
    }

    override fun draw(build: Building) {
        for (draw in drawers) {
            draw.draw(build)
        }
    }

    override fun drawPlan(block: Block, plan: BuildPlan, list: Eachable<BuildPlan>) {
        for (draw in drawers) {
            draw.drawPlan(block, plan, list)
        }
    }

    override fun drawLight(build: Building) {
        for (draw in drawers) {
            draw.drawLight(build)
        }
    }

    override fun load(block: Block) {
        for (draw in drawers) {
            draw.load(block)
        }
    }

    override fun icons(block: Block): Array<TextureRegion> {
        val result = Seq<TextureRegion>()
        for (draw in drawers) {
            result.addAll(*draw.icons(block))
        }
        return result.toArray(TextureRegion::class.java)
    }
}