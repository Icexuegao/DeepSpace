package ice.world.content.blocks.crafting.oreMultipleCrafter

import arc.struct.Seq
import mindustry.type.Item
import mindustry.type.ItemStack
import mindustry.world.consumers.Consume
import mindustry.world.consumers.ConsumeItems

open class OreFormula {
    var crftTime = 60f
    var input = Seq<Consume>()
    var output = HashMap<ItemStack, Int>()
    fun addOutput(item: Item, amount: Int, chance: Int) {
        addOutput(ItemStack(item, amount), chance)
    }

    fun addOutput(items: ItemStack, chance: Int) {
        output[items] = chance
    }

    fun addInput(vararg items:Any) {
        addInputs(*ItemStack.with(*items))
    }

    fun addInputs(vararg items: ItemStack) {
        input.add(ConsumeItems(items))
    }
    fun addInput(vararg items: Consume) {
        input.add(items)
    }
}


