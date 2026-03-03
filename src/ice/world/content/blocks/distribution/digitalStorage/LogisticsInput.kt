package ice.world.content.blocks.distribution.digitalStorage

import arc.func.Prov
import mindustry.gen.Building
import mindustry.type.Item

class LogisticsInput(name: String) : LogisticsBlock(name) {
    override val blockType = Type.INPUT

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

    inner class DigitalInputBuild : LogisticsBuild() {
        var original: LogisticsHub.DigitalStorageBuild? = null

        override fun acceptItem(source: Building, item: Item): Boolean {
            original = LogisticsGraph.getHub(this)
            return original?.acceptItem(source, item) ?: false
        }

        override fun handleItem(source: Building, item: Item) {
            original?.handleItem(source, item)
        }
    }
}