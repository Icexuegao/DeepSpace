package ice.parse.parses

import arc.struct.ObjectMap
import ice.world.content.unit.IceUnitType
import mindustry.mod.ClassMap

object ClassMap {
    val classmap: ObjectMap<String, Class<*>> = ClassMap.classes

    init {
        classmap.put("IceUnitType", IceUnitType::class.java)
    }
}
