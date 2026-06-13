@file:Suppress("UNCHECKED_CAST")

package ice.entities

import arc.func.Prov
import arc.struct.ObjectIntMap
import arc.struct.ObjectMap
import mindustry.gen.EntityMapping
import mindustry.gen.Entityc
import mindustry.gen.Unit
import java.lang.reflect.Constructor

object EntityRegistry {
  private val ids = ObjectIntMap<Class<out Entityc>>()
  private val providers: ObjectMap<String, Prov<Unit>> = ObjectMap()
  private val TmpClassConstruntor = ObjectMap<Class<out Unit>, Constructor<out Unit>>()

  private fun <T :Entityc> register(name: String, type: Class<T>, provider: Prov<Unit>): Prov<Unit> {
    providers.put(name, provider)
    ids.put(type, EntityMapping.register(name, provider))
    return provider
  }

  private fun getUnit(name: String): Prov<Unit> =
    providers[name] ?: throw IllegalArgumentException("Unit $name 没有注册,请先调用 getPutUnits")

  /**根据实体类型获取其对应的ID
   * @param clazz 实体类的Class对象,必须是Entityc或其子类
   * @return 该实体类型对应的ID
   * @throws Exception 如果该实体类型未注册（ID为-1时抛出）*/
  fun getId(clazz: Class<out Entityc>): Int {
    val id = ids[clazz, -1]
    return if (id == -1) throw Exception("Unit ${clazz.canonicalName} 没有注册") else id
  }

  /**获取并注册指定Unit类的提供者,会自动注册[EntityMapping]
   * ```kotlin
   *   constructor = IceRegister.getPutUnits(clazz)
   *   ```
   * @param clazz 要获取提供者的类对象,可以是任意类型
   * @return 返回一个[Prov]类型的提供者,用于创建指定类的实例*/
  fun getPutUnits(clazz: Class<out Unit>): Prov<Unit> {
    val name = clazz.canonicalName

    if (providers.containsKey(name)) return getUnit(name)
    return register(name, clazz) {
      TmpClassConstruntor.get(clazz) {
        clazz.getDeclaredConstructor().also { it.isAccessible = true }
      }.newInstance()
    }
  }
}