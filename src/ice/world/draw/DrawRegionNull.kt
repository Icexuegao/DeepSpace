package ice.world.draw

import arc.Core
import arc.graphics.Color
import arc.graphics.g2d.Draw
import arc.graphics.g2d.TextureRegion
import arc.util.Eachable
import arc.util.Nullable
import mindustry.entities.units.BuildPlan
import mindustry.gen.Building
import mindustry.graphics.Drawf
import mindustry.world.Block
import mindustry.world.draw.DrawBlock

class DrawRegionNull(val suffix: String = "", var rotateSpeed: Float = 0f, var spinSprite: Boolean = false) : DrawBlock() {
    lateinit var region: TextureRegion

    /** If set, overrides the region name.  */
    @Nullable
    var name: String? = null

    @Nullable
    var color: Color? = null

    var drawPlan: Boolean = true
    var buildingRotate: Boolean = false

    var x: Float = 0f
    var y: Float = 0f
    var rotation: Float = 0f

    /** Any number <=0 disables layer changes.  */
    var layer: Float = -1f


    override fun draw(build: Building) {
        if (region.found()) {
            val z = Draw.z()
            if (layer > 0) Draw.z(layer)
            if (color != null) Draw.color(color)
            if (spinSprite) {
                Drawf.spinSprite(
                    region, build.x + x, build.y + y, build.totalProgress() * rotateSpeed + rotation + (if (buildingRotate) build.rotdeg() else 0f)
                )
            } else {
                Draw.rect(
                    region, build.x + x, build.y + y, build.totalProgress() * rotateSpeed + rotation + (if (buildingRotate) build.rotdeg() else 0f)
                )
            }
            if (color != null) Draw.color()
            Draw.z(z)
        }
    }

    override fun drawPlan(block: Block?, plan: BuildPlan, list: Eachable<BuildPlan?>?) {
        if (!drawPlan) return
        if (region.found()) {
            if (spinSprite) {
                Drawf.spinSprite(
                    region, plan.drawx() + x, plan.drawy() + y, (if (buildingRotate) plan.rotation * 90f else 0 + rotation)
                )
            } else {
                Draw.rect(
                    region, plan.drawx() + x, plan.drawy() + y, (if (buildingRotate) plan.rotation * 90f else 0 + rotation)
                )
            }
        }
    }

    override fun icons(block: Block): Array<TextureRegion?> {
        return if (region.found()) arrayOf(region) else arrayOf()
    }

    override fun load(block: Block) {
        region = Core.atlas.find(block.name + suffix)
    }
}