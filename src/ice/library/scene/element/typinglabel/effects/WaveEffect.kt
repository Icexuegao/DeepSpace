package ice.library.scene.element.typinglabel.effects

import arc.math.Interp
import ice.library.scene.element.typinglabel.Effect
import ice.library.scene.element.typinglabel.TLabel
import ice.library.scene.element.typinglabel.TypingGlyph

/** Moves the text vertically in a sine wave pattern.  */
class WaveEffect(label: TLabel, params: Array<String?>) : Effect(label) {
    companion object {
        private const val DEFAULT_FREQUENCY = 15f
        private const val DEFAULT_DISTANCE = 0.33f
        private const val DEFAULT_INTENSITY = 0.5f
    }

    private var distance = 1f // 它们应该移动多少身高
    private var frequency = 1f // 波形模式重复的频率
    private var intensity = 1f // 符文应该移动多快

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
        val progress = calculateProgress(progressModifier, progressOffset)

        // Calculate offset
        var y = lineHeight * distance * Interp.sine.apply(-1f, 1f, progress) * DEFAULT_DISTANCE

        // Calculate fadeout
        val fadeout = calculateFadeout()
        y *= fadeout

        // Apply changes
        glyph.yoffset = (glyph.yoffset + y).toInt()
    }

}
