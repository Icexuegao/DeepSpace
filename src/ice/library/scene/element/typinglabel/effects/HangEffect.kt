package ice.library.scene.element.typinglabel.effects

import arc.math.Interp
import arc.math.Mathf
import arc.struct.IntFloatMap
import ice.library.scene.element.typinglabel.Effect
import ice.library.scene.element.typinglabel.TypingGlyph
import ice.library.scene.element.typinglabel.TLabel

/** Hangs the text in midair and suddenly drops it. Doesn't repeat itself.  */
class HangEffect(label: TLabel, params: Array<String?>) : Effect(label) {
    private var distance = 1f // How much of their height they should move
    private var intensity = 1f // How fast the glyphs should move

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
    }

    override fun onApply(glyph: TypingGlyph, localIndex: Int, delta: Float) {
        // Calculate real intensity
        val realIntensity = intensity * 1f * DEFAULT_INTENSITY

        // Calculate progress
        val timePassed = timePassedByGlyphIndex.increment(localIndex, 0f, delta)
        val progress = Mathf.clamp(timePassed / realIntensity, 0f, 1f)

        // Calculate offset
        val interpolation: Float
        val split = 0.7f
        interpolation = if (progress < split) {
            Interp.pow3Out.apply(0f, 1f, progress / split)
        } else {
            Interp.swing.apply(1f, 0f, (progress - split) / (1f - split))
        }
        val distanceFactor = Interp.linear.apply(1.0f, 1.5f, progress)
        var y = lineHeight * distance * distanceFactor * interpolation * DEFAULT_DISTANCE

        // Calculate fadeout
        val fadeout = calculateFadeout()
        y *= fadeout

        // Apply changes
        glyph.yoffset = (glyph.yoffset + y).toInt()
    }

    companion object {
        private const val DEFAULT_DISTANCE = 0.7f
        private const val DEFAULT_INTENSITY = 1.5f
    }
}
