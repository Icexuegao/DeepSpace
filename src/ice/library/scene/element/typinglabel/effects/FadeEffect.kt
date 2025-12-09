
package ice.library.scene.element.typinglabel.effects

import arc.graphics.Color
import arc.math.Mathf
import arc.struct.IntFloatMap
import ice.library.scene.element.typinglabel.Effect
import ice.library.scene.element.typinglabel.TypingGlyph
import ice.library.scene.element.typinglabel.TLabel

/** Fades the text's color from between colors or alphas. Doesn't repeat itself.  */
class FadeEffect(label: TLabel, params: Array<String?>) : Effect(label) {
    private var color1: Color? = null // First color of the effect.
    private var color2: Color? = null // Second color of the effect.
    private var alpha1 = 0f // First alpha of the effect, in case a color isn't provided.
    private var alpha2 = 1f // Second alpha of the effect, in case a color isn't provided.
    private var fadeDuration = 1f // Duration of the fade effect

    private val timePassedByGlyphIndex = IntFloatMap()

    init {
        // Color 1 or Alpha 1
        if (params.isNotEmpty()) {
            this.color1 = paramAsColor(params[0])
            if (this.color1 == null) {
                alpha1 = paramAsFloat(params[0], 0f)
            }
        }

        // Color 2 or Alpha 2
        if (params.size > 1) {
            this.color2 = paramAsColor(params[1])
            if (this.color2 == null) {
                alpha2 = paramAsFloat(params[1], 1f)
            }
        }

        // Fade duration
        if (params.size > 2) {
            this.fadeDuration = paramAsFloat(params[2], 1f)
        }
    }

    override fun onApply(glyph: TypingGlyph, localIndex: Int, delta: Float) {
        // Calculate progress
        val timePassed = timePassedByGlyphIndex.increment(localIndex, 0f, delta)
        val progress = Mathf.clamp(timePassed / fadeDuration, 0f, 1f)

        // Create glyph color if necessary
        val color = glyph.color
        if (color == null) {
            glyph.color = Color(Integer.reverseBytes(glyph.runColor))
        }
        color!!
        // Calculate initial color
        if (this.color1 == null) {
            color.a = Mathf.lerp(color.a, this.alpha1, 1f - progress)
        } else {
            color.lerp(this.color1, 1f - progress)
        }

        // Calculate final color
        if (this.color2 == null) {
            color.a = Mathf.lerp(color.a, this.alpha2, progress)
        } else {
            color.lerp(this.color2, progress)
        }
    }
}
