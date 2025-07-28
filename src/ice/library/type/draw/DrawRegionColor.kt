@file:Suppress("UNCHECKED_CAST")

package ice.library.type.draw

import arc.func.Func
import arc.graphics.Color
import arc.graphics.g2d.Draw
import mindustry.gen.Building
import mindustry.world.draw.DrawRegion

class DrawRegionColor<T>(name: String, var color: Func<T, Color?>) : DrawRegion(name) {
    override fun draw(build: Building) {
        val color1 = color[build as T]
        color1?.let {
            Draw.color(it)
        }
        super.draw(build)
        Draw.color()
    }
}