package universecore.reflection.methodInvoker.staticInvoker

import java.lang.reflect.Method

class StaticInvoker2<P1, P2, R>(private val method: Method) :(P1, P2) -> R {
  override fun invoke(p1: P1, p2: P2) = method.invoke(null, p1, p2) as R
}