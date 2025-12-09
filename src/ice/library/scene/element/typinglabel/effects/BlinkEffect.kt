
package ice.library.scene.element.typinglabel.effects

import arc.graphics.Color
import arc.math.Mathf
import ice.library.scene.element.typinglabel.Effect
import ice.library.scene.element.typinglabel.TypingGlyph
import ice.library.scene.element.typinglabel.TLabel

/** Blinks the entire text in two different colors at once, without interpolation.  */
class BlinkEffect(label: TLabel, params: Array<String?>) : Effect(label) {
    private var color1: Color? = null // First color of the effect.
    private var color2: Color? = null // Second color of the effect.
    private var frequency = 1f // How frequently the color pattern should move through the text.
    private var threshold = 0.5f // Point to switch colors.

    init {
        // Color 1
        if (params.isNotEmpty()) {
            this.color1 = paramAsColor(params[0])
        }

        // Color 2
        if (params.size > 1) {
            this.color2 = paramAsColor(params[1])
        }

        // Frequency
        if (params.size > 2) {
            this.frequency = paramAsFloat(params[2], 1f)
        }

        // Threshold
        if (params.size > 3) {
            this.threshold = paramAsFloat(params[3], 0.5f)
        }

        // Validate parameters
        if (this.color1 == null) this.color1 = Color(Color.white)
        if (this.color2 == null) this.color2 = Color(Color.white)
        this.threshold = Mathf.clamp(this.threshold, 0f, 1f)
    }

    override fun onApply(glyph: TypingGlyph, localIndex: Int, delta: Float) {
        // Calculate progress
        val frequencyMod = (1f / frequency) * DEFAULT_FREQUENCY
        val progress = calculateProgress(frequencyMod)

        // Calculate color
        glyph.color?.set(if (progress <= threshold) color1 else color2)
    }

    companion object {
        private const val DEFAULT_FREQUENCY = 1f
    }
}
