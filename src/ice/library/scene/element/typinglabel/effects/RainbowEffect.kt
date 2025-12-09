package ice.library.scene.element.typinglabel.effects

import arc.graphics.Color
import ice.library.scene.element.typinglabel.Effect
import ice.library.scene.element.typinglabel.TypingGlyph
import ice.library.scene.element.typinglabel.TLabel

/** Tints the text in a rainbow pattern. */
//文本必须是白色才能应用次效果
class RainbowEffect(label: TLabel, params: Array<String?>) : Effect(label) {
    companion object {
        private const val DEFAULT_DISTANCE = 0.975f
        private const val DEFAULT_FREQUENCY = 2f
    }
    private var distance = 1f // How extensive the rainbow effect should be.
    private var frequency = 1f // How frequently the color pattern should move through the text.
    private var saturation = 1f // Color saturation
    private var brightness = 1f // Color brightness

    init {
        // Distance
        if (params.isNotEmpty()) {
            this.distance = paramAsFloat(params[0], 1f)
        }

        // Frequency
        if (params.size > 1) {
            this.frequency = paramAsFloat(params[1], 1f)
        }

        // Saturation
        if (params.size > 2) {
            this.saturation = paramAsFloat(params[2], 1f)
        }

        // Brightness
        if (params.size > 3) {
            this.brightness = paramAsFloat(params[3], 1f)
        }
    }

    override fun onApply(glyph: TypingGlyph, localIndex: Int, delta: Float) {
        // Calculate progress
        val distanceMod = (1f / distance) * (1f - DEFAULT_DISTANCE)
        val frequencyMod = (1f / frequency) *DEFAULT_FREQUENCY
        val progress = calculateProgress(frequencyMod, distanceMod * localIndex, false)
        // Calculate color
        if (glyph.color == null) {
            glyph.color = Color(Color.white)
        }
        // Calculate color
        Color.HSVtoRGB(360f * progress, saturation * 100f, brightness * 100f, glyph.color)
    }




}
