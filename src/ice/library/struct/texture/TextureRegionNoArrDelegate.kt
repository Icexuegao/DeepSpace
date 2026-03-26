package ice.library.struct.texture

import arc.graphics.g2d.TextureRegion
import ice.library.IFiles
import kotlin.reflect.KProperty

class TextureRegionNoArrDelegate(private var basePath: String) : TextureDelegate() {
  private var cachedRegions: Array<TextureRegion>? = null

  init {
    delegate.add {
      cachedRegions = loadTextureRegions()
    }
  }

  private fun loadTextureRegions(): Array<TextureRegion> {
    var count = 0

    while (IFiles.hasPng(basePath + (count + 1))) {
      count++
    }

    return Array(count) { index ->
      IFiles.findPng(basePath + (index + 1))
    }
  }

  operator fun getValue(thisRef: Any?, property: KProperty<*>): Array<TextureRegion> {
    cachedRegions = cachedRegions ?: loadTextureRegions()
    return cachedRegions!!
  }

  operator fun setValue(thisRef: Any?, property: KProperty<*>, value: Array<TextureRegion>) {
    this.cachedRegions = value
  }
}