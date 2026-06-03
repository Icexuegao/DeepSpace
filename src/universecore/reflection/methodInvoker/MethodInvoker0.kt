package universecore.reflection.methodInvoker

import java.lang.reflect.Method

class MethodInvoker0<O, R>(private val method: Method) :(O) -> R {
  override fun invoke(self: O) = method.invoke(self) as R
}