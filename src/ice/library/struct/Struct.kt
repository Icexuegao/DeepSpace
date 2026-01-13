package ice.library.struct

import arc.func.Cons
import arc.func.Prov
import arc.graphics.g2d.TextureRegion
import arc.scene.style.TextureRegionDrawable
import arc.struct.Seq
import arc.util.Log
import universecore.util.colletion.CollectionObjectMap
import kotlin.properties.Delegates
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

fun <T> Seq<T>.isNotEmpty() = size != 0
fun <T> Cons<T>.getT(t: T): T {
    get(t)
    return t
}

fun <K, V> HashMap<K, V>.addP(key: K, prov: Prov<V>) {
    put(key, prov.get())
}


/**
 * 对集合中的前 [count] 个元素执行指定的操作
 * @param count 要处理的元素数量，如果小于等于0则不处理任何元素
 * @param action 要对每个元素执行的操作
 */
inline fun <T> Iterable<T>.forEach(count: Int, action: (T) -> Unit) {
    if (count <= 0) return
    var processed = 0
    for (element in this) {
        action(element)
        if (++processed >= count) break
    }
}

fun <T> observable(initialValue: T, onChange: (property: KProperty<*>, old: T, new: T) -> Unit = { _, _, _ -> }) = Delegates.observable(initialValue) { property, old, new ->
    if (old != new) {
        onChange(property, old, new)
    }
}

fun <E> log(e: () -> E) = Log.info(e())
fun TextureRegion.asDrawable(): TextureRegionDrawable = TextureRegionDrawable(this)

class AttachedProperty<in T : Any, V>(private val defaultValue: V) : ReadWriteProperty<T, V> {
    private val valuesMap = CollectionObjectMap<T, V>()
    override fun getValue(thisRef: T, property: KProperty<*>): V {
        return valuesMap.getOrPut(thisRef) { defaultValue }
    }
    override fun setValue(thisRef: T, property: KProperty<*>, value: V){
        valuesMap[thisRef] = value
    }

}
