package universecore.components.blockcomp

import arc.func.Boolf
import arc.struct.ObjectMap
import arc.struct.Seq
import mindustry.gen.Building

/**此接口给予实现者从一堆元素中依次取出（或者预测取出）元素的功能
 *
 * @author EBwilson
 * @since 1.2*/
interface Takeable : BuildCompBase {
    //  @Annotations.BindField(value = "heaps", initialize = "new arc.struct.ObjectMap()")
    var heaps: ObjectMap<String, Heaps<*>>

    /**添加一个输出元素堆，用一个字符串作为名字和一个Seq容器初始化，这个操作不是绝对必要的
     *
     * @param name 堆命名，用于索引
     * @param targets 所有堆元素，选取操作在这当中进行*/
    fun addHeap(name: String, targets: Seq<*>) {
        @Suppress("UNCHECKED_CAST")
        heaps.put(name, Heaps(targets as Seq<Any?>))
    }

    /**添加一个输出元素堆，用一个字符串作为名字和一个Seq容器，以及一个返回布尔值的过滤器函数初始化，这个操作不是绝对必要的
     *
     * @param name 堆命名，用于索引
     * @param targets 所有堆元素，选取操作在这当中进行
     * @param valid 选择过滤器*/
    fun <T> addHeap(name: String, targets: Seq<T?>, valid: Boolf<T?>) {
        heaps.put(name, Heaps(targets, valid))
    }

    /**获取一个输出元素堆
     *
     * @param name 堆在容器中保存的名称*/
    @Suppress("UNCHECKED_CAST")
    fun <T> getHeaps(name: String): Heaps<T>? {
        return heaps!!.get(name) as Heaps<T>?
    }

    /**从指定名称的堆中获得下一个元素，如果指定的堆不存在，会创建一个新的堆加入容器
     *
     * @param name 堆名称*/
    fun getNext(name: String): Building? {
        return getNext(name, true)
    }

    /**从指定名称的堆中获得下一个元素，如果指定的堆不存在，会创建一个新的堆加入容器
     *
     * @param name 堆名称
     * @param targets 提供给堆中元素列表*/
    fun <T> getNext(name: String, targets: Seq<T?>): T? {
        return getNext(name, targets, true)
    }

    /**从指定名称的堆中获得下一个元素，如果指定的堆不存在，会创建一个新的堆加入容器，堆的元素默认来自building的{@link Building#proximity}
     *
     * @param name 堆名称
     * @param valid 元素的过滤器*/
    fun getNext(name: String, valid: Boolf<Building?>): Building? {
        return getNext(name, valid, true)
    }

    /**从指定名称的堆中获得下一个元素，如果指定的堆不存在，会创建一个新的堆加入容器
     *
     * @param name 堆名称
     * @param targets 提供给堆中元素列表
     * @param valid 元素的过滤器*/
    fun <T> getNext(name: String, targets: Seq<T?>, valid: Boolf<T?>): T? {
        return getNext(name, targets, valid, true)
    }

    /**预测下一个获得的元素，如果指定的堆不存在，会创建一个新的堆加入容器，堆的元素默认来自building的{@link Building#proximity}
     *
     * @param name 堆名称*/
    fun peek(name: String): Building? {
        return getNext(name, false)
    }

    /**预测下一个获得的元素，如果指定的堆不存在，会创建一个新的堆加入容器
     *
     * @param name 堆名称
     * @param targets 提供给堆中元素列表*/
    fun <T> peek(name: String, targets: Seq<T?>): T? {
        return getNext(name, targets, false)
    }

    /**预测下一个获得的元素，如果指定的堆不存在，会创建一个新的堆加入容器，堆的元素默认来自building的{@link Building#proximity}
     *
     * @param name 堆名称
     * @param valid 元素的过滤器*/
    fun peek(name: String, valid: Boolf<Building?>): Building? {
        return getNext(name, valid, false)
    }

    /**预测下一个获得的元素，如果指定的堆不存在，会创建一个新的堆加入容器
     *
     * @param name 堆名称
     * @param targets 提供给堆中元素列表
     * @param valid 元素的过滤器*/
    fun <T> peek(name: String, targets: Seq<T?>, valid: Boolf<T?>): T? {
        return getNext(name, targets, valid, false)
    }

