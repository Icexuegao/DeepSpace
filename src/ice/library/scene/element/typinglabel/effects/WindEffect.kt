
package ice.library.scene.element.typinglabel.effects

import ice.library.scene.element.typinglabel.Effect
import ice.library.scene.element.typinglabel.TypingGlyph
import ice.library.scene.element.typinglabel.TLabel
import ice.library.scene.element.typinglabel.utils.SimplexNoise
import kotlin.math.abs
import kotlin.math.sign

/** Moves the text in a wind pattern.  */
class WindEffect(label: TLabel, params: Array<String?>) : Effect(label) {
    private val noise = SimplexNoise(1, 0.5f, 1f)
    private var noiseCursorX = 0f
    private var noiseCursorY = 0f

    private var distanceX = 1f // How much of their line height glyphs should move in the X axis
    private var distanceY = 1f // How much of their line height glyphs should move in the Y axis
    private var spacing = 1f // How much space there should be between waves
    private var intensity = 1f // How strong the wind should be

    init {
        // Distance X
        if (params.isNotEmpty()) {
            this.distanceX = paramAsFloat(params[0], 1f)
        }

        // Distance Y
        if (params.size > 1) {
            this.distanceY = paramAsFloat(params[1], 1f)
        }

        // Spacing
        if (params.size > 2) {
            this.spacing = paramAsFloat(params[2], 1f)
        }

        // Intensity
        if (params.size > 3) {
            this.intensity = paramAsFloat(params[3], 1f)
        }

        // Duration
        if (params.size > 4) {
            this.duration = paramAsFloat(params[4], -1f)
        }
    }

    override fun update(delta: Float) {
        super.update(delta)

        // Update noise cursor
        val changeAmount = 0.1f * intensity * DEFAULT_INTENSITY * delta * IDEAL_DELTA
        noiseCursorX += changeAmount
        noiseCursorY += changeAmount
    }

    override fun onApply(glyph: TypingGlyph, localIndex: Int, delta: Float) {
        // Calculate progress
        val progressModifier = DEFAULT_INTENSITY / intensity
        val normalSpacing = DEFAULT_SPACING / spacing
        val progressOffset = localIndex / normalSpacing
        val progress = calculateProgress(progressModifier, progressOffset)

        // Calculate noise
        val indexOffset = localIndex * 0.05f * spacing
        val noiseX = noise.getNoise(noiseCursorX + indexOffset, 0f)
        val noiseY = noise.getNoise(0f, noiseCursorY + indexOffset)

        // Calculate offset
        val lineHeight = getLineHeight()
        var x = lineHeight * noiseX * progress * distanceX * DISTANCE_X_RATIO * DEFAULT_DISTANCE
        var y = lineHeight * noiseY * progress * distanceY * DISTANCE_Y_RATIO * DEFAULT_DISTANCE

        // Calculate fadeout
        val fadeout = calculateFadeout()
        x *= fadeout
        y *= fadeout

        // Add flag effect to X offset
        x = abs(x) * -sign(distanceX)

        // Apply changes
        glyph.xoffset = (glyph.xoffset + x).toInt()
        glyph.yoffset = (glyph.yoffset + y).toInt()
    }

    companion object {
        private const val DEFAULT_SPACING = 10f
        private const val DEFAULT_DISTANCE = 0.33f
        private const val DEFAULT_INTENSITY = 0.375f
        private const val DISTANCE_X_RATIO = 1.5f
        private const val DISTANCE_Y_RATIO = 1.0f
        private const val IDEAL_DELTA = 60f
    }
}
