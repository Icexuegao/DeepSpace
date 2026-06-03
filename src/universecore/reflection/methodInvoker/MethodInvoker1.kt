package universecore.reflection.methodInvoker

import java.lang.reflect.Method

class MethodInvoker1<O, P1, R>(private val method: Method) :(O, P1) -> R {
  override fun invoke(self: O, p1: P1) = method.invoke(self, p1) as R
}