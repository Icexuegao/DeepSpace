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
import java.lang.reflect.Constructor

object IceRegister : Load {
  private val ids = ObjectIntMap<Class<out Entityc>>()
  private val map: ObjectMap<String, Prov<out Entityc>> = ObjectMap()
  private val TmpClassConstruntor = ObjectMap<Class<Unit>, Constructor<Unit>>()
  private fun <T : Entityc> put(name: String, type: Class<T>, prov: Prov<out T>) {
    map.put(name, prov)
    ids.put(type, EntityMapping.register(name, prov))
  }

  private fun getUnit(name: String): Prov<Unit>? = map[name] as Prov<Unit>?

  /**根据实体类型获取其对应的ID
   * @param type 实体类的Class对象,必须是Entityc或其子类
   * @return 该实体类型对应的ID
   * @throws Exception 如果该实体类型未注册（ID为-1时抛出）
   */
  fun getId(type: Class<out Entityc>): Int {
    val id = ids[type, -1]
    return if (id == -1) throw Exception("Unit ${type.simpleName} 没有注册") else id
  }

  /**获取并注册指定Unit类的提供者,会自动注册[EntityMapping]
   * @param clazz 要获取提供者的类对象,可以是任意类型
   * @return 返回一个[Prov]类型的提供者,用于创建指定类的实例
   */
  fun getPutUnits(clazz: Class<*>): Prov<Unit> {
    put(clazz.simpleName, clazz as Class<Unit>) {
      TmpClassConstruntor.get(clazz) { clazz.getDeclaredConstructor() }.also { it.isAccessible = true }.newInstance()
    }
    return getUnit(clazz.simpleName) as Prov<Unit>
  }

  override fun setup() {
    put(PackStack::class.java.simpleName, PackStack::class.java) {
      return@put PackStack::class.java.getDeclaredConstructor().newInstance()
    }
  }
}
