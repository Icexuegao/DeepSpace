package ice.world.draw

import arc.graphics.g2d.Draw
import mindustry.gen.Building
import mindustry.graphics.Layer
import mindustry.world.draw.DrawBlock

class DrawCracks : DrawBlock() {
    override fun draw(build: Building) {
        Draw.z(Layer.blockCracks)
        build.drawCracks()
        Draw.z(Layer.blockAfterCracks)
    }
}