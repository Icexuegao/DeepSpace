package ice.library.scene.element.typinglabel

import arc.graphics.Color
import arc.graphics.g2d.GlyphLayout
import arc.math.Mathf
import arc.scene.ui.Label
import arc.struct.IntSeq
import arc.struct.ObjectMap
import arc.struct.Seq
import arc.util.Align
import arc.util.pooling.Pools
import ice.library.scene.element.typinglabel.Parser.parseTokens
import java.lang.reflect.Field
import java.util.*
import kotlin.math.max

/**
 * An extension of [Label] that progressively shows the text as if it was being typed in real time, and allows the
 * use of tokens in the following format: <tt>{TOKEN=PARAMETER}</tt>.  */
class TLabel(text:CharSequence) : Label(text) {
    // Collections
    internal val variables = ObjectMap<String?, String?>()
    internal val tokenEntries: Seq<TokenEntry> = Seq<TokenEntry>()

    /**
     * Returns a [Color] instance with the color to be used on `CLEARCOLOR` tokens. Modify this instance to
     * change the token color. Default value is specified by [TypingConfig].
     * @see TypingConfig.defaultClearColor
     */
    // Config
    val clearColor: Color = Color(TypingConfig.defaultClearColor)

    /** Returns the [TypingListener]s associated with this label. May be empty.  */
    val typingListeners: Seq<TypingListener> = Seq<TypingListener>(TypingListener::class.java)

    /**
     * Sets whether or not this instance should enable markup color by force.
     * @see TypingConfig.FORCE_COLOR_MARKUP_BY_DEFAULT
     */
    var forceMarkupColor: Boolean = TypingConfig.FORCE_COLOR_MARKUP_BY_DEFAULT

    // Internal state
    internal val glyphCache = Seq<TypingGlyph>()
    private val glyphRunCapacities = IntSeq()
    private val offsetCache = IntSeq()
    private val layoutLineBreaks = IntSeq()
    private val activeEffects = Seq<Effect>()
    private var textSpeed = TypingConfig.DEFAULT_SPEED_PER_CHAR
    private var charCooldown = textSpeed
    private var rawCharIndex = -2 // All chars, including color codes
    private var glyphCharIndex = -1 // Only renderable chars, excludes color codes
    private var glyphCharCompensation = 0
    private var cachedGlyphCharIndex = -1 // Last glyphCharIndex sent to the cache
    private var lastLayoutX = 0f
    private var lastLayoutY = 0f
    private var parsed = false

    /** Returns whether or not this label is paused.  */
    var isPaused: Boolean = false
        private set
    private var ended = false

    /**
     * Returns whether or not this label is currently skipping its typing progression all the way to the end. This is
     * only true if skipToTheEnd is called.
     */
    var isSkipping: Boolean = false
        private set
    private var ignoringEvents = false
    private var ignoringEffects = false

    /** Returns the default token being used in this label. Defaults to empty string.  */
    var defaultToken: String = ""
        /**
         * Sets the default token being used in this label. This token will be used before the label's text, and after each
         * {RESET} call. Useful if you want a certain token to be active at all times without having to type it all the
         * time.
         */
        set(defaultToken) {
            field = defaultToken
            this.parsed = false
        }

    // Superclass mirroring
    //添加s以便和java分辨
    var wraps: Boolean = false
    var ellipsiss: String? = null
    var lastPrefHeights: Float = 0f
    var fontScaleChangeds: Boolean = false

    /** Similar to [.getText], but returns the original text with all the tokens unchanged.  */
    private val originalText = StringBuilder()


