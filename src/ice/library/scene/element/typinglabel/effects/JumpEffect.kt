package ice.library.scene.element.typinglabel.effects

import arc.math.Interp
import ice.library.scene.element.typinglabel.Effect
import ice.library.scene.element.typinglabel.TypingGlyph
import ice.library.scene.element.typinglabel.TLabel

/** Makes the text jumps and falls as if there was gravity. */
class JumpEffect(label: TLabel, params: Array<String?>) : Effect(label) {
    private var distance = 1f // How much of their height they should move
    private var frequency = 1f // How frequently the wave pattern repeats
    private var intensity = 1f // How fast the glyphs should move

    init {
        // Distance
        if (params.isNotEmpty()) {
            this.distance = paramAsFloat(params[0], 1f)
        }

        // Frequency
        if (params.size > 1) {
            this.frequency = paramAsFloat(params[1], 1f)
        }

        // Intensity
        if (params.size > 2) {
            this.intensity = paramAsFloat(params[2], 1f)
        }

        // Duration
        if (params.size > 3) {
            this.duration = paramAsFloat(params[3], -1f)
        }
    }

    override fun onApply(glyph: TypingGlyph, localIndex: Int, delta: Float) {
        // Calculate progress
        val progressModifier = (1f / intensity) * DEFAULT_INTENSITY
        val normalFrequency = (1f / frequency) * DEFAULT_FREQUENCY
        val progressOffset = localIndex / normalFrequency
        val progress = calculateProgress(progressModifier, -progressOffset, false)

        // Calculate offset
        var interpolation: Float
        val split = 0.2f
        interpolation = if (progress < split) {
            Interp.pow2Out.apply(0f, 1f, progress / split)
        } else {
            Interp.bounceOut.apply(1f, 0f, (progress - split) / (1f - split))
        }
        var y = lineHeight * distance * interpolation * DEFAULT_DISTANCE

        // Calculate fadeout
        val fadeout = calculateFadeout()
        y *= fadeout

        // Apply changes
        glyph.yoffset = (glyph.yoffset + y).toInt()
    }

    companion object {
        private const val DEFAULT_FREQUENCY = 50f
        private const val DEFAULT_DISTANCE = 1.33f
        private const val DEFAULT_INTENSITY = 1f
    }
}
