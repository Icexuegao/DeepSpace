package ice.library.struct.texture

import arc.Core
import arc.graphics.g2d.TextureRegion
import kotlin.reflect.KProperty

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