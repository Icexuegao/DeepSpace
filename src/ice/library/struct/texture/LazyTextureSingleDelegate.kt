package ice.library.struct.texture

import arc.Core
import arc.graphics.g2d.TextureRegion
import kotlin.reflect.KProperty

/** 延迟加载单个纹理的委托类
 * 支持指定主纹理名称和备用纹理名称,当主纹理未找到时自动尝试加载备用纹理
 *
 * @param T 纹理类型,可以是 [TextureRegion] 或 [TextureRegion?]
 * ```kotlin
 * //可空
 * var icon: TextureRegion? by LazyTextureSingleDelegate("research_$name", null)
 * //非空
 * val top2: TextureRegion by LazyTextureSingleDelegate("${this.name}-top2")
 * ```
 * @property initialValue 主纹理的名称
 * @property def 备用纹理的名称,默认为 null。如果主纹理未找到且提供了备用名称,则尝试加载备用纹理
 * @author Alon
 */
@Suppress("UNCHECKED_CAST")
class LazyTextureSingleDelegate<T :TextureRegion?>(var initialValue: String, var def: String? = null) :LazyTextureDelegate() {
  private var value: T? = null

  init {
    delegate.add {
      val textrue = Core.atlas.find(initialValue)
      value = if (textrue.found()) textrue as T else def?.let { Core.atlas.find(it) as T }
    }
  }

  operator fun getValue(thisRef: Any?, property: KProperty<*>): T {
    val textrue = Core.atlas.find(initialValue)
    value = if (textrue.found()) textrue as T else def?.let { Core.atlas.find(it) as T }
    return value as T
  }

  operator fun setValue(thisRef: Any?, property: KProperty<*>, value: T) {
    this.value = value
  }
}
