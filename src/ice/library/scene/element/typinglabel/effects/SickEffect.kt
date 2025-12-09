package ice.library.scene.element.typinglabel.effects

import arc.math.Interp
import arc.struct.IntSeq
import ice.library.scene.element.typinglabel.Effect
import ice.library.scene.element.typinglabel.TypingGlyph
import ice.library.scene.element.typinglabel.TLabel

/** Drips the text in a random pattern.  */
class SickEffect(label: TLabel, params: Array<String?>) : Effect(label) {
    var distance: Float = 1f // How far the glyphs should move
    var intensity: Float = 1f // How fast the glyphs should move

    private val indices = IntSeq()

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
        // Calculate progress
        val progressModifier = (1f / intensity) * DEFAULT_INTENSITY
        val progressOffset = localIndex / DEFAULT_FREQUENCY
        val progress = calculateProgress(progressModifier, -progressOffset, false)

        if (progress < .01f && Math.random() > .25f && !indices.contains(localIndex)) indices.add(localIndex)
        if (progress > .95f) indices.removeValue(localIndex)

        if (!indices.contains(localIndex) && !indices.contains(localIndex - 1) && !indices.contains(localIndex - 2) && !indices.contains(localIndex + 2) && !indices.contains(localIndex + 1)) return

        val split = 0.5f
        // Calculate offset
        val interpolation = if (progress < split) {
            Interp.pow2Out.apply(0f, 1f, progress / split)
        } else {
            Interp.pow2In.apply(1f, 0f, (progress - split) / (1f - split))
        }
        var y = lineHeight * distance * interpolation * DEFAULT_DISTANCE

        if (indices.contains(localIndex)) y *= 2.15f
        if (indices.contains(localIndex - 1) || indices.contains(localIndex + 1)) y *= 1.35f

        // Calculate fadeout
        val fadeout = calculateFadeout()
        y *= fadeout

        // Apply changes
        glyph.yoffset = (glyph.yoffset - y).toInt()
    }

    companion object {
        private const val DEFAULT_FREQUENCY = 50f
        private const val DEFAULT_DISTANCE = .125f
        private const val DEFAULT_INTENSITY = 1f
    }
}
