package universecore.reflection.methodInvoker.staticInvoker

import java.lang.reflect.Method

class StaticInvoker3<P1, P2, P3, R>(private val method: Method) :(P1, P2, P3) -> R {
  override fun invoke(p1: P1, p2: P2, p3: P3) = method.invoke(null, p1, p2, p3) as R
}