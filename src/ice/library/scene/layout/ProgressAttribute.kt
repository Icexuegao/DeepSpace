package ice.library.scene.layout

import arc.graphics.Color
import arc.graphics.g2d.TextureRegion
import arc.scene.style.Drawable
import arc.scene.style.TextureRegionDrawable

class ProgressAttribute(var barBackground: TextureRegion, var barTop: Drawable, var bottom: TextureRegion?=null, var scale: Float = 1f) {
    var starX = 0.0f
    var starY = 0.0f
    var drawHeight = 0f
    var color:Color=Color.white
    val barBackgroundDrawable = TextureRegionDrawable(barBackground,scale)
    var bottomBar:TextureRegionDrawable?=null
    init {
        bottom?.let {
            bottomBar=TextureRegionDrawable(it,scale)
        }
    }
    fun getHeight(): Float {
        return barBackgroundDrawable.minHeight
    }

    fun getWidth(): Float {
        return barBackgroundDrawable.minWidth
    }
}
