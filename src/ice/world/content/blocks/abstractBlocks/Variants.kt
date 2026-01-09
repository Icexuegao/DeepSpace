package ice.world.content.blocks.abstractBlocks

import ice.library.IFiles
import mindustry.world.Block

object Variants {
    fun setBlockVariants(block: Block) {
        var variant = 0
        while (IFiles.hasPng("${block.name}${variant + 1}")) {
            variant++
        }
        block.variants = variant
        if (IFiles.hasPng("${block.name}-shadow1") || IFiles.hasPng("${block.name}-shadow")) block.customShadow = true
    }
}