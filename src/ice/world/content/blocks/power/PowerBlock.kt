package ice.world.content.blocks.power

import ice.world.content.blocks.abstractBlocks.IceBlock
import mindustry.world.meta.BlockGroup

open class PowerBlock(name: String) : IceBlock(name) {
    init {
        update = true
        solid = true
        hasPower = true
        group = BlockGroup.power
    }
}