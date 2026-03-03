package ice.library.util

interface Traversable<T> {
    fun each(cons: (T) -> Unit)
}