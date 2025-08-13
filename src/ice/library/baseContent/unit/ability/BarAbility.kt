package ice.library.baseContent.unit.ability

import arc.func.Cons2
import arc.scene.ui.layout.Table
import mindustry.entities.abilities.Ability
import mindustry.gen.Unit

class BarAbility<T : Unit> : Ability() {
    lateinit var cons: Cons2<T, Table>
    fun cons(cons: Cons2<T, Table>): BarAbility<T> {
        this.cons = cons
        return this
    }

    @Suppress("UNCHECKED_CAST")
    override fun displayBars(unit1: Unit, bars: Table) {
        super.displayBars(unit1, bars)
        cons.get(unit1 as T, bars)
    }
}