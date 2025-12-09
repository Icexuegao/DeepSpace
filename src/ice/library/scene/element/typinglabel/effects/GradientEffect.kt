package ice.library.scene.element.typinglabel.effects

import arc.graphics.Color
import ice.library.scene.element.typinglabel.Effect
import ice.library.scene.element.typinglabel.TypingGlyph
import ice.library.scene.element.typinglabel.TLabel

/** Tints the text in a gradient pattern.  */
class GradientEffect(label: TLabel, params: Array<String?>) : Effect(label) {
    private var color1: Color? = null // First color of the gradient.
    private var color2: Color? = null // Second color of the gradient.
    private var distance = 1f // How extensive the rainbow effect should be.
    private var frequency = 1f // How frequently the color pattern should move through the text.

    init {
        // Color 1
        if (params.isNotEmpty()) {
            this.color1 = paramAsColor(params[0])
        }

        // Color 2
        if (params.size > 1) {
            this.color2 = paramAsColor(params[1])
        }

        // Distance
        if (params.size > 2) {
            this.distance = paramAsFloat(params[2], 1f)
        }

        // Frequency
        if (params.size > 3) {
            this.frequency = paramAsFloat(params[3], 1f)
        }

        // Validate parameters
        if (this.color1 == null) this.color1 = Color(Color.white)
        if (this.color2 == null) this.color2 = Color(Color.white)
    }

    override fun onApply(glyph: TypingGlyph, localIndex: Int, delta: Float) {
        // Calculate progress
        val distanceMod = (1f / distance) * (1f - DEFAULT_DISTANCE)
        val frequencyMod = (1f / frequency) * DEFAULT_FREQUENCY
        val progress = calculateProgress(frequencyMod, distanceMod * localIndex, true)

        // Calculate color
        if (glyph.color == null) glyph.color = Color(Color.white)
        glyph.color?.set(color1)?.lerp(color2, progress)
    }

    companion object {
        private const val DEFAULT_DISTANCE = 0.975f
        private const val DEFAULT_FREQUENCY = 2f
    }
}
