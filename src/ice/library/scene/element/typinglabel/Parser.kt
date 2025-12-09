package ice.library.scene.element.typinglabel

import arc.graphics.Color
import arc.graphics.Colors
import arc.math.Mathf
import arc.struct.Seq
import regexodus.Pattern
import regexodus.REFlags
import java.lang.reflect.InvocationTargetException
import java.util.*

/** Utility class to parse tokens from a [TLabel].  */
internal object Parser {
    private var CURRENT_DELIMITER: TokenDelimiter = TypingConfig.TOKEN_DELIMITER
    private var PATTERN_TOKEN_STRIP: Pattern? = compileTokenPattern()
    private val PATTERN_MARKUP_STRIP: Pattern = Pattern.compile("(\\[{2})|(\\[#?\\w*(\\[|\\])?)")

    private val PATTERN_COLOR_HEX_NO_HASH: Pattern = Pattern.compile("[A-F0-9]{6}")

    private val BOOLEAN_TRUE = arrayOf<String?>("true", "yes", "t", "y", "on", "1")
    private const val INDEX_TOKEN = 1
    private const val INDEX_PARAM = 2

    private var RESET_REPLACEMENT: String? = null

    /** Parses all tokens from the given [TLabel].  */
    fun parseTokens(label: TLabel) {
        // Detect if token delimiter has changed
        val hasDelimiterChanged = CURRENT_DELIMITER != TypingConfig.TOKEN_DELIMITER
        if (hasDelimiterChanged) {
            CURRENT_DELIMITER = TypingConfig.TOKEN_DELIMITER
        }

        // Compile patterns if necessary
        if (PATTERN_TOKEN_STRIP == null || TypingConfig.dirtyEffectMaps || hasDelimiterChanged) {
            PATTERN_TOKEN_STRIP = compileTokenPattern()
        }
        if (RESET_REPLACEMENT == null || TypingConfig.dirtyEffectMaps || hasDelimiterChanged) {
            RESET_REPLACEMENT = resetReplacement
        }

        // Adjust and check markup color
        if (label.forceMarkupColor) label.fontCache.font.getData().markupEnabled = true

        // Remove any previous entries
        label.tokenEntries.clear()

        // Parse all tokens with text replacements, namely color and var.
        parseReplacements(label)

        // Parse all regular tokens and properly register them
        parseRegularTokens(label)

        // Parse color markups and register SKIP tokens
        parseColorMarkups(label)

        // Sort token entries
        label.tokenEntries.sort()
        label.tokenEntries.reverse()
    }

    /** Parse tokens that only replace text, such as colors and variables.  */
    private fun parseReplacements(label: TLabel) {
        // Get text
        var text: CharSequence? = label.getText()
        val hasMarkup = label.fontCache.font.getData().markupEnabled

        // Create string builder
        // StringBuilder sb = new StringBuilder(text.length());
        val m = PATTERN_TOKEN_STRIP!!.matcher(text)
        var matcherIndexOffset = 0

        // Iterate through matches
        while (true) {
            // Reset StringBuilder and matcher
            //    sb.setLength(0);
            m.setTarget(text)
            m.setPosition(matcherIndexOffset)

            // Make sure there's at least one regex match
            if (!m.find()) break

            // Get token and parameter
            val internalToken = InternalToken.fromName(m.group(INDEX_TOKEN))
            val param = m.group(INDEX_PARAM)

            // If token couldn't be parsed, move one index forward to continue the search
            if (internalToken == null) {
                matcherIndexOffset++
                continue
            }

            // Process tokens and handle replacement
            var replacement: String? = ""
            when (internalToken) {
                InternalToken.COLOR -> if (hasMarkup) replacement = stringToColorMarkup(param)
                InternalToken.ENDCOLOR, InternalToken.CLEARCOLOR -> if (hasMarkup) replacement = "[#" + label.clearColor.toString() + "]"
                InternalToken.VAR -> {
                    replacement = null

                    // Try to replace variable through listeners.
                    for (listener in label.typingListeners) {
                        replacement = listener.replaceVariable(param)
                        if (replacement != null) break
                    }

                    // If replacement is null, get value from maps.
                    if (replacement == null) {
                        replacement = label.variables.get(param.uppercase(Locale.getDefault()))
                    }

                    // If replacement is still null, get value from global scope
                    if (replacement == null) {
                        replacement = TypingConfig.GLOBAL_VARS.get(param.uppercase(Locale.getDefault()))
                    }

                    // Make sure we're not inserting "null" to the text.
                    if (replacement == null) replacement = param.uppercase(Locale.getDefault())
                }
                InternalToken.IF -> {
                    // Process token
                    replacement = processIfToken(label, param)

                    // Make sure we're not inserting "null" to the text.
                    if (replacement == null) replacement = param.uppercase(Locale.getDefault())
                }
                InternalToken.RESET -> replacement = RESET_REPLACEMENT + label.defaultToken
                else -> {
                    // We don't want to process this token now. Move one index forward to continue the search
                    matcherIndexOffset++
                    continue
                }
            }

            // Update text with replacement
            m.setPosition(m.start())
            text = m.replaceFirst(replacement)
        }

        // Set new text
        label.setText(text!!)
    }

