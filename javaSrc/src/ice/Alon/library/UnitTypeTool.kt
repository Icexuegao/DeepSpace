package ice.Alon.library

import arc.func.Prov
import mindustry.gen.EntityMapping
import mindustry.gen.Unit

class UnitTypeTool {
    companion object {
        fun entityConstructor(name: String): Prov<Unit>? {
            val map = EntityMapping.map(name)
            return if (map.get() is Unit) Prov { map.get() as Unit } else null
        }
    }
}