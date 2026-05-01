package ice.library.struct

import ice.library.util.Traversable

class UnsafeLinkedList<E> :Iterable<E>, Traversable<E> {
  var size: Int = 0
    private set

  private var head: Node<E>? = null
  private var tail: Node<E>? = null

  private var iterator: UnsafeIterator<E>? = null

  fun add(value: E) = addLast(value)

  // fucking safety, O(1) first
  // so we clear the other list
  fun addAll(list: UnsafeLinkedList<E>) = apply {
    if (list.size == 0) return@apply

    if (size == 0) {
      head = list.head
      tail = list.tail
    } else {
      val prev = tail!!
      val next = list.head!!
      prev.next = next
      next.prev = prev
      tail = list.tail

      // for test
      head!!.prev = null
      tail!!.next = null
    }

    size += list.size

    // let's test
//        list.clear()
  }

  fun addFirst(value: E) = apply {
    val next = head
    head = Node(value, null, next)
    if (next != null) next.prev = head else tail = head
    size++
  }

  fun addLast(value: E) = apply {
    val prev = tail
    tail = Node(value, prev, null)
    if (prev != null) prev.next = tail else head = tail
    size++
  }

  fun removeFirst(): E {
    head ?: throw NoSuchElementException()
    val prev = head!!
    head = prev.next
    if (head != null) head!!.prev = null else tail = null
    size--
    return prev.item
  }

  fun removeLast(): E {
    tail ?: throw NoSuchElementException()
    val next = tail!!
    tail = next.prev
    if (tail != null) tail!!.next = null else head = null
    size--
    return next.item
  }

  fun getFirst(): E = head?.item ?: throw NoSuchElementException()

  fun getLast(): E = tail?.item ?: throw NoSuchElementException()

  fun isEmpty() = size == 0

  fun isNotEmpty() = size > 0

  // fucking gc, O(1) first
  fun clear() {
    head = null
    tail = null
    size = 0
  }

  operator fun plusAssign(value: E) {
    addLast(value)
  }

  operator fun plusAssign(list: UnsafeLinkedList<E>) {
    addAll(list)
  }

  override fun iterator(): Iterator<E> {
    if (iterator == null) iterator = UnsafeIterator()
    iterator!!.size = size
    iterator!!.current = head
    return iterator!!
  }

  override fun each(cons: (E) -> Unit) {
    var currency = head
    while(currency != null) {
      cons(currency.item)
      currency = currency.next
    }
  }

  private data class Node<E>(
    var item: E,
    var prev: Node<E>?,
    var next: Node<E>?,
  )

  private class UnsafeIterator<E> :Iterator<E> {
    var size = 0
    var current: Node<E>? = null

    override fun next(): E {
      if (size == 0) throw NoSuchElementException()

      val item = current!!.item
      current = current!!.next
      size--

      return item
    }

    override fun hasNext() = size > 0
  }
}