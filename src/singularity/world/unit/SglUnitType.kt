package singularity.world.unit

import arc.util.Nullable
import arc.util.io.Reads
import arc.util.io.Writes
import ice.entities.EntityRegistry
import mindustry.gen.Unit
import mindustry.type.ItemStack
import mindustry.type.UnitType

open class SglUnitType<T :Unit>(name: String, clazz: Class<T>) :UnitType(name) {
  @Nullable var requirements: Array<ItemStack>? = null
  fun requirements(vararg req: Any?) {
    requirements = ItemStack.with(*req)
  }

  init {
    constructor = EntityRegistry.getPutUnits(clazz)
  }

  open fun init(unit: T) {}

  open fun read(sglUnitEntity: T, read: Reads, revision: Int) {
  }

  open fun write(sglUnitEntity: T, write: Writes) {
  }
}