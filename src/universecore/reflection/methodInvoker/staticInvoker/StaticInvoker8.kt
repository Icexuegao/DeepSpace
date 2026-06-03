package universecore.reflection.methodInvoker.staticInvoker

import java.lang.reflect.Method

class StaticInvoker8<P1, P2, P3, P4, P5, P6, P7, P8, R>(private val method: Method) :(P1, P2, P3, P4, P5, P6, P7, P8) -> R {
  override fun invoke(p1: P1, p2: P2, p3: P3, p4: P4, p5: P5, p6: P6, p7: P7, p8: P8) =
    method.invoke(null, p1, p2, p3, p4, p5, p6, p7, p8) as R
}