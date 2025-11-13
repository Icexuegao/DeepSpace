package ice.library.scene.element

import arc.func.Floatp
import arc.graphics.g2d.Draw
import arc.math.Mathf
import arc.scene.Element
import ice.library.scene.layout.ProgressAttribute
import kotlin.math.min

class ProgressBar(var pa: ProgressAttribute, private var fraction: Floatp) : Element() {
    private var value = 0f

    init {
        value = fraction.get()
    }

    fun reset(value: Float) {
        this.value = value
    }

    override fun getPrefHeight(): Float {
        return pa.getHeight()
    }

    override fun getHeight(): Float {
        return pa.getHeight()
    }

    override fun getPrefWidth(): Float {
        return pa.getWidth()
    }

    override fun getWidth(): Float {
        return pa.getWidth()
    }

    override fun draw() {
        var computed = Mathf.clamp(fraction.get())
        if (value.isNaN()) value = 0f
        if (value.isInfinite()) value = 1f
        if (computed.isNaN()) computed = 0f
        if (computed.isInfinite()) computed = 1f

        value = Mathf.lerpDelta(value, computed, 0.15f)
        Draw.alpha(color.a * parentAlpha)
        pa.bottomBar?.draw(x, y, width, height)
        Draw.color(pa.color, pa.color.a * parentAlpha)
        pa.barTop.draw(x + pa.starX, y + pa.starY, min(width * value, width - (pa.starX * 2)),  pa.drawHeight)
        Draw.color()
        Draw.alpha(color.a * parentAlpha)
        pa.barBackgroundDrawable.draw(x, y, width, height)

    }
}
