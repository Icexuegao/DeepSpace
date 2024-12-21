@file:Suppress("LeakingThis")

package ice.type

import arc.Core
import arc.graphics.g2d.TextureRegion
import arc.struct.ObjectMap
import arc.struct.Seq
import mindustry.world.Block
import mindustry.world.blocks.environment.Floor
import mindustry.world.blocks.environment.StaticWall
import mindustry.world.meta.BuildVisibility

/**基础继承类集合*/
class IceBlocks {
    companion object {
        val floors = ObjectMap<String, Floor>()
        val iceBlocks = Seq<IceBlock>()
    }

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

    open class IceBlock(name: String) : Block(name) {
        constructor() : this("text${iceBlocks.size}") {
            iceBlocks.add(this)
            buildVisibility = BuildVisibility.shown
            size = 2
            health = 20
            hasItems = true
            hasLiquids = true
            itemCapacity = 100
        }
    }
}
