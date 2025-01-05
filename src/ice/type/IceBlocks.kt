@file:Suppress("LeakingThis")

package ice.type

import arc.Core
import arc.graphics.g2d.TextureRegion
import arc.struct.ObjectMap
import mindustry.world.blocks.environment.Floor
import mindustry.world.blocks.environment.StaticWall

/**基础继承类集合*/
object IceBlocks {

    val floors = ObjectMap<String, Floor>()


    open class IceStaticWall(name: String) : StaticWall(name) {
        init {
            val floor = floors[name.replace("Wall", "")]
            if (floor != null) floor.asFloor().wall = this
        }
    }

    open class IceFloor(name: String, variants: Int) : Floor(name, variants) {
        init {
            floors.put(name, this)
        }

        override fun icons(): Array<TextureRegion> {
            return if (variants == 0) arrayOf(Core.atlas.find(name)) else arrayOf(Core.atlas.find(name + "1"))
        }
    }
}
