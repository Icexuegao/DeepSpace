package ice.library.scene.element

import arc.func.Floatp
import arc.graphics.g2d.Draw
import arc.math.Mathf
import arc.scene.Element
import arc.scene.style.Drawable
import kotlin.math.min

class BaseProgressBar(var back: Drawable, var prog: Drawable, private var fraction: Floatp) : Element() {
    private var value = 0f

    init {
        value = fraction.get()
    }

    override fun draw() {
        var computed = Mathf.clamp(fraction.get())
        if (value.isNaN()) value = 0f
        if (value.isInfinite()) value = 1f
        if (computed.isNaN()) computed = 0f
        if (computed.isInfinite()) computed = 1f

        value = Mathf.lerpDelta(value, computed, 0.15f)
        Draw.alpha(color.a * parentAlpha)
        back.draw(x, y, width, height)
        Draw.color(color)
        Draw.alpha(color.a * parentAlpha)
        prog.draw(x, y, min(width * value, width), height)
        Draw.color()
        Draw.reset()
    }
}