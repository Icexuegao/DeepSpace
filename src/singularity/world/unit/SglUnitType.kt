package singularity.world.unit

import arc.util.Nullable
import arc.util.io.Reads
import arc.util.io.Writes
import ice.entities.IceRegister
import mindustry.gen.Unit
import mindustry.type.ItemStack
import mindustry.type.UnitType

open class SglUnitType<T : Unit>(name: String,private val clazz: Class<T>) : UnitType(name) {
  @Nullable
  var requirements: Array<ItemStack>? = null
  fun requirements(vararg req: Any?) {
    requirements = ItemStack.with(*req)
  }

  init {
    constructor= IceRegister.getPutUnits(clazz)
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

  open fun version(): Int {
    return 0
  }

  open fun init(unit: T) {}

  open fun read(sglUnitEntity: T, read: Reads, revision: Int) {
  }

  open fun write(sglUnitEntity: T, write: Writes) {
  }
}