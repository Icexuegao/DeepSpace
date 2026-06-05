package universecore.reflection.methodInvoker

import java.lang.reflect.Method

class MethodInvoker5<O, P1, P2, P3, P4, P5, R>(private val method: Method) :(O, P1, P2, P3, P4, P5) -> R {
  override fun invoke(self: O, p1: P1, p2: P2, p3: P3, p4: P4, p5: P5) = method.invoke(self, p1, p2, p3, p4, p5) as R
}