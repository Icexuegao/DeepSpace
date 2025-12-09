package ice.world.draw

import arc.Core
import arc.graphics.Blending
import arc.graphics.Color
import arc.graphics.g2d.Draw
import arc.graphics.g2d.TextureRegion
import arc.math.Mathf
import arc.util.Time
import mindustry.gen.Building
import mindustry.world.Block
import mindustry.world.draw.DrawBlock

class DrawRim: DrawBlock() {
    val s: Float = 0.3f
    val ts: Float = 0.6f
    var heatColor: Color = Color.valueOf("ff5512")

    var rimRegion: TextureRegion? = null

    override fun draw(build: Building) {
        rimRegion?.let {
            Draw.color(heatColor)
            Draw.alpha(build.warmup() * ts * (1f - s + Mathf.absin(Time.time, 3f, s)))
            Draw.blend(Blending.additive)
            Draw.rect(it, build.x, build.y)
            Draw.blend()
            Draw.reset()
        }
    }

    override fun load(block: Block) {
        rimRegion= Core.atlas.find("${block.name}-rim")
    }

}