    init {
        saveOriginalText()
    }
    /**
     * 计算文本完全显示所需的总时间（秒）
     * @return 返回显示完整文本所需的时间，如果文本为空则返回 0
     */
    fun getTotalDuration(): Float {
        if (getText().isEmpty()) return 0f

        var totalTime = 0f
        var currentSpeed = TypingConfig.DEFAULT_SPEED_PER_CHAR
        val text = getText()

        // 创建 tokenEntries 的副本以避免修改原始数据
        val tokenEntriesCopy = Seq<TokenEntry>()
        tokenEntriesCopy.addAll(tokenEntries)

        // 用于记录需要跳过的字符范围
        var skipUntil = -1

        // 遍历所有字符
        for (charIndex in text.indices) {
            // 如果当前字符在跳过范围内，跳过它
            if (charIndex <= skipUntil) {
                continue
            }

            val c = text[charIndex]

            // 检查当前位置是否有标记
            while (tokenEntriesCopy.any() && tokenEntriesCopy.first().index == charIndex) {
                val entry = tokenEntriesCopy.remove(0)
                when (entry.category) {
                    TokenCategory.SPEED -> {
                        // SPEED 标记的值是速度的倍数，0.5表示速度是默认的一半（显示时间是2倍）
                        val minModifier = TypingConfig.MIN_SPEED_MODIFIER
                        val maxModifier = TypingConfig.MAX_SPEED_MODIFIER
                        val modifier = Mathf.clamp(entry.floatValue, minModifier, maxModifier)
                        currentSpeed = TypingConfig.DEFAULT_SPEED_PER_CHAR * modifier

                        // 计算标记字符串的完整长度，包括花括号
                        val tokenString = "{$entry.token=$entry.stringValue}"
                        skipUntil = charIndex + tokenString.length - 1
                    }
                    TokenCategory.WAIT -> {
                        totalTime += entry.floatValue
                    }
                    TokenCategory.SKIP -> {
                        // 跳过的字符不计入显示时间
                        entry.stringValue?.let {
                            skipUntil = charIndex + it.length
                        }
                    }
                    else -> {
                        // 对于其他类型的标记，也需要计算其完整长度并跳过
                        val tokenString = if (entry.stringValue != null) {
                            "{$entry.token=$entry.stringValue}"
                        } else {
                            "{$entry.token}"
                        }
                        skipUntil = charIndex + tokenString.length - 1
                    }
                }
            }

            // 如果当前字符在跳过范围内，不计算显示时间
            if (charIndex <= skipUntil) {
                continue
            }

            // 计算当前字符的显示时间
            val intervalMultiplier = TypingConfig.INTERVAL_MULTIPLIERS_BY_CHAR.get(c, 1f)
            val charTime = currentSpeed * intervalMultiplier
            totalTime += charTime
        }

        return totalTime
    }


    override fun setText(newText: CharSequence) {
        setText(newText, false)
    }

    /**
     * Sets the text of this label.
     * @param modifyOriginalText Flag determining if the original text should be modified as well. If `false`,the display text is changed while the original text is untouched.
     */
    private fun setText(newText: CharSequence, modifyOriginalText: Boolean) {
        setText(newText, modifyOriginalText, false)
    }

    /**
     * Sets the text of this label.
     * @param modifyOriginalText Flag determining if the original text should be modified as well. If `false`,
     * only the display text is changed while the original text is untouched.
     * @param restart Whether or not this label should restart. Defaults to false.
     * @see .restart
     */
    internal fun setText(newText: CharSequence, modifyOriginalText: Boolean, restart: Boolean) {
        super.setText(newText)
        if (modifyOriginalText && originalText != null){
            saveOriginalText()
        }
        if (restart) {
            this.restart()
        }
        if (hasEnded()) {
            this.skipToTheEnd(true, ignoreEffects = false)
        }
    }

    /**
     * Copies the content of [.getText] to the [StringBuilder] containing the original text with all
     * tokens unchanged.
     */
    private fun saveOriginalText() {
        originalText.setLength(0)
        originalText.insert(0, getText())
        originalText.trimToSize()
    }

    /**
     * Restores the original text with all tokens unchanged to this label. Make sure to call [.parseTokens] to
     * parse the tokens again.
     */
    private fun restoreOriginalText() {
        super.setText(originalText)
        this.parsed = false
    }

    /** Adds a [TypingListener] to this label.  */
    fun addTypingListener(listener: TypingListener?) {
        typingListeners.add(listener)
    }

