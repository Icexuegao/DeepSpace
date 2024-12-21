package ice.world.blocks.ore

import arc.struct.Seq
import mindustry.type.Item
import mindustry.type.ItemStack

 open class OreFormula {
    var crftTime = 60
    var input = Seq<ItemStack>()
    var output = HashMap<ItemStack, Int>()
    fun addOutput(item: Item, amount: Int, chance: Int) {
        addOutput(ItemStack(item, amount), chance)
    }

    fun addOutput(items: ItemStack, chance: Int) {
        output[items] = chance
    }

    fun addInput(item: Item, amount: Int) {
        addInput(ItemStack(item, amount))
    }

    fun addInput(items: ItemStack) {
        input.add(items)
    }
     class OreFormulaStack {
         val oreFormula = Seq<OreFormula>()
         fun with(vararg formulas: OreFormula) {
             oreFormula.add(formulas)
         }
     }
}


