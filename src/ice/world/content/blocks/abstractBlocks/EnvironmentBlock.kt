package ice.world.content.blocks.abstractBlocks

import arc.graphics.g2d.TextureRegion
import ice.library.EventType
import ice.library.IFiles
import mindustry.world.Block

open class EnvironmentBlock(name: String) : Block(name) {
    init {
        EventType.addAtlasPackEvent {
            Variants.setBlockVariants(this)
        }
    }

    override fun icons(): Array<TextureRegion> {
        return arrayOf(IFiles.findPng("${name}1"))
    }
}