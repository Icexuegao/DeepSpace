package ice.library.scene.element.typinglabel.effects

import arc.graphics.Color
import arc.math.Mathf
import ice.library.scene.element.typinglabel.Effect
import ice.library.scene.element.typinglabel.TypingGlyph
import ice.library.scene.element.typinglabel.TLabel

/** Creates a garbled text effect by randomly replacing characters. */
class GarbledEffect(label: TLabel, params: Array<String?>) : Effect(label) {
    private var intensity = 1f // How intense the garbling effect is
    private var threshold = 0.5f // Threshold for character replacement
    private var garbledChars = "蜿ｯ謔ｲ逧諢夊｢逧蟆丞ｧ大ｨ倡鯵荳ｽ荳蝨ｨ荳榊庄諤晁ｮｮ荵句嵜貍ｫ豁･逹迢よｰ秘先ｸ蝉ｾｵ陏迪ｫ蜆ｿ蜿大蝌ｲ隨譌莨第裏豁｢逧幻莨壻ｸ郤｢闌ｶ荵溷ｷｲ扈丞蜃蟆ｱ霑櫁ｿ呎ｨ讓邉顔ｳ顔噪諢剰ｯｹ豺蛹悶∵ｺ｢蜃ｺ 豺ｷ蜷亥惠荳襍ｷ蜿ｪ譛我ｸ画律譛育噪譛亥ｽｱ 譏ｭ遉ｺ逹邇ｰ蝨ｨ逧慮髣ｴ偪偺怴媄儘偱僶乿僩妝偪崠僒弍乣抧乽儏傔傪偖懧僉帹僗鋜傔侓傘鋜墱乣帹夣偛偑偄偩偒偨偡偛偆傝偞峸傑擖偄偁偲仸偙偺嶌昳偵娷傑傟傞壒惡丒岠壥壒丒夋憸僨乕僞偺柍抐揮嵹摍傪嬛巭偟傑偡"
    private var changeInterval = 0.02f // Time between changes
    private val glyphStates = mutableMapOf<Int, GlyphState>() // Track each glyph's state

    data class GlyphState(
            var isGarbled: Boolean = false,
            var currentChar: Char,
            var lastChangeTime: Float = 0f
    )

    init {
        // Intensity
        if (params.isNotEmpty()) {
            this.intensity = paramAsFloat(params[0], 1f)
        }

        // Threshold
        if (params.size > 1) {
            this.threshold = paramAsFloat(params[1], 0.5f)
        }

        // Duration
        if (params.size > 2) {
            this.duration = paramAsFloat(params[2], -1f)
        }

        // Change interval
        if (params.size > 3) {
            this.changeInterval = paramAsFloat(params[3], 0.2f)
        }

        // Custom garbled characters
        if (params.size > 4) {
            this.garbledChars = params[4] ?: garbledChars
        }
    }

    override fun onApply(glyph: TypingGlyph, localIndex: Int, delta: Float) {
        // Calculate fadeout
        val fadeout = calculateFadeout()
        if (fadeout <= 0) return

        // Apply intensity
        val effectiveIntensity = intensity * fadeout

        // Get or create state for this glyph
        val state = glyphStates.getOrPut(localIndex) {
            GlyphState(
                isGarbled = false,
                currentChar = glyph.id.toChar(),
                lastChangeTime = 0f
            )
        }

        // Update change timer
        state.lastChangeTime += delta

        // Check if we should change the character
        if (state.lastChangeTime >= changeInterval) {
            state.lastChangeTime = 0f

            // Randomly decide if we should garble this character
            val shouldGarble = Mathf.random() < threshold * effectiveIntensity

            // Only change state if different
            if (state.isGarbled != shouldGarble) {
                state.isGarbled = shouldGarble

                if (shouldGarble) {
                    // Get a random character and its glyph
                    val randomChar = garbledChars.random()
                    val newGlyph = label.fontCache.font.data.getGlyph(randomChar)

                    if (newGlyph != null) {
                        // Copy glyph properties
                        glyph.id = newGlyph.id
                        glyph.width = newGlyph.width
                        glyph.height = newGlyph.height
                        glyph.xoffset = newGlyph.xoffset
                        glyph.yoffset = newGlyph.yoffset
                        glyph.srcX = newGlyph.srcX
                        glyph.srcY = newGlyph.srcY
                        glyph.u = newGlyph.u
                        glyph.v = newGlyph.v
                        glyph.u2 = newGlyph.u2
                        glyph.v2 = newGlyph.v2
                        glyph.page = newGlyph.page
                        state.currentChar = randomChar
                    }
                } else {
                    // Restore original glyph
                    val originalGlyph = label.fontCache.font.data.getGlyph(state.currentChar)
                    if (originalGlyph != null) {
                        glyph.id = originalGlyph.id
                        glyph.width = originalGlyph.width
                        glyph.height = originalGlyph.height
                        glyph.xoffset = originalGlyph.xoffset
                        glyph.yoffset = originalGlyph.yoffset
                        glyph.srcX = originalGlyph.srcX
                        glyph.srcY = originalGlyph.srcY
                        glyph.u = originalGlyph.u
                        glyph.v = originalGlyph.v
                        glyph.u2 = originalGlyph.u2
                        glyph.v2 = originalGlyph.v2
                        glyph.page = originalGlyph.page
                        glyph.color= Color(Color.white)
                    }
                }
            }
        }
    }
}
