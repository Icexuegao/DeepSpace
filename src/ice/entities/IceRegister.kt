@file:Suppress("UNCHECKED_CAST")

package ice.entities

import arc.func.Prov
import arc.struct.ObjectIntMap
import arc.struct.ObjectMap
import ice.library.world.Load
import ice.world.content.blocks.distribution.conveyor.PackStack
import mindustry.gen.EntityMapping
import mindustry.gen.Entityc
import mindustry.gen.Unit

object IceRegister: Load {
    private val ids = ObjectIntMap<Class<out Entityc>>()
    private val map: ObjectMap<String, Prov<out Entityc>> = ObjectMap()
    fun <T : Entityc> put(name: String, type: Class<T>, prov: Prov<out T>) {
        map.put(name, prov)
        ids.put(type, EntityMapping.register(name, prov))
    }

    private fun getUnit(name: String): Prov<Unit> {
        return map[name] as Prov<Unit>
    }


    fun getId(type: Class<out Entityc>): Int {

      val i = ids[type, -1]
      if (i == -1) {
        throw Exception("Unit ${type.simpleName} 没有注册")
      }
      return i
    }



  internal  fun < T : Unit> getPutUnits(clazz: Class<T>): Prov<T> {
    put(clazz.simpleName, clazz) {
      val declaredConstructor =clazz.getDeclaredConstructor()
      declaredConstructor.isAccessible = true
      declaredConstructor.newInstance()

    }
    return getUnit(clazz.simpleName) as Prov<T>
  }

    internal inline fun <reified T : Entityc> getPutUnit(): Prov<Unit> {
        put(T::class.java.simpleName, T::class.java) {
            T::class.java.getDeclaredConstructor().newInstance()
        }
        return getUnit(T::class.java.simpleName)
    }

    override fun setup() {
        put(PackStack::class.java.simpleName, PackStack::class.java) {
            return@put PackStack::class.java.getDeclaredConstructor().newInstance()
        }
    }
}
