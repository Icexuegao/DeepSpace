package ice.world.blocks.distribution.digitalStorage

import arc.func.Prov
import mindustry.content.Items
import mindustry.gen.Building
import mindustry.type.Category
import mindustry.type.Item
import mindustry.type.ItemStack
import mindustry.world.Block

class DigitalInput(name: String) : Block(name) {
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
        requirements(Category.distribution, ItemStack.with(Items.copper, 100))
    }

    class DigitalInputBuild : Building() {
        var building: DigitalStorage.DigitalStorageBuild? = null

        override fun acceptItem(source: Building, item: Item): Boolean {
            return building?.acceptItem(source, item) ?: false
        }

        override fun handleItem(source: Building, item: Item) {
            building?.handleItem(source, item)
        }
    }
}