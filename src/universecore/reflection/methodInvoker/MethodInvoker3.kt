package universecore.reflection.methodInvoker

import java.lang.reflect.Method

class MethodInvoker3<O, P1, P2, P3, R>(private val method: Method) :(O, P1, P2, P3) -> R {
  override fun invoke(self: O, p1: P1, p2: P2, p3: P3) = method.invoke(self, p1, p2, p3) as R
}