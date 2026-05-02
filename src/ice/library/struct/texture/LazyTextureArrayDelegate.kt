package ice.library.struct.texture

import arc.Core
import arc.graphics.g2d.TextureRegion
import kotlin.reflect.KProperty

class LazyTextureArrayDelegate(private var initialValue: String, private var size: Int) :LazyTextureDelegate() {
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