    /** Clears all [TypingListener]s associated with this label.  */
    fun clearTypingListeners() {
        typingListeners.clear()
    }

    /** Parses all tokens of this label. Use this after setting the text and any variables that should be replaced.  */
    fun parseTokens() {
        this.setText(this.defaultToken + getText(), false, restart = false)
        parseTokens(this)
        parsed = true
    }

    /**
     * Skips the char progression to the end, showing the entire label. Useful for when users don't want to wait for too
     * long.
     * @param ignoreEvents If `true`, skipped events won't be reported to the listener.
     * @param ignoreEffects If `true`, all text effects will be instantly cancelled.
     */
    /**
     * Skips the char progression to the end, showing the entire label. Useful for when users don't want to wait for too
     * long.
     * @param ignoreEvents If `true`, skipped events won't be reported to the listener.
     */
    /**
     * Skips the char progression to the end, showing the entire label. Useful for when users don't want to wait for too
     * long. Ignores all subsequent events by default.
     */
    @JvmOverloads
    fun skipToTheEnd(ignoreEvents: Boolean = true, ignoreEffects: Boolean = false) {
        this.isSkipping = true
        ignoringEvents = ignoreEvents
        ignoringEffects = ignoreEffects
    }

    /**
     * Cancels calls to [.skipToTheEnd]. Useful if you need to restore the label's normal behavior at some event
     * after skipping.
     */
    fun cancelSkipping() {
        if (this.isSkipping) {
            this.isSkipping = false
            ignoringEvents = false
            ignoringEffects = false
        }
    }

    /** Pauses this label's character progression.  */
    fun pause() {
        this.isPaused = true
    }

    /** Resumes this label's character progression.  */
    fun resume() {
        this.isPaused = false
    }

    /** Returns whether or not this label's char progression has ended.  */
    fun hasEnded(): Boolean {
        return ended
    }

    /**
     * Restarts this label with the given text and starts the char progression right away. All tokens are automatically
     * parsed.
     */
    /**
     * Restarts this label with the original text and starts the char progression right away. All tokens are
     * automatically parsed.
     */

    fun restart(newText: CharSequence = this.originalText) {
        Pools.freeAll(glyphCache)
        glyphCache.clear()
        glyphRunCapacities.clear()
        offsetCache.clear()
        layoutLineBreaks.clear()
        activeEffects.clear()

        // Reset state
        textSpeed = TypingConfig.DEFAULT_SPEED_PER_CHAR
        charCooldown = textSpeed
        rawCharIndex = -2
        glyphCharIndex = -1
        glyphCharCompensation = 0
        cachedGlyphCharIndex = -1
        lastLayoutX = 0f
        lastLayoutY = 0f
        parsed = false
        this.isPaused = false
        ended = false
        this.isSkipping = false
        ignoringEvents = false
        ignoringEffects = false

        // Set new text
        this.setText(newText, modifyOriginalText = true, restart = false)
        invalidate()

        // Parse tokens
        tokenEntries.clear()
        parseTokens()
    }

    /** Returns an [ObjectMap] with all the variable names and their respective replacement values.  */
    fun getVariables(): ObjectMap<String?, String?> {
        return variables
    }

    /** Registers a variable and its respective replacement value to this label.  */
    fun setVariable(`var`: String, value: String?) {
        variables.put(`var`.uppercase(Locale.getDefault()), value)
    }

    /** Registers a set of variables and their respective replacement values to this label.  */
    fun setVariables(variableMap: ObjectMap<String?, String?>) {
        this.variables.clear()
        for (entry in variableMap.entries()) {
            this.variables.put(entry.key!!.uppercase(Locale.getDefault()), entry.value)
        }
    }

    /** Registers a set of variables and their respective replacement values to this label.  */
    fun setVariables(variableMap: MutableMap<String?, String?>) {
        this.variables.clear()
        for (entry in variableMap.entries) {
            this.variables.put(entry.key!!.uppercase(Locale.getDefault()), entry.value)
        }
    }

