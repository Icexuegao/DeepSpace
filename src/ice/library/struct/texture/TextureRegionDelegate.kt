package ice.library.struct.texture

import arc.Core
import arc.graphics.g2d.TextureRegion
import kotlin.reflect.KProperty

class TextureRegionDelegate(var initialValue: String, var def: String = "") : TextureDelegate() {
  private var value: TextureRegion? = null

  init {
    delegate.add {
      value = Core.atlas.find(initialValue)
    }
  }

  operator fun getValue(thisRef: Any?, property: KProperty<*>): TextureRegion {
    value = Core.atlas.find(initialValue).let {
      if (it.found()) it else Core.atlas.find(def)
    }

    return value!!
  }

  operator fun setValue(thisRef: Any?, property: KProperty<*>, value: TextureRegion) {
    this.value = value
  }
}