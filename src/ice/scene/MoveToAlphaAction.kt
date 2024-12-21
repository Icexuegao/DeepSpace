package ice.scene

import arc.graphics.Color
import arc.scene.actions.TemporalAction
import arc.util.Align

class MoveToAlphaAction : TemporalAction() {
    private var startX = 0f
    private var startY = 0f
    var endX = 0f
    var endY = 0f
    var alignment = Align.bottomLeft

    private var start = 0f
    private var end = 0f
    var color: Color? = null

    override fun begin() {
        startX = target.getX(alignment)
        startY = target.getY(alignment)

        if (color == null) color = target.color
        start = color!!.a
    }

    override fun update(percent: Float) {
        target.setPosition(startX + (endX - startX) * percent, startY + (endY - startY) * percent, alignment)

        color!!.a = start + (end - start) * percent
    }

    override fun reset() {
        super.reset()
        alignment = Align.bottomLeft

        color = null
    }

    fun setPosition(x: Float, y: Float) {
        endX = x
        endY = y
    }

    fun setPosition(x: Float, y: Float, alignment: Int) {
        endX = x
        endY = y
        this.alignment = alignment
    }

    fun getAlpha(): Float {
        return end
    }

    fun setAlpha(alpha: Float) {
        end = alpha
    }
}