package universecore.util

import arc.Events
import arc.func.Cons
import arc.struct.ObjectMap
import arc.struct.Seq
import universecore.reflection.accessField

/** 删除[Events]内部事件的工具类
 *
 * //mindustry.audio.SoundControl$$Lambda/0x000000009750aa10@2b3d7075 pc
 *
 * //mindustry.audio.SoundControl$$ExternalSyntheticLambda0@2e872cb  android
 * @author Alon */
object EventRemover {
  val events: ObjectMap<Any, Seq<Cons<*>>> by Events::class.accessField("events")

  /**
   * 根据监听器的声明类名移除指定事件类型的监听器,不会对匿名类进行查找删除
   *
   * 遍历指定事件类型的所有监听器，查找 `toString()` 中包含目标类名的监听器并移除。
   * 匹配模式为 `${T::class.simpleName}$$`，可匹配匿名类和 Lambda 表达式。
   *
   * @param T 监听器的声明类类型
   * @param E 事件的类型
   * @return 如果找到并移除了匹配的监听器返回 `true`，否则返回 `false`
   */
  inline fun <reified T, reified E> remove(): Boolean {
    val qualifiedName = T::class.java.name
    qualifiedName.log()
    for(lambda in events.get(E::class.java)) {
      // 匹配完整类名
      if (lambda.toString().contains("$qualifiedName$$")) {
        @Suppress("UNCHECKED_CAST") Events.remove(E::class.java, lambda as Cons<E>)
        return true
      }
    }
    return false
  }
}