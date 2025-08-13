package ice.library.draw.drawer

import mindustry.gen.Building
import mindustry.world.draw.DrawBlock

class DrawBuild<T : Building>(val block: T.() -> Unit) : DrawBlock() {
    override fun draw(build: Building) {
        @Suppress("UNCHECKED_CAST") block(build as T)
        super.draw(build)
    }
}

