package ice.library.struct

import ice.DeepSpace
import kotlin.reflect.KProperty

/**一个用于代理配置属性的类,支持从 [DeepSpace.globals] 中自动加载和保存值
 * 允许将属性读写操作重定向到全局配置存储中
 * 默认值为构造时提供的 [value],当全局存储中不存在对应键时使用该默认值,并添加该值
 *
 * @param T 属性值的类型 一般来说只支持settings的那几种基元类型
 * @property value 属性的默认值,同时作为内存中的当前值
 * @property saveName 在全局配置存储中用于标识该属性的键名 */
class ConfigPropertyDelegate<T>(
  var value: T, val saveName: String, var change: (old: T, new: T) -> Unit = { _, _ -> }
) {
  var initializer = false
  @Suppress("UNCHECKED_CAST")
  operator fun getValue(thisRef: Any?, property: KProperty<*>): T {
    if (!initializer) {
      value = DeepSpace.globals.get(saveName, value) as T
    }
    return value
  }

  operator fun setValue(thisRef: Any?, property: KProperty<*>, newValue: T) {
    change.invoke(value, newValue)
    value = newValue
    DeepSpace.globals.put(saveName, value)

  }
}