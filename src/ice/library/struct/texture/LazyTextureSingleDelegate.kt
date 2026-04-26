package ice.library.struct.texture

import arc.Core
import arc.graphics.g2d.TextureRegion
import kotlin.reflect.KProperty

/** 延迟加载单个纹理的委托类
 * 支持指定主纹理名称和备用纹理名称,当主纹理未找到时自动尝试加载备用纹理
 * ```kotlin
 * //非空
 * var top2: TextureRegion by LazyTextureSingleDelegate("${this.name}-top2")
 * ```
 * @property initialValue 主纹理的名称
 * @property def 备用纹理的名称,如果主纹理未找到且提供了备用名称,则尝试加载备用纹理
 * @author Alon
 */
@Suppress("UNCHECKED_CAST")
class LazyTextureSingleDelegate(var initialValue: String, var def: String = "") :LazyTextureDelegate() {
  private var value: TextureRegion? = null

  init {
    delegate.add {
      value = Core.atlas.find(initialValue, def)
    }
  }

  operator fun getValue(thisRef: Any?, property: KProperty<*>): TextureRegion {
    value = Core.atlas.find(initialValue, def)
    return value!!
  }

  operator fun setValue(thisRef: Any?, property: KProperty<*>, value: TextureRegion) {
    this.value = value
  }
}