    /** Removes all variables from this label.  */
    fun clearVariables() {
        this.variables.clear()
    }

    override fun act(delta: Float) {
        super.act(delta)

        // Force token parsing
        if (!parsed) {
            parseTokens()
        }

        // Update cooldown and process char progression
        if (this.isSkipping || (!ended && !this.isPaused)) {
            if (this.isSkipping || (delta.let { charCooldown -= it; charCooldown }) < 0.0f) {
                processCharProgression()
            }
        }

        // Restore glyph offsets
        if (activeEffects.size > 0) {
            for (i in 0..<glyphCache.size) {
                val glyph = glyphCache.get(i)
                glyph.xoffset = offsetCache.get(i * 2)
                glyph.yoffset = offsetCache.get(i * 2 + 1)
            }
        }

        // Apply effects
        if (!ignoringEffects) {
            for (i in activeEffects.size - 1 downTo 0) {
                val effect = activeEffects.get(i)
                effect.update(delta)
                val start = effect.indexStart
                val end = if (effect.indexEnd >= 0) effect.indexEnd else glyphCharIndex

                // If effect is finished, remove it
                if (effect.isFinished) {
                    activeEffects.remove(i)
                    continue
                }

                // Apply effect to glyph
                var j = max(0, start)
                while (j <= glyphCharIndex && j <= end && j < glyphCache.size) {
                    val glyph = glyphCache.get(j)
                    effect.apply(glyph, j, delta)
                    j++
                }
            }
        }
    }

    /** Proccess char progression according to current cooldown and process all tokens in the current index.  */
    private fun processCharProgression() {
        // Keep a counter of how many chars we're processing in this tick.
        var charCounter = 0

        // Process chars while there's room for it
        while (this.isSkipping || charCooldown < 0.0f) {
            // Apply compensation to glyph index, if any
            if (glyphCharCompensation != 0) {
                if (glyphCharCompensation > 0) {
                    glyphCharIndex++
                    glyphCharCompensation--
                } else {
                    glyphCharIndex--
                    glyphCharCompensation++
                }

                // Increment cooldown and wait for it
                charCooldown += textSpeed
                continue
            }

            // Increase raw char index
            rawCharIndex++

            // Get next character and calculate cooldown increment
            val safeIndex = Mathf.clamp(rawCharIndex, 0, getText().length - 1)
            var primitiveChar = '\u0000' // Null character by default
            if (!getText().isEmpty()) {
                primitiveChar = getText().get(safeIndex)
                val intervalMultiplier = TypingConfig.INTERVAL_MULTIPLIERS_BY_CHAR.get(primitiveChar, 1f)
                charCooldown += textSpeed * intervalMultiplier
            }

            // If char progression is finished, or if text is empty, notify listener and abort routine
            val textLen = getText().length
            if (textLen == 0 || rawCharIndex >= textLen) {
                if (!ended) {
                    ended = true
                    this.isSkipping = false
                    for (listener in this.typingListeners) {
                        listener.end()
                    }
                }
                return
            }

            // Detect layout line breaks
            var isLayoutLineBreak = false
            if (layoutLineBreaks.contains(glyphCharIndex)) {
                layoutLineBreaks.removeValue(glyphCharIndex)
                isLayoutLineBreak = true
            }

            // Increase glyph char index for all characters, except new lines.
            if (rawCharIndex >= 0 && primitiveChar != '\n' && primitiveChar != '\r' && !isLayoutLineBreak) glyphCharIndex++

            // Process tokens according to the current index
            while (tokenEntries.size > 0 && tokenEntries.peek().index == rawCharIndex) {
                val entry = tokenEntries.pop()
                val token = entry.token
                // Process tokens
                when (val category = entry.category) {
                    TokenCategory.SPEED -> {
                        textSpeed = entry.floatValue
                        continue
                    }
                    TokenCategory.WAIT -> {
                        glyphCharIndex--
                        glyphCharCompensation++
                        charCooldown += entry.floatValue
                        continue
                    }
                    TokenCategory.SKIP -> {
                        entry.stringValue?.let {
                            rawCharIndex += it.length
                        }
                        continue
                    }
                    TokenCategory.EVENT -> {
                        if (!ignoringEvents) {
                            for (listener in this.typingListeners) {
                                listener.event(entry.stringValue)
                            }
                        }
                        continue
                    }
                    TokenCategory.EFFECT_START, TokenCategory.EFFECT_END -> {
                        // Get effect class
                        val isStart = category == TokenCategory.EFFECT_START
                        val effectClass: Class<out Effect?> = (if (isStart) TypingConfig.EFFECT_START_TOKENS.get(token) else TypingConfig.EFFECT_END_TOKENS.get(token))!!

                        // End all effects of the same type
                        var i = 0
                        while (i < activeEffects.size) {
                            val effect = activeEffects.get(i)
                            if (effect.indexEnd < 0) {
                                if (effectClass.isAssignableFrom(effect.javaClass)) {
                                    effect.indexEnd = glyphCharIndex - 1
                                }
                            }
                            i++
                        }

                        // Create new effect if necessary
                        if (isStart) {
                            entry.effect!!.indexStart = glyphCharIndex
                            activeEffects.add(entry.effect)
                        }
                    }
                    else -> {}
                }
            }

            // Notify listener about char progression
            val nextIndex = Mathf.clamp(rawCharIndex, 0, getText().length - 1)
            val nextChar = if (nextIndex == 0) null else getText().get(nextIndex)
            if (nextChar != null) {
                for (listener in this.typingListeners) {
                    listener.onChar(nextChar)
                }
            }

            // Increment char counter
            charCounter++

            // Break loop if this was our first glyph to prevent glyph issues.
            if (glyphCharIndex == -1) {
                charCooldown = textSpeed
                break
            }

            // Break loop if enough chars were processed
            charCounter++
            val charLimit = TypingConfig.CHAR_LIMIT_PER_FRAME
            if (!this.isSkipping && charLimit > 0 && charCounter > charLimit) {
                charCooldown = max(charCooldown, textSpeed)
                break
            }
        }
    }

