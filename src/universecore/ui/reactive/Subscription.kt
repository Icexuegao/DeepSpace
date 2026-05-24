package universecore.ui.reactive

/** 订阅对,用于取消订阅 */
class Subscription(private val unsubscribe: () -> Unit) {
  fun unsubscribe() = unsubscribe.invoke()
}