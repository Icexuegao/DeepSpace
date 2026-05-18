package universecore.util.funcs

fun interface VariableFunc<T, R> {
  fun apply(vararg args: T): R
}