    override fun remove(): Boolean {
        Pools.freeAll(glyphCache)
        glyphCache.clear()
        return super.remove()
    }

    override fun setEllipsis(ellipsis: String?) {
        // Mimics superclass but keeps an accessible reference
        super.setEllipsis(ellipsis)
        this.ellipsiss = ellipsis
    }

    override fun setEllipsis(ellipsis: Boolean) {
        // Mimics superclass but keeps an accessible reference
        super.setEllipsis(ellipsis)
        if (ellipsis) this.ellipsiss = "..."
        else this.ellipsiss = null
    }

    override fun setWrap(wrap: Boolean) {
        // Mimics superclass but keeps an accessible reference
        super.setWrap(wrap)
        this.wraps = wrap
    }

    override fun setFontScale(fontScale: Float) {
        super.setFontScale(fontScale)
        this.fontScaleChangeds = true
    }

    override fun setFontScale(fontScaleX: Float, fontScaleY: Float) {
        super.setFontScale(fontScaleX, fontScaleY)
        this.fontScaleChangeds = true
    }

    override fun setFontScaleX(fontScaleX: Float) {
        super.setFontScaleX(fontScaleX)
        this.fontScaleChangeds = true
    }

    override fun setFontScaleY(fontScaleY: Float) {
        super.setFontScaleY(fontScaleY)
        this.fontScaleChangeds = true
    }

