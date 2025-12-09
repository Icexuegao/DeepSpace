package ice.world.content.unit.ability

import arc.func.Cons2
import arc.scene.ui.layout.Table
import mindustry.entities.abilities.Ability
import mindustry.gen.Unit

class BarAbility<T : Unit>(var cons: Cons2<T, Table>) : Ability() {

    init {
        display = false
    }

    @Suppress("UNCHECKED_CAST")
    override fun displayBars(unit1: Unit, bars: Table) {
        super.displayBars(unit1, bars)
        cons.get(unit1 as T, bars)
    }

}