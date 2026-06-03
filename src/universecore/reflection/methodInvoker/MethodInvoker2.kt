package universecore.reflection.methodInvoker

import java.lang.reflect.Method

class MethodInvoker2<O, P1, P2, R>(private val method: Method) :(O, P1, P2) -> R {
  override fun invoke(self: O, p1: P1, p2: P2) = method.invoke(self, p1, p2) as R
}