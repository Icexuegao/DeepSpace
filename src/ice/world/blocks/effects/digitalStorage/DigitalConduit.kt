package ice.world.blocks.effects.digitalStorage

import arc.func.Prov
import mindustry.content.Items
import mindustry.gen.Building
import mindustry.type.Category
import mindustry.type.ItemStack
import mindustry.world.Block

class DigitalConduit(name: String) : Block(name) {
    init {
        health = 50
        size = 1
        hasItems = false
        destructible = true
        unloadable = false
        hasPower = true
        acceptsItems = false
        requirements(Category.effect, ItemStack.with(Items.copper, 2))
        buildType = Prov(::DigitalConduitBuild)
        update = false
        configurable = true
        conductivePower = true
    }

    class DigitalConduitBuild : Building() {
        var building: DigitalStorage.DigitalStorageBuild? = null
    }
}