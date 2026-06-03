package universecore.reflection.methodInvoker.staticInvoker

import java.lang.reflect.Method

class StaticInvoker11<P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, R>(private val method: Method) :
    (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11) -> R {
  override fun invoke(p1: P1, p2: P2, p3: P3, p4: P4, p5: P5, p6: P6, p7: P7, p8: P8, p9: P9, p10: P10, p11: P11) =
    method.invoke(null, p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11) as R
}