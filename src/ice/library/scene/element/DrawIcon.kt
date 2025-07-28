package ice.library.scene.element

import arc.graphics.g2d.Draw
import arc.graphics.g2d.TextureRegion
import arc.scene.Element

class DrawIcon(private var dx:Float, private var dy:Float, private val region: TextureRegion): Element() {
    init {
        region.scale=2.5f
    }
    override fun draw() {
        super.draw()
        Draw.alpha(color.a)
        Draw.rect(region, dx, dy)
    }
}