    override fun layout() {
        // --- SUPERCLASS IMPLEMENTATION ---
        val cache = fontCache
        val text = getText()
        val layout = super.getGlyphLayout()
        val lineAlign = getLineAlign()
        val labelAlign = getLabelAlign()
        val style = getStyle()

        val font = cache.font
        val oldScaleX = font.scaleX
        val oldScaleY = font.scaleY
        if (fontScaleChangeds) font.getData().setScale(getFontScaleX(), getFontScaleY())

        val wrap = this.wraps && ellipsiss == null
        if (wrap) {
            val prefHeight = getPrefHeight()
            if (prefHeight != lastPrefHeights) {
                lastPrefHeights = prefHeight
                invalidateHierarchy()
            }
        }

        var width = getWidth()
        var height = getHeight()
        val background = style.background
        var x = 0f
        var y = 0f
        if (background != null) {
            x = background.leftWidth
            y = background.bottomHeight
            width -= background.leftWidth + background.rightWidth
            height -= background.bottomHeight + background.topHeight
        }

        val textWidth: Float
        val textHeight: Float
        if (wrap || text.indexOf("\n") != -1) {
            // If the text can span multiple lines, determine the text's actual size so it can be aligned within the label.
            layout.setText(font, text, 0, text.length, Color.white, width, lineAlign, wrap, ellipsiss)
            textWidth = layout.width
            textHeight = layout.height

            if ((labelAlign and Align.left) == 0) {
                x += if ((labelAlign and Align.right) != 0) width - textWidth
                else (width - textWidth) / 2
            }
        } else {
            textWidth = width
            textHeight = font.getData().capHeight
        }

        if ((labelAlign and Align.top) != 0) {
            y += if (cache.font.isFlipped) 0f else height - textHeight
            y += style.font.descent
        } else if ((labelAlign and Align.bottom) != 0) {
            y += if (cache.font.isFlipped) height - textHeight else 0f
            y -= style.font.descent
        } else {
            y += (height - textHeight) / 2
        }
        if (!cache.font.isFlipped) y += textHeight

        layout.setText(font, text, 0, text.length, Color.white, textWidth, lineAlign, wrap, ellipsiss)
        cache.setText(layout, x, y)

        if (fontScaleChangeds) font.getData().setScale(oldScaleX, oldScaleY)

        // --- END OF SUPERCLASS IMPLEMENTATION ---

        // Store coordinates passed to BitmapFontCache
        lastLayoutX = x
        lastLayoutY = y

        // Perform cache layout operation, where the magic happens
        Pools.freeAll(glyphCache)
        glyphCache.clear()
        layoutCache()
    }

