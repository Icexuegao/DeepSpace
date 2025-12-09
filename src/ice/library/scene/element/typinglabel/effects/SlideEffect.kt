
package ice.library.scene.element.typinglabel.effects

import arc.math.Interp
import arc.math.Mathf
import arc.struct.IntFloatMap
import ice.library.scene.element.typinglabel.Effect
import ice.library.scene.element.typinglabel.TypingGlyph
import ice.library.scene.element.typinglabel.TLabel

/** Moves the text horizontally easing it into the final position. Doesn't repeat itself.  */
class SlideEffect(label: TLabel, params: Array<String?>) : Effect(label) {
    private var distance = 1f // How much of their height they should move
    private var intensity = 1f // How fast the glyphs should move
    private var elastic = false // Whether or not the glyphs have an elastic movement

    private val timePassedByGlyphIndex = IntFloatMap()

    init {
        // Distance
        if (params.isNotEmpty()) {
            this.distance = paramAsFloat(params[0], 1f)
        }

        // Intensity
        if (params.size > 1) {
            this.intensity = paramAsFloat(params[1], 1f)
        }

        // Elastic
        if (params.size > 2) {
            this.elastic = paramAsBoolean(params[2])
        }
    }

    override fun onApply(glyph: TypingGlyph, localIndex: Int, delta: Float) {
        // Calculate real intensity
        val realIntensity = intensity * (if (elastic) 3f else 1f) * DEFAULT_INTENSITY

        // Calculate progress
        val timePassed = timePassedByGlyphIndex.increment(localIndex, 0f, delta)
        val progress = Mathf.clamp(timePassed / realIntensity, 0f, 1f)

        // Calculate offset
        val interpolation = if (elastic) Interp.swingOut else Interp.sine
        val interpolatedValue = interpolation.apply(1f, 0f, progress)
        val x = lineHeight * distance * interpolatedValue * DEFAULT_DISTANCE

        // Apply changes
        glyph.xoffset = (glyph.xoffset + x).toInt()
    }

    companion object {
        private const val DEFAULT_DISTANCE = 2f
        private const val DEFAULT_INTENSITY = 0.375f
    }
}
