package universecore.world.draw

import mindustry.gen.Building
import mindustry.world.draw.DrawBlock

@Suppress("UNCHECKED_CAST")
class DrawBuild<T :Building>(val block: T.() -> Unit) :DrawBlock() {
  override fun draw(build: Building) {
    block(build as T)
  }
}

