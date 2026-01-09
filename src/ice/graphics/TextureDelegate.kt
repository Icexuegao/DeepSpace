package ice.graphics

import arc.Core
import arc.graphics.g2d.TextureRegion
import arc.struct.Seq
import ice.library.IFiles
import kotlin.reflect.KProperty

open class TextureDelegate {
    companion object {
        internal var delegate = Seq<() -> Unit>()
    }
}

class TextureRegionDelegate(var initialValue: String, var def: String = "") : TextureDelegate() {
    private var value: TextureRegion? = null

    init {
        delegate.add {
            val find = Core.atlas.find(initialValue)
            value = if (find.found()) find else null
        }
    }

    operator fun getValue(thisRef: Any?, property: KProperty<*>): TextureRegion {
        val find = Core.atlas.find(initialValue)
        value = value ?: if (find.found() && def.isEmpty()) find else Core.atlas.find(def)
        return value!!
    }

    operator fun setValue(thisRef: Any?, property: KProperty<*>, value: TextureRegion) {
        this.value = value
    }
}

class TextureRegionArrDelegate(private var initialValue: String, private var size: Int) : TextureDelegate() {
    private var value: Array<TextureRegion>? = null

    init {
        delegate.add {
            value = getArr()
        }
    }

    fun getArr(): Array<TextureRegion> {
        return Array(size) {
            Core.atlas.find("$initialValue-$it")
        }
    }

    operator fun getValue(thisRef: Any?, property: KProperty<*>): Array<TextureRegion> {
        value = value ?: getArr()
        return value!!
    }

    operator fun setValue(thisRef: Any?, property: KProperty<*>, value: Array<TextureRegion>) {
        this.value = value
    }
}

class TextureRegionNoArrDelegate(private var initialValue: String) : TextureDelegate() {
    private var value: Array<TextureRegion>? = null

    init {
        delegate.add {
            value = getArr()
        }
    }

    fun getArr(): Array<TextureRegion> {
        var variants = 0

        while (IFiles.hasPng(initialValue + (variants + 1))) {
            variants++
        }

        return Array(variants) {
            IFiles.findPng(initialValue + (it + 1))
        }
    }

    operator fun getValue(thisRef: Any?, property: KProperty<*>): Array<TextureRegion> {
        value = value ?: getArr()
        return value!!
    }

    operator fun setValue(thisRef: Any?, property: KProperty<*>, value: Array<TextureRegion>) {
        this.value = value
    }
}

class TextureRegionArrArrDelegate(var initialValue: String, var one: Int, var two: Int) : TextureDelegate() {
    private var value: Array<Array<TextureRegion>>? = null

    init {
        delegate.add {
            value = Array(one) { s ->
                Array(two) {
                    Core.atlas.find("$initialValue-$s-$it")
                }
            }
        }
    }

    operator fun getValue(thisRef: Any?, property: KProperty<*>): Array<Array<TextureRegion>> {
        value = value ?: Array(one) { s ->
            Array(two) {
                Core.atlas.find("$initialValue-$s-$it")
            }
        }
        return value!!
    }

    operator fun setValue(thisRef: Any?, property: KProperty<*>, value: Array<Array<TextureRegion>>) {
        this.value = value
    }
}