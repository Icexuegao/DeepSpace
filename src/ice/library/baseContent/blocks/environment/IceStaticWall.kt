package ice.library.baseContent.blocks.environment

import mindustry.world.blocks.environment.StaticWall

open class IceStaticWall(name: String, variants: Int = 2) : StaticWall(name) {
    init {
        this.variants = variants
    }
}