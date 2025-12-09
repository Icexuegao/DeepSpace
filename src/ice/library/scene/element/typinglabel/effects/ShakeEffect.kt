package ice.library.scene.element.typinglabel.effects

import arc.math.Interp
import arc.math.Mathf
import arc.struct.FloatSeq
import ice.library.scene.element.typinglabel.Effect
import ice.library.scene.element.typinglabel.TypingGlyph
import ice.library.scene.element.typinglabel.TLabel
import kotlin.math.roundToInt

/** Shakes the text in a random pattern.  */
class ShakeEffect(label: TLabel, params: Array<String?>) : Effect(label) {
    private val lastOffsets = FloatSeq()

    private var distance = 1f // How far the glyphs should move
    private var intensity = 1f // How fast the glyphs should move

    init {
        // Distance
        if (params.isNotEmpty()) {
            this.distance = paramAsFloat(params[0], 1f)
        }

        // Intensity
        if (params.size > 1) {
            this.intensity = paramAsFloat(params[1], 1f)
        }

        // Duration
        if (params.size > 2) {
            this.duration = paramAsFloat(params[2], -1f)
        }
    }

    override fun onApply(glyph: TypingGlyph, localIndex: Int, delta: Float) {
        // Make sure we can hold enough entries for the current index
        if (localIndex >= lastOffsets.size / 2) {
            lastOffsets.setSize(lastOffsets.size + 16)
        }

        // Get last offsets
        val lastX = lastOffsets.get(localIndex * 2)
        val lastY = lastOffsets.get(localIndex * 2 + 1)

        // Calculate new offsets
        var x = lineHeight * distance * Mathf.random(-1, 1) * DEFAULT_DISTANCE
        var y = lineHeight * distance * Mathf.random(-1, 1) * DEFAULT_DISTANCE

        // Apply intensity
        val normalIntensity = Mathf.clamp(intensity * DEFAULT_INTENSITY, 0f, 1f)
        x = Interp.linear.apply(lastX, x, normalIntensity)
        y = Interp.linear.apply(lastY, y, normalIntensity)

        // Apply fadeout
        val fadeout = calculateFadeout()
        x *= fadeout
        y *= fadeout
        x = x.roundToInt().toFloat()
        y = y.roundToInt().toFloat()

        // Store offsets for the next tick
        lastOffsets.set(localIndex * 2, x)
        lastOffsets.set(localIndex * 2 + 1, y)

        // Apply changes
        glyph.xoffset = (glyph.xoffset + x).toInt()
        glyph.yoffset = (glyph.yoffset + y).toInt()
    }

    companion object {
        private const val DEFAULT_DISTANCE = 0.12f
        private const val DEFAULT_INTENSITY = 0.5f
    }
}