    private fun processIfToken(label: TLabel, paramsString: String?): String? {
        // Split params
        val toTypedArray = paramsString?.split(";".toRegex())?.dropLastWhile { it.isEmpty() }?.toTypedArray<String?>()
        val params = toTypedArray ?: arrayOfNulls(0)
        val variable = if (params.isNotEmpty()) params[0] else null

        // Ensure our params are valid
        if (params.size <= 1 || variable == null) {
            return null
        }

        /*
            Get variable's value
         */
        var variableValue: String? = null

        // Try to get value through listener.
        for (listener in label.typingListeners) {
            variableValue = listener.replaceVariable(variable)
            if (variableValue != null) break
        }

        // If value is null, get it from maps.
        if (variableValue == null) {
            variableValue = label.variables.get(variable.uppercase(Locale.getDefault()))
        }

        // If value is still null, get it from global scope
        if (variableValue == null) {
            variableValue = TypingConfig.GLOBAL_VARS.get(variable.uppercase(Locale.getDefault()))
        }

        // Ensure variable is never null
        if (variableValue == null) {
            variableValue = ""
        }

        // Iterate through params and try to find a match
        var defaultValue: String? = null
        var i = 1
        val n = params.size
        while (i < n) {
            val subParams = params[i]!!.split("=".toRegex(), limit = 2).toTypedArray()
            val key = subParams[0]
            val value: String = subParams[subParams.size - 1]
            val isKeyValid = subParams.size > 1 && !key.isEmpty()

            // If key isn't valid, it must be a default value. Store it and carry on
            if (!isKeyValid) {
                defaultValue = value
                break
            }

            // Compare variable's value with key
            if (variableValue.equals(key, ignoreCase = true)) {
                return value
            }
            i++
        }

        // Try to return any default values captured during the iteration
        if (defaultValue != null) {
            return defaultValue
        }

        // If we got this far, no values matched our variable.
        // Return the variable itself, which might be useful for debugging.
        return variable
    }

