@file:Suppress("UNCHECKED_CAST")

package ice.library.entities

import arc.func.Prov
import arc.struct.ObjectIntMap
import arc.struct.ObjectMap
import ice.DFW
import mindustry.gen.EntityMapping
import mindustry.gen.Entityc
import mindustry.gen.Unit

object IceRegister {
    private val ids = ObjectIntMap<Class<out Entityc>>()
    val map: ObjectMap<String, Prov<out Entityc>> = ObjectMap()
    private fun <T : Entityc> put(name: String, type: Class<T>, prov: Prov<out T>) {
        map.put(name, prov)
        ids.put(type, EntityMapping.register(name, prov))
    }

    private fun getUnit(name: String): Prov<Unit> {
        return map[name] as Prov<Unit>
    }

    fun getId(type: Class<out Entityc>): Int {
        return ids[type, -1]
    }

    internal inline fun <reified T : Entityc> getPutUnit(): Prov<Unit> {
        put(T::class.java.simpleName, T::class.java) {
            T::class.java.getDeclaredConstructor().newInstance()
        }
        return getUnit(T::class.java.simpleName)
    }

    init {
        put("DFW", DFW::class.java) {
            return@put DFW::class.java.getDeclaredConstructor().newInstance()
        }
    }
}
