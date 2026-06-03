package universecore.reflection.methodInvoker.staticInvoker

import java.lang.reflect.Method

class StaticInvoker5<P1, P2, P3, P4, P5, R>(private val method: Method) :(P1, P2, P3, P4, P5) -> R {
  override fun invoke(p1: P1, p2: P2, p3: P3, p4: P4, p5: P5) = method.invoke(null, p1, p2, p3, p4, p5) as R
}