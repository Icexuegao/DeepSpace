package ice.graphics

import arc.Core
import arc.Events
import arc.graphics.g2d.TextureRegion
import arc.struct.Seq
import ice.library.world.Load
import kotlin.reflect.KProperty

class TextureRegionDelegate(var initialValue: String, var def: String = "") {
    companion object : Load {
        private var delegate = Seq<() -> Unit>()
        override fun setup() {
            Events.on(mindustry.game.EventType.AtlasPackEvent::class.java) { _ ->
                delegate.forEach { it.invoke() }
            }
        }
    }

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