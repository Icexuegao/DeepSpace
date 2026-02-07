package ice.world.content.blocks.crafting.multipleCrafter

import arc.struct.Seq
import mindustry.gen.Building
import mindustry.world.Block

class FormulaStack {
    companion object {
        fun with(vararg formulas: Formula): FormulaStack {
            return FormulaStack().addFormulas(*formulas)
        }
    }

    var formulas: Seq<Formula> = Seq()
    fun getFormula(index: Int): Formula {
        return formulas[index]
    }

    fun setFormula(index: Int, formula: Formula) {
        formulas[index] = formula
    }


    fun addFormulas(vararg formulas: Formula): FormulaStack {
        formulas.forEach(this.formulas::add)
        return this
    }

    fun outputItems(index: Int): Boolean {
        return getFormula(index).outputItems != null
    }

    fun outputItems(): Boolean {
        for (f in formulas) {
            if (f.outputItems != null) return true
        }
        return false
    }

    fun outputLiquids(): Boolean {
        for (f in formulas) {
            if (f.outputLiquids != null) return true
        }
        return false
    }

    fun trigger(build: Building) {
        for (f in formulas) {
            f.trigger(build)
        }
    }

    fun apply(block: Block) {
        for (f in formulas) {
            f.apply(block)
        }
    }

    fun size(): Int {
        return formulas.size
    }

}
