package ice.library.func

interface Traversable<T> {
    fun each(cons: (T) -> Unit)
}