    /** Parses regular tokens that don't need replacement and register their indexes in the [TLabel].  */
    private fun parseRegularTokens(label: TLabel) {
        // Get text
        var text: CharSequence? = label.getText()

        // 创建匹配器和字符串构建器
        val m = PATTERN_TOKEN_STRIP!!.matcher(text)
        //StringBuilder sb = new StringBuilder(text.length());
        var matcherIndexOffset = 0

        //迭代匹配
        while (true) {
            // 复位匹配器和字符串构建器
            m.setTarget(text)
            //  sb.setLength(0);
            m.setPosition(matcherIndexOffset)

            // Make sure there's at least one regex match
            if (!m.find()) break

            // Get token name and category
            val tokenName = m.group(INDEX_TOKEN).uppercase(Locale.getDefault())
            var tokenCategory: TokenCategory? = null
            val tmpToken = InternalToken.fromName(tokenName)
            if (tmpToken == null) {
                if (TypingConfig.EFFECT_START_TOKENS.containsKey(tokenName)) {
                    tokenCategory = TokenCategory.EFFECT_START
                } else if (TypingConfig.EFFECT_END_TOKENS.containsKey(tokenName)) {
                    tokenCategory = TokenCategory.EFFECT_END
                }
            } else {
                tokenCategory = tmpToken.category
            }

            // Get token, param and index of where the token begins
            val groupCount = m.groupCount()
            val paramsString = if (groupCount == INDEX_PARAM) m.group(INDEX_PARAM) else null
            val toTypedArray = paramsString?.split(";".toRegex())?.dropLastWhile { it.isEmpty() }?.toTypedArray<String?>()
            val params: Array<String?> = toTypedArray ?: arrayOfNulls(0)
            val firstParam = if (params.isNotEmpty()) params[0] else null
            val index = m.start(0)
            var indexOffset = 0

            // If token couldn't be parsed, move one index forward to continue the search
            if (tokenCategory == null) {
                matcherIndexOffset++
                continue
            }

            // Process tokens
            var floatValue = 0f
            var stringValue: String? = null
            var effect: Effect? = null

            when (tokenCategory) {
                TokenCategory.WAIT -> {
                    floatValue = stringToFloat(firstParam, TypingConfig.DEFAULT_WAIT_VALUE)
                }
                TokenCategory.EVENT -> {
                    stringValue = paramsString
                    indexOffset = -1
                }
                TokenCategory.SPEED -> {
                    when (tokenName) {
                        "SPEED" -> {
                            val minModifier = TypingConfig.MIN_SPEED_MODIFIER
                            val maxModifier = TypingConfig.MAX_SPEED_MODIFIER
                            val modifier = Mathf.clamp(stringToFloat(firstParam, 1f), minModifier, maxModifier)
                            floatValue = TypingConfig.DEFAULT_SPEED_PER_CHAR / modifier
                        }
                        "SLOWER" -> floatValue = TypingConfig.DEFAULT_SPEED_PER_CHAR / 0.500f
                        "SLOW" -> floatValue = TypingConfig.DEFAULT_SPEED_PER_CHAR / 0.667f
                        "NORMAL" -> floatValue = TypingConfig.DEFAULT_SPEED_PER_CHAR
                        "FAST" -> floatValue = TypingConfig.DEFAULT_SPEED_PER_CHAR / 2.000f
                        "FASTER" -> floatValue = TypingConfig.DEFAULT_SPEED_PER_CHAR / 4.000f
                    }
                }
                TokenCategory.EFFECT_START -> {
                    val clazz = TypingConfig.EFFECT_START_TOKENS.get(tokenName.uppercase(Locale.getDefault()))
                    try {
                        if (clazz != null) {
                            val constructor = clazz.getConstructors()[0]
                            val constructorParamCount = constructor.parameterTypes.size
                            effect = if (constructorParamCount >= 2) {
                                constructor.newInstance(label, params) as Effect
                            } else {
                                constructor.newInstance(label) as Effect
                            }
                        }
                    } catch (e: InstantiationException) {
                        val message = "Failed to initialize $tokenName effect token. Make sure the associated class ($clazz) has only one constructor with TypingLabel as first parameter and optionally String[] as second."
                        throw IllegalStateException(message, e)
                    } catch (e: IllegalAccessException) {
                        val message = "Failed to initialize $tokenName effect token. Make sure the associated class ($clazz) has only one constructor with TypingLabel as first parameter and optionally String[] as second."
                        throw IllegalStateException(message, e)
                    } catch (e: InvocationTargetException) {
                        val message = "Failed to initialize $tokenName effect token. Make sure the associated class ($clazz) has only one constructor with TypingLabel as first parameter and optionally String[] as second."
                        throw IllegalStateException(message, e)
                    }
                }
                TokenCategory.EFFECT_END -> {}
                else -> {}
            }

            // Register token
            val entry = TokenEntry(tokenName, tokenCategory, index + indexOffset, floatValue, stringValue)
            entry.effect = effect
            label.tokenEntries.add(entry)

            // Set new text without tokens
            m.setPosition(0)
            text = m.replaceFirst("")
        }

        // Update label text
        label.setText(text!!, modifyOriginalText = true, restart = false)
    }

