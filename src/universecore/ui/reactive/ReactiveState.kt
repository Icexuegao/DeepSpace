package universecore.ui.reactive

import kotlin.reflect.KProperty

/** 响应式状态管理类
 * 当值发生变化时,自动通知所有订阅者 */
class ReactiveState<T>(private var value: T) {
  private val listeners = mutableListOf<(T) -> Unit>()

  fun set(newValue: T) {
    if (value != newValue) {
      value = newValue
      notifyListeners()
    }
  }

  fun get() = value

  fun update(updater: (T) -> T) = set(updater(value))

  fun subscribe(listener: (T) -> Unit): Subscription {
    listeners.add(listener)
    // 立即通知当前值
    listener(value)
    return Subscription { listeners.remove(listener) }
  }

  private fun notifyListeners() {
    listeners.forEach { it(value) }
  }

  operator fun getValue(thisRef: Any?, property: KProperty<*>) = value

  operator fun setValue(thisRef: Any?, property: KProperty<*>, value: T) = set(value)

}


