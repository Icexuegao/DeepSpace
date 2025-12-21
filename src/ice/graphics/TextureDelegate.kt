package ice.graphics

import arc.Core
import arc.Events
import arc.graphics.g2d.TextureRegion
import arc.struct.Seq
import ice.library.world.Load
import kotlin.reflect.KProperty

open class TextureDelegate {
    companion object : Load {
        internal var delegate = Seq<() -> Unit>()
        override fun setup() {
            Events.on(mindustry.game.EventType.AtlasPackEvent::class.java) { _ ->
                delegate.forEach { it.invoke() }
            }
        }
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

class TextureRegionArrDelegate(var initialValue: String, var size: Int) : TextureDelegate() {
    private var value: Array<TextureRegion>? = null

    init {
        delegate.add {
            value = Array(size) {
                Core.atlas.find("$initialValue-$it")
            }
        }
    }

    operator fun getValue(thisRef: Any?, property: KProperty<*>): Array<TextureRegion> {
        value = value ?: Array(size) {
            Core.atlas.find("$initialValue-$it")
        }
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