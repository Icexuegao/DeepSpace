package ice.library.scene.element.typinglabel

import arc.graphics.Color
import arc.struct.ObjectFloatMap
import arc.struct.ObjectMap
import ice.library.scene.element.typinglabel.effects.*
import java.util.*

/** Configuration class that easily allows the user to fine tune the library's functionality.  */
object TypingConfig {
    /**
     * Whether or not [LibGDX's Color Markup
 * Language](https://github.com/libgdx/libgdx/wiki/Color-Markup-Language) should be enabled when parsing a [TLabel]. Note that this library doesn't truly handle
     * colors, but simply convert them to the markup format. If markup is disabled, color tokens will be ignored.
     */
    var FORCE_COLOR_MARKUP_BY_DEFAULT: Boolean = true

    /** Default time in seconds that an empty `WAIT` token should wait for. Default value is `0.250`.  */
    var DEFAULT_WAIT_VALUE: Float = 0.250f

    /** Time in seconds that takes for each char to appear in the default speed. Default value is `0.035`.  */
    var DEFAULT_SPEED_PER_CHAR: Float = 0.035f

    /**
     * Minimum value for the `SPEED` token. This value divides [.DEFAULT_SPEED_PER_CHAR] to calculate the
     * final speed. Keep it above zero. Default value is `0.001`.
     */
    var MIN_SPEED_MODIFIER: Float = 0.001f

    /**
     * Maximum value for the `SPEED` token. This value divides [.DEFAULT_SPEED_PER_CHAR] to calculate the
     * final speed. Default value is `100`.
     */
    var MAX_SPEED_MODIFIER: Float = 100.0f

    /**
     * Defines how many chars can appear per frame. Use a value less than `1` to disable this limit. Default value
     * is `-1`.
     */
    var CHAR_LIMIT_PER_FRAME: Int = -1

    /** Default color for the `CLEARCOLOR` token. Can be overriden by [TLabel.getClearColor].  */
    var defaultClearColor: Color = Color(Color.white)

    /** Characters used to start and end tokens. Defaults to [TokenDelimiter.CURLY_BRACKETS].  */
    var TOKEN_DELIMITER: TokenDelimiter = TokenDelimiter.CURLY_BRACKETS

    /**
     * Returns a map of characters and their respective interval multipliers, of which the interval to the next char
     * should be multiplied for.
     */
    var INTERVAL_MULTIPLIERS_BY_CHAR: ObjectFloatMap<Char?> = ObjectFloatMap<Char?>()

    /** Map of global variables that affect all [TLabel] instances at once.  */
    val GLOBAL_VARS: ObjectMap<String?, String?> = ObjectMap<String?, String?>()

    /** Map of start tokens and their effect classes. Internal use only.  */
    val EFFECT_START_TOKENS: ObjectMap<String?, Class<out Effect?>?> = ObjectMap<String?, Class<out Effect?>?>()

    /** Map of end tokens and their effect classes. Internal use only.  */
    val EFFECT_END_TOKENS: ObjectMap<String?, Class<out Effect?>?> = ObjectMap<String?, Class<out Effect?>?>()

    /**效果标记是否脏,需要重新计算。  */
    var dirtyEffectMaps: Boolean = true

    /**
     * Registers a new effect to TypingLabel.
     *
     * @param startTokenName Name of the token that starts the effect, such as WAVE.
     * @param endTokenName   Name of the token that ends the effect, such as ENDWAVE.
     * @param effectClass    Class of the effect, such as WaveEffect.class.
     */
    fun registerEffect(startTokenName: String, endTokenName: String, effectClass: Class<out Effect>) {
        EFFECT_START_TOKENS.put(startTokenName.uppercase(Locale.getDefault()), effectClass)
        EFFECT_END_TOKENS.put(endTokenName.uppercase(Locale.getDefault()), effectClass)
        dirtyEffectMaps = true
    }

    /**
     * Unregisters an effect from TypingLabel.
     *
     * @param startTokenName Name of the token that starts the effect, such as WAVE.
     * @param endTokenName   Name of the token that ends the effect, such as ENDWAVE.
     */
    fun unregisterEffect(startTokenName: String, endTokenName: String) {
        EFFECT_START_TOKENS.remove(startTokenName.uppercase(Locale.getDefault()))
        EFFECT_END_TOKENS.remove(endTokenName.uppercase(Locale.getDefault()))
    }

    init {
        // Generate default char intervals
        INTERVAL_MULTIPLIERS_BY_CHAR.put(' ', 0.0f)
        INTERVAL_MULTIPLIERS_BY_CHAR.put(':', 1.5f)
        INTERVAL_MULTIPLIERS_BY_CHAR.put(',', 2.5f)
        INTERVAL_MULTIPLIERS_BY_CHAR.put('.', 2.5f)
        INTERVAL_MULTIPLIERS_BY_CHAR.put('!', 5.0f)
        INTERVAL_MULTIPLIERS_BY_CHAR.put('?', 5.0f)
        INTERVAL_MULTIPLIERS_BY_CHAR.put('\n', 20f)

        // Register default tokens
        registerEffect("EASE", "ENDEASE", EaseEffect::class.java)
        registerEffect("HANG", "ENDHANG", HangEffect::class.java)
        registerEffect("JUMP", "ENDJUMP", JumpEffect::class.java)
        registerEffect("SHAKE", "ENDSHAKE", ShakeEffect::class.java)
        registerEffect("SICK", "ENDSICK", SickEffect::class.java)
        registerEffect("SLIDE", "ENDSLIDE", SlideEffect::class.java)
        registerEffect("WAVE", "ENDWAVE", WaveEffect::class.java)
        registerEffect("WIND", "ENDWIND", WindEffect::class.java)
        registerEffect("RAINBOW", "ENDRAINBOW", RainbowEffect::class.java)
        registerEffect("GRADIENT", "ENDGRADIENT", GradientEffect::class.java)
        registerEffect("FADE", "ENDFADE", FadeEffect::class.java)
        registerEffect("BLINK", "ENDBLINK", BlinkEffect::class.java)
        registerEffect("GARBLED", "ENDGARBLED", GarbledEffect::class.java)
    }
}
