package singularity.world.unit

import arc.func.Prov
import arc.util.Nullable
import arc.util.io.Reads
import arc.util.io.Writes
import mindustry.gen.Unit
import mindustry.gen.UnitEntity
import mindustry.type.ItemStack
import mindustry.type.UnitType

open class SglUnitType<T : Unit>(name: String) : UnitType(name) {
    init {
        constructor = Prov(UnitEntity::create)
    }
    @Nullable
    var requirements: Array<ItemStack>? = null

    fun requirements(vararg req: Any?) {
        requirements = ItemStack.with(*req)
    }

    override fun getRequirements(prevReturn: Array<UnitType?>?, timeReturn: FloatArray?): Array<ItemStack>? {
        if (requirements == null) return super.getRequirements(prevReturn, timeReturn)

        if (totalRequirements != null) return totalRequirements

        totalRequirements = requirements
        buildTime = 0f
        if (prevReturn != null) prevReturn[0] = null

        for (stack in requirements) {
            buildTime += stack.item.cost * stack.amount
        }
        if (timeReturn != null) timeReturn[0] = buildTime

        return requirements
    }

    fun version(): Int {
        return 0
    }

    open fun init(unit: SglUnitEntity) {
    }

    open fun read(sglUnitEntity: Unit, read: Reads?, revision: Int) {
    }

    open fun write(sglUnitEntity: Unit, write: Writes?) {
    }

}
