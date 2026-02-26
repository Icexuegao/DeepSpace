package ice.library.struct

import java.util.*
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

/**一个用于为任意对象附加属性的委托类,通过弱引用映射存储属性值,避免内存泄漏。
 *
 * 核心功能：
 * - 为任意类型T的对象提供类型为V的属性附加功能
 * - 使用WeakHashMap存储属性值,确保不会阻止宿主对象被垃圾回收
 * - 支持设置默认值,当首次访问属性时返回默认值
 *
 * 使用示例：
 * ```kotlin
 * val Person.age by AttachedProperty(0)
 *
 * val person = Person()
 * println(person.age)  // 输出: 0
 * person.age = 25
 * println(person.age)  // 输出: 25
 * ```
 *
 * 构造函数参数：
 * @param defaultValue 属性的默认值,当首次访问属性时返回此值
 *
 * 注意事项：
 * - 由于Pools的重置机制,可能对象不会被回收,从而导致WeakHashMap的值依旧保留
 * - 宿主对象T必须是非空类型(Any)
 * - 由于使用WeakHashMap,属性值会在宿主对象被回收后自动清除
 * - 不建议用于存储与宿主对象生命周期无关的重要数据 */
class AttachedProperty<in T : Any, V>(private val defaultValue: V) : ReadWriteProperty<T, V> {
  private val valuesMap = WeakHashMap<T, V>()
  override fun getValue(thisRef: T, property: KProperty<*>): V {
    return valuesMap.getOrPut(thisRef) { defaultValue }
  }

  override fun setValue(thisRef: T, property: KProperty<*>, value: V) {
    valuesMap[thisRef] = value
  }
}