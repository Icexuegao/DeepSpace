package universecore.reflection.methodInvoker.staticInvoker

import java.lang.reflect.Method

class StaticInvoker1<P1, R>(private val method: Method) :(P1) -> R {
  override fun invoke(p1: P1) = method.invoke(null, p1) as R
}