    /**
     * Reallocate glyph clones according to the updated [GlyphLayout]. This should only be called when the text or
     * the layout changes.
     */
    private fun layoutCache() {
        val cache = fontCache
        val layout = super.getGlyphLayout()
        val runs = layout.runs
        var colorStack: Field?
        try {
            colorStack = GlyphLayout::class.java.getDeclaredField("colorStack")
        } catch (e: NoSuchFieldException) {
            throw RuntimeException(e)
        }
        colorStack.setAccessible(true)
        var colors: Seq<Color>
        try {
            colors = colorStack.get(layout) as Seq<Color>
        } catch (e: IllegalAccessException) {
            throw RuntimeException(e)
        }

        // Reset layout line breaks
        layoutLineBreaks.clear()

        // Store GlyphRun sizes and count how many glyphs we have
        var glyphCount = 0
        glyphRunCapacities.setSize(runs.size)
        for (i in 0..<runs.size) {
            val glyphs = runs.get(i).glyphs
            glyphRunCapacities.set(i, glyphs.size)
            glyphCount += glyphs.size
        }

        // Make sure our cache array can hold all glyphs
        if (glyphCache.size < glyphCount) {
            glyphCache.setSize(glyphCount)
            offsetCache.setSize(glyphCount * 2)
        }

        // Clone original glyphs with independent instances
        var index = -1
        var lastY = 0f

        var colorIndex = 1
        var currentColor = if (colors.size < 2) -0x1 else colors.get(1)!!.argb8888()
        var colorChange = if (colors.size < 4) glyphCount else colors.get(2)!!.argb8888()

        for (i in 0..<runs.size) {
            val run = runs.get(i)
            val glyphs = run.glyphs
            for (j in 0..<glyphs.size) {
                // Detect and store layout line breaks

                if (!Mathf.equal(run.y, lastY)) {
                    lastY = run.y
                    layoutLineBreaks.add(index)
                }

                // Increment index
                index++
                if (index >= colorChange && colorIndex + 2 < colors.size) {
                    currentColor = colors.get(2.let { colorIndex += it; colorIndex })!!.argb8888()
                    colorChange = if (colors.size <= colorIndex + 1) glyphCount else colors.get(colorIndex + 1)!!.argb8888()
                }

                // Get original glyph
                val original = glyphs.get(j)

                // Get clone glyph
                var clone: TypingGlyph? = null
                if (index < glyphCache.size) {
                    clone = glyphCache.get(index)
                }
                if (clone == null) {
                    clone = Pools.obtain(TypingGlyph::class.java, ::TypingGlyph)
                    clone!!
                    glyphCache.set(index, clone)
                }
                clone.set(original)
                clone.width = (clone.width * getFontScaleX()).toInt()
                clone.height = (clone.height * getFontScaleY()).toInt()
                clone.xoffset = (clone.xoffset * getFontScaleX()).toInt()
                clone.yoffset = (clone.yoffset * getFontScaleY()).toInt()
                clone.runColor = currentColor

                // Store offset data
                offsetCache.set(index * 2, clone.xoffset)
                offsetCache.set(index * 2 + 1, clone.yoffset)

                // Replace glyph in original array
                glyphs.set(j, clone)
            }
        }

        // Remove exceeding glyphs from original array
        var glyphCountdown = glyphCharIndex
        for (i in 0..<runs.size) {
            val glyphs = runs.get(i).glyphs
            if (glyphs.size < glyphCountdown) {
                glyphCountdown -= glyphs.size
                continue
            }

            for (j in 0..<glyphs.size) {
                if (glyphCountdown < 0) {
                    glyphs.removeRange(j, glyphs.size - 1)
                    break
                }
                glyphCountdown--
            }
        }

        // Pass new layout with custom glyphs to BitmapFontCache
        cache.setText(layout, lastLayoutX, lastLayoutY)
    }

    /** 随着字符索引的推进,将缓存的字形添加到活跃的FontCache中。  */
    private fun addMissingGlyphs() {
        // Add additional glyphs to layout array, if any
        var glyphLeft = glyphCharIndex - cachedGlyphCharIndex
        if (glyphLeft < 1) return

        // Get runs
        val layout = super.getGlyphLayout()
        val runs = layout.runs

        // Iterate through GlyphRuns to find the next glyph spot
        var glyphCount = 0
        for (runIndex in 0..<glyphRunCapacities.size) {
            val runCapacity = glyphRunCapacities.get(runIndex)
            if ((glyphCount + runCapacity) < cachedGlyphCharIndex) {
                glyphCount += runCapacity
                continue
            }

            // Get run and increase glyphCount up to its current size
            val glyphs = runs.get(runIndex).glyphs
            glyphCount += glyphs.size

            // Next glyphs go here
            while (glyphLeft > 0) {
                // Skip run if this one is full

                val runSize = glyphs.size
                if (runCapacity == runSize) {
                    break
                }

                // Put new glyph to this run
                cachedGlyphCharIndex++
                val glyph = glyphCache.get(cachedGlyphCharIndex)
                glyphs.add(glyph)

                // Cache glyph's vertex index
                glyph.internalIndex = glyphCount

                // Advance glyph count
                glyphCount++
                glyphLeft--
            }
        }
    }

    override fun draw() {
        super.validate()
        addMissingGlyphs()
        // Update cache with new glyphs
        fontCache.setText(glyphLayout, lastLayoutX, lastLayoutY)
        // Tint glyphs
        for (glyph in glyphCache) {
            if (glyph.internalIndex >= 0) {
                if (glyph.color==null)glyph.color= Color(Color.white)
                fontCache.setColors(glyph.color, glyph.internalIndex, glyph.internalIndex + 1)
            }
        }
        super.draw()
    }
}
