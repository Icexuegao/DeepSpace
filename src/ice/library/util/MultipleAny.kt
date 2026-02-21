package ice.library.util

/** 一个用于存储多个任意类型值的容器类,支持解构声明,用于方法返回多个值
 * 使用示例:
 * val m :Any= MultipleAny(1, "hello", 3.14)
 * val (a: Int, b: String, c: Double) = m.multipleAny()
 * @param anys 可变参数,可以接受任意数量的任意类型值
 * @throws ClassCastException 类型转换失败时抛出 */
@Suppress("unused", "UNCHECKED_CAST")
class MultipleAny(vararg val anys: Any) {

  companion object {
    fun Any.multipleAny(): MultipleAny {
      return this as MultipleAny
    }
  }

  operator fun <T> component1(): T {
    return anys[0] as T
  }

  operator fun <T> component2(): T {
    return anys[1] as T
  }

  operator fun <T> component3(): T {
    return anys[2] as T
  }

  operator fun <T> component4(): T {
    return anys[3] as T
  }

  operator fun <T> component5(): T {
    return anys[4] as T
  }
}