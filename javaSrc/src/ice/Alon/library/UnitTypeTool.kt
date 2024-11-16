package ice.Alon.library

import arc.func.Prov
import mindustry.gen.EntityMapping
import mindustry.gen.Unit

class UnitTypeTool {
    companion object {
        fun entityConstructor(name: String): Prov<Unit>? {
            val map = EntityMapping.map(name)
            val unit = map.get()
            return if (unit is Unit) Prov { unit } else null
        }
    }
}