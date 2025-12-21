package ice.world.content.blocks.abstractBlocks

import arc.Core
import arc.graphics.g2d.TextureRegion
import ice.library.IFiles
import mindustry.world.Block

open class EnvironmentBlock(name: String) : Block(name) {
    init {
        var variant = 0
        while (IFiles.hasPng("$name${variant + 1}")) {
            variant++
        }
        variants = variant
        if (IFiles.hasPng("$name-shadow1") || IFiles.hasPng("$name-shadow")) customShadow = true
    }

    override fun icons(): Array<TextureRegion> {
        return arrayOf(Core.atlas.find(this.name + "1"))
    }
}