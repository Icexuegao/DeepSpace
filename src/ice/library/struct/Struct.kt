package ice.library.struct

import arc.func.Cons
import arc.func.Prov
import arc.graphics.g2d.TextureRegion
import arc.scene.style.TextureRegionDrawable
import arc.struct.Seq
import arc.util.Log

fun <T> Seq<T>.addP(prov: Prov<T>): Seq<T> = add(prov.get())
fun <T> Seq<T>.isNotEmpty() = size != 0
fun <T> Cons<T>.getT(t: T): T {
    get(t)
    return t
}
fun <K,V>HashMap<K,V>.addP(key: K,prov: Prov<V>){
    put(key,prov.get())
}

/** true 则运行run*/
fun Boolean.ifTrue(run: () -> Unit) {
    if (this) run()
}

fun <E> log(e: () -> E) = Log.info(e())
fun TextureRegion.asDrawable() = TextureRegionDrawable(this)
