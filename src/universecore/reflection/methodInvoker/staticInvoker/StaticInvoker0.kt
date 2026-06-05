package universecore.reflection.methodInvoker.staticInvoker

import java.lang.reflect.Method

class StaticInvoker0<R>(private val method: Method) :() -> R {
  override fun invoke() = method.invoke(null) as R
}