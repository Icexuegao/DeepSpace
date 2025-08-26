package ice.library.baseContent.blocks.distribution.digitalStorage

import arc.Events
import arc.func.Prov
import ice.library.EventType
import ice.library.baseContent.blocks.abstractBlocks.IceBlock
import mindustry.gen.Building
import mindustry.type.Item

class LogisticsInput(name: String) : IceBlock(name) {
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
        var original: LogisticsHub.DigitalStorageBuild? = null
        override fun updateProximity() {
            super.updateProximity()
            Events.fire(EventType.LogisticsHubFire())
        }
        override fun acceptItem(source: Building, item: Item): Boolean {
            return original?.acceptItem(source, item) ?: false
        }

        override fun handleItem(source: Building, item: Item) {
            original?.handleItem(source, item)
        }
    }
}