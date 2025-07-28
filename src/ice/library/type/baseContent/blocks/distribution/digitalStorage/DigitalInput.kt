package ice.library.type.baseContent.blocks.distribution.digitalStorage

import arc.func.Prov
import ice.library.type.baseContent.blocks.abstractBlocks.IceBlock
import mindustry.gen.Building
import mindustry.type.Item

class DigitalInput(name: String) : IceBlock(name) {
    init {
        size = 1
        solid = true
        health = 100
        update = false
        hasItems = false
        unloadable = false
        destructible = true
        canOverdrive = false
        buildType = Prov(::DigitalInputBuild)
    }

    inner class DigitalInputBuild : IceBuild() {
        var building: DigitalStorage.DigitalStorageBuild? = null
        override fun acceptItem(source: Building, item: Item): Boolean {
            return building?.acceptItem(source, item) ?: false
        }

        override fun handleItem(source: Building, item: Item) {
            building?.handleItem(source, item)
        }
    }
}