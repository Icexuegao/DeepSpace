package ice.library.type.baseContent.item

import ice.library.type.baseContent.blocks.environment.IceOreBlock

open class OreItem(name: String, color: String, hardness: Int) : IceItem(name, color) {
    init {
        this.hardness = hardness
        IceOreBlock(name,this)
    }
}