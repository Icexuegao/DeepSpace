package universecore.util.funcs

interface Traversable<T> {
    fun each(cons: (T) -> Unit)
}