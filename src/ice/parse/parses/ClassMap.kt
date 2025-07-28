package ice.parse.parses

import arc.struct.ObjectMap
import ice.library.type.baseContent.unit.type.IceUnitType
import mindustry.mod.ClassMap

object ClassMap {
    val classmap: ObjectMap<String, Class<*>> = ClassMap.classes

    init {
        classmap.put("IceUnitType", IceUnitType::class.java)
    }
}