    /**从指定名称的堆中获得下一个元素，如果指定的堆不存在，会创建一个新的堆加入容器，堆的元素默认来自building的{@link Building#proximity}
     *
     * @param name 堆名称
     * @param increase 是否增加一次计数器，如果为false则此方法用于预测下一个元素*/
    @Suppress("UNCHECKED_CAST")
    fun getNext(name: String, increase: Boolean): Building? {
        var heaps1: Heaps<Building>? = heaps.get(name) as Heaps<Building>
        if (heaps1 == null) {
            heaps1 = Heaps(getBuilding(Building::class.java).proximity)
            heaps.put(name, heaps1)
        }
        return if (increase) heaps1.next() else heaps1.peek()
    }

    /**从指定名称的堆中获得下一个元素，如果指定的堆不存在，会创建一个新的堆加入容器，堆的元素默认来自building的{@link Building#proximity}
     *
     * @param name 堆名称
     * @param valid 元素的过滤器
     * @param increase 是否增加一次计数器，如果为false则此方法用于预测下一个元素*/
    @Suppress("UNCHECKED_CAST")
    fun getNext(name: String, valid: Boolf<Building?>, increase: Boolean): Building? {
        var heaps1: Heaps<Building>? = heaps.get(name) as Heaps<Building>?
        if ((heaps1) == null) {
            heaps1 = Heaps(getBuilding(Building::class.java).proximity, valid)
            heaps!!.put(name, heaps1)
        }
        return if (increase) heaps1.next(valid) else heaps1.peek(valid)
    }

    /**从指定名称的堆中获得下一个元素，如果指定的堆不存在，会创建一个新的堆加入容器
     *
     * @param name 堆名称
     * @param targets 提供给堆中元素列表
     * @param increase 是否增加一次计数器，如果为false则此方法用于预测下一个元素*/
    @Suppress("UNCHECKED_CAST")
    fun <T> getNext(name: String, targets: Seq<T?>, increase: Boolean): T? {
        var heaps1: Heaps<T>? = heaps!!.get(name) as Heaps<T>?
        if ((heaps1) == null) {
            heaps1 = Heaps(targets)
            heaps!!.put(name, heaps1)
        }
        return if (increase) heaps1.next(targets) else heaps1.peek(targets)
    }

    /**从指定名称的堆中获得下一个元素，如果指定的堆不存在，会创建一个新的堆加入容器
     *
     * @param name 堆名称
     * @param targets 提供给堆中元素列表
     * @param valid 元素的过滤器
     * @param increase 是否增加一次计数器，如果为false则此方法用于预测下一个元素*/
    @Suppress("UNCHECKED_CAST")
    fun <T> getNext(name: String, targets: Seq<T?>, valid: Boolf<T?>, increase: Boolean): T?{
        var heaps1: Heaps<T?>? = heaps.get(name) as? Heaps<T?>?
        if ((heaps1) == null) {
            heaps1 = Heaps(targets, valid)
            heaps.put(name, heaps1)
        }
        return if (increase) heaps1.next(targets, valid) else heaps1.peek(targets, valid)
    }

    /**元素堆，用于保存和计数弹出的目标元素*/
    class Heaps<Type> {
        var targets: Seq<Type?> = Seq()
        var valid: Boolf<Type?> = Boolf { true }
        var heapCounter: Int = 0

        constructor()

        constructor(defaultAll: Seq<Type?>) {
            this.targets = defaultAll
        }

        constructor(targets: Seq<Type?>, valid: Boolf<Type?>) {
            this.targets = targets
            this.valid = valid
        }

        fun increaseCount(size: Int): Int {
            heapCounter = (heapCounter + 1) % size
            return heapCounter
        }

        fun next(): Type? {
            return next(targets, valid)
        }

        fun peek(): Type? {
            return peek(targets, valid)
        }

        fun next(valid: Boolf<Type?>): Type? {
            return next(targets, valid)
        }

        fun peek(valid: Boolf<Type?>): Type? {
            return peek(targets, valid)
        }

        fun next(targets: Seq<Type?>): Type? {
            return next(targets, valid)
        }

        fun peek(targets: Seq<Type?>): Type ?{
            return peek(targets, valid)
        }

        fun next(targets: Seq<Type?>, valid: Boolf<Type?>): Type? {
            val size = targets.size
            if (size == 0) return null as Type
            var result: Type
            for (ignored in targets) {
                result = targets[increaseCount(size)] as Type
                if (valid.get(result)) return result
            }
            return null
        }

        fun peek(targets: Seq<Type?>, valid: Boolf<Type?>): Type? {
            val size = targets.size
            var curr = heapCounter
            if (size == 0) return null as Type
            var result: Type
            for (ignored in targets) {
                curr = (curr + 1) % size
                result = targets[curr] as Type
                if (valid.get(result)) return result
            }
            return null
        }
    }
}