    /** Parse color markup tags and register SKIP tokens.  */
    private fun parseColorMarkups(label: TLabel) {
        // Get text
        val text: CharSequence? = label.getText()

        // Iterate through matches and register skip tokens
        val m = PATTERN_MARKUP_STRIP.matcher(text)
        while (m.find()) {
            val tag = m.group(0)
            val index = m.start(0)
            label.tokenEntries.add(TokenEntry("SKIP", TokenCategory.SKIP, index, 0f, tag))
        }
    }

    /** Returns a float value parsed from the given String, or the default value if the string couldn't be parsed.  */
    fun stringToFloat(str: String?, defaultValue: Float): Float {
        if (str != null) {
            try {
                return str.toFloat()
            } catch (e: Exception) {
                throw RuntimeException(e)
            }
        }
        return defaultValue
    }

    /** Returns a boolean value parsed from the given String, or the default value if the string couldn't be parsed.  */
    fun stringToBoolean(str: String?): Boolean {
        if (str != null) {
            for (booleanTrue in BOOLEAN_TRUE) {
                if (booleanTrue.equals(str, ignoreCase = true)) {
                    return true
                }
            }
        }
        return false
    }

    /** Parses a color from the given string. Returns null if the color couldn't be parsed.  */
    fun stringToColor(str: String?): Color? {
        if (str != null) {
            // Try to parse named color

            val namedColor = Colors.get(str.uppercase(Locale.getDefault()))
            if (namedColor != null) {
                return Color(namedColor)
            }

            // Try to parse hex
            if (str.length >= 6) {
                try {
                    return Color.valueOf(str)
                } catch (ignored: NumberFormatException) {
                    throw Exception("无效颜色: $str", ignored)
                }
            }
        }

        return null
    }

    /** Encloses the given string in brackets to work as a regular color markup tag.  */
    private fun stringToColorMarkup(str: String?): String {
        var str = str
        if (str != null) {
            // Upper case
            str = str.uppercase(Locale.getDefault())

            // If color isn't registered by name, try to parse it as a hex code.
            val namedColor = Colors.get(str)
            if (namedColor == null) {
                val isHexWithoutHashChar = str.length >= 6 && PATTERN_COLOR_HEX_NO_HASH.matches(str)
                if (isHexWithoutHashChar) {
                    str = "#$str"
                }
            }
        }

        // Return color code
        return "[$str]"
    }

    /**
     * Returns a compiled [Pattern] that groups the token name in the first group and the params in an optional second one. Case-     * insensitive .
     */
    private fun compileTokenPattern(): Pattern {
        val sb = StringBuilder()
        sb.append("\\").append(CURRENT_DELIMITER.open).append("(")
        val tokens = Seq<String?>()
        TypingConfig.EFFECT_START_TOKENS.keys().toSeq(tokens)
        TypingConfig.EFFECT_END_TOKENS.keys().toSeq(tokens)
        for (token in InternalToken.entries) {
            tokens.add(token.name)
        }
        for (i in 0..<tokens.size) {
            sb.append(tokens.get(i))
            if ((i + 1) < tokens.size) sb.append('|')
        }
        sb.append(")(?:=([;:?=^_ #-'*-.\\.\\w]+))?\\").append(CURRENT_DELIMITER.close)
        return Pattern.compile(sb.toString(), REFlags.IGNORE_CASE)
    }

    private val resetReplacement: String
        /** Returns the replacement string intended to be used on {RESET} tokens.  */
        get() {
            val tokens = Seq<String?>()
            TypingConfig.EFFECT_END_TOKENS.keys().toSeq(tokens)
            tokens.add("CLEARCOLOR")
            tokens.add("NORMAL")

            val sb = StringBuilder()
            for (token in tokens) {
                sb.append(CURRENT_DELIMITER.open).append(token).append(CURRENT_DELIMITER.close)
            }
            return sb.toString()
        }
}
