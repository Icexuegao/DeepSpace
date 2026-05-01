package ice.library.struct

import kotlin.random.Random

object EulerTourTree {
  fun <V :UserData<V>> link(u: Node<V>, v: Node<V>, uv: Node<V>, vu: Node<V>) {
    val (l1, l2) = Treap.splitUp2(u)
    val (r1, r2) = Treap.splitUp2(v)

    merge(l2, l1, uv, r2, r1, vu)
  }

  fun <V :UserData<V>> cut(uv: Node<V>, vu: Node<V>) {
    var uv = uv
    var vu = vu
    if (uv.rank > vu.rank) uv = vu.apply { vu = uv }

    val (l1, _) = Treap.splitUp3(uv)
    val (_, l3) = Treap.splitUp3(vu)

    merge(l1, l3)
  }

  fun <V :UserData<V>> connected(u: Node<V>, v: Node<V>) = u.root === v.root

  private fun <V :UserData<V>> merge(vararg nodes: Node<V>?) = nodes.reduceOrNull(Treap::merge)

  private object Treap {
    fun <V :UserData<V>> merge(u: Node<V>?, v: Node<V>?): Node<V>? = when {
      u == null -> v
      v == null -> u

      u.priority < v.priority -> {
        u.right = merge(u.right, v)
        u.maintain()
      }

      else -> {
        v.left = merge(u, v.left)
        v.maintain()
      }
    }

    fun <V :UserData<V>> splitUp2(u: Node<V>?): Pair<Node<V>?, Node<V>?> {
      if (u == null) return null to null

      var left: Node<V>? = null
      var right: Node<V>? = u.right
      right?.parent = null
      u.right = null

      var currency: Node<V>? = u
      var fromLeft = false
      var isLeft = false

      while(currency != null) {
        val parent = currency.parent
        if (parent != null) {
          isLeft = currency.isLeftChild
          if (isLeft) parent.left = null else parent.right = null
          currency.parent = null
        }

        if (fromLeft) right = merge(right, currency) else left = merge(currency, left)

        fromLeft = isLeft
        currency.maintain()
        currency = parent
      }

      return left to right
    }

    fun <V :UserData<V>> splitUp3(u: Node<V>?): Pair<Node<V>?, Node<V>?> {
      if (u == null) return null to null

      var left: Node<V>? = u.left
      left?.parent = null
      u.left = null
      var right: Node<V>? = u.right
      right?.parent = null
      u.right = null

      var currency: Node<V>? = u
      var fromLeft = false
      var isLeft = false
      var skipMerge = true

      while(currency != null) {
        val parent = currency.parent
        if (parent != null) {
          isLeft = currency.isLeftChild
          if (isLeft) parent.left = null else parent.right = null
          currency.parent = null
        }

        when {
          skipMerge -> skipMerge = false
          fromLeft -> right = merge(right, currency)
          else -> left = merge(currency, left)
        }

        fromLeft = isLeft
        currency.maintain()
        currency = parent
      }

      return left to right
    }
  }

  open class Node<Data :UserData<Data>>(var data: Data?, var priority: Double = Random.nextDouble()) {
    var size: Int = 1
    var parent: Node<Data>? = null
    var left: Node<Data>? = null
    var right: Node<Data>? = null

    val leftSize: Int get() = left?.size ?: 0
    val rightSize: Int get() = right?.size ?: 0
    val isLeftChild: Boolean get() = parent?.left == this

    val root: Node<Data>
      get() {
        var currency: Node<Data> = this
        while(currency.parent != null) currency = currency.parent!!
        return currency
      }

    val rank: Int
      get() {
        var rank = leftSize + 1
        var currency: Node<Data>? = this
        while(currency?.parent != null) {
          if (!currency.isLeftChild) rank += currency.parent!!.leftSize + 1
          currency = currency.parent
        }
        return rank
      }

    fun maintain() = also {
      size = 1 + leftSize + rightSize

      left?.parent = this
      right?.parent = this

      data?.maintain(left?.data, right?.data)
    }
  }

  open class UserData<D :UserData<D>> {
    open fun maintain(left: D?, right: D?) {}
  }
}

