package ice.library.scene.element.typinglabel

import arc.graphics.Color
import arc.math.Interp
import arc.math.Mathf
import ice.library.scene.element.typinglabel.Parser.stringToBoolean
import ice.library.scene.element.typinglabel.Parser.stringToColor
import ice.library.scene.element.typinglabel.Parser.stringToFloat

/** Abstract text effect.  */
abstract class Effect(protected val label: TLabel) {
    var indexStart: Int = -1
    var indexEnd: Int = -1
    var duration: Float = Float.POSITIVE_INFINITY
    protected var totalTime: Float = 0f

    open fun update(delta: Float) {
        totalTime += delta
    }
    internal fun getLineHeight(): Float {
        return label.fontCache.font.lineHeight * label.getFontScaleY()
    }
    /** Applies the effect to the given glyph.  */
    fun apply(glyph: TypingGlyph, glyphIndex: Int, delta: Float) {
        val localIndex = glyphIndex - indexStart
        onApply(glyph, localIndex, delta)
    }

    /** Called when this effect should be applied to the given glyph.  */
    protected abstract fun onApply(glyph: TypingGlyph, localIndex: Int, delta: Float)

    val isFinished: Boolean
        /** Returns whether or not this effect is finished and should be removed. Note that effects are infinite by default.  */
        get() = totalTime > duration

    /** Calculates the fadeout of this effect, if any. Only considers the second half of the duration.  */
    protected fun calculateFadeout(): Float {
        if (java.lang.Float.isInfinite(duration)) return 1f

        // Calculate raw progress
        val progress = Mathf.clamp(totalTime / duration, 0f, 1f)

        // If progress is before the split point, return a full factor
        if (progress < FADEOUT_SPLIT) return 1f

        // Otherwise calculate from the split point
        return Interp.smooth.apply(1f, 0f, (progress - FADEOUT_SPLIT) / (1f - FADEOUT_SPLIT))
    }

    /** Calculates a linear progress dividing the total time by the given modifier. Returns a value between 0 and 1.  */
    /**
     * Calculates a linear progress dividing the total time by the given modifier. Returns a value between 0 and 1 that
     * loops in a ping-pong mode.
     */
    /**
     * Calculates a linear progress dividing the total time by the given modifier. Returns a value between 0 and 1 that
     * loops in a ping-pong mode.
     */
    protected fun calculateProgress(modifier: Float, offset: Float = 0f, pingpong: Boolean = true): Float {
        var progress = totalTime / modifier + offset
        while (progress < 0.0f) {
            progress += 2.0f
        }
        if (pingpong) {
            progress %= 2f
            if (progress > 1.0f) progress = 1f - (progress - 1f)
        } else {
            progress %= 1.0f
        }
        progress = Mathf.clamp(progress, 0f, 1f)
        return progress
    }

    protected val lineHeight: Float
        /** Returns the line height of the label controlling this effect.  */
        get() = label.fontCache.font.lineHeight * label.getFontScaleY()

    /** Returns a float value parsed from the given String, or the default value if the string couldn't be parsed.  */
    protected fun paramAsFloat(str: String?, defaultValue: Float): Float {
        return stringToFloat(str, defaultValue)
    }

    /** Returns a boolean value parsed from the given String, or the default value if the string couldn't be parsed.  */
    protected fun paramAsBoolean(str: String?): Boolean {
        return stringToBoolean(str)
    }

    /** Parses a color from the given string. Returns null if the color couldn't be parsed.  */
    protected fun paramAsColor(str: String?): Color? {
        return stringToColor(str)
    }

    companion object {
        private const val FADEOUT_SPLIT = 0.25f
    }
}
