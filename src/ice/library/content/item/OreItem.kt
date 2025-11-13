package ice.library.content.item

import ice.library.content.blocks.environment.IceOreBlock
import mindustry.world.blocks.environment.OreBlock

open class OreItem(name: String, color: String, hardness: Int, appls: OreItem.() -> Unit) : IceItem(name, color) {
    val oreBlock: OreBlock

    init {
        appls(this)
        this.hardness = hardness
        oreBlock = IceOreBlock(name, this)
    }
}