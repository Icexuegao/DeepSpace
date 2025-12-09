package ice.library.scene.element.typinglabel

/**
 * Enum that lists all supported delimiters for tokens.
 */
enum class TokenDelimiter(val open: Char, val close: Char) {
    /** `{TOKEN}`  */
    CURLY_BRACKETS('{', '}'),

    /** [ TOKEN ]  */
    BRACKETS('[', ']'),

    /** `(TOKEN)`  */
    PARENTHESES('(', ')')
}
