package ice.library.scene.element.typinglabel

internal enum class InternalToken(val category: TokenCategory) {
    // @formatter:off
    // Public

    WAIT( TokenCategory.WAIT),
    SPEED( TokenCategory.SPEED),
    SLOWER( TokenCategory.SPEED),
    SLOW(TokenCategory.SPEED),
    NORMAL( TokenCategory.SPEED),
    FAST( TokenCategory.SPEED),
    FASTER( TokenCategory.SPEED),
    COLOR( TokenCategory.COLOR),
    CLEARCOLOR( TokenCategory.COLOR),
    ENDCOLOR(TokenCategory.COLOR),
    VAR( TokenCategory.VARIABLE),
    IF( TokenCategory.IF),
    EVENT( TokenCategory.EVENT),
    RESET( TokenCategory.RESET),
    SKIP(TokenCategory.SKIP); // @formatter:on

    companion object {
        fun fromName(name: String?): InternalToken? {
            name?.let {
                for (token in entries) {
                    if (name.equals(token.name, ignoreCase = true)) {
                        return token
                    }
                }
            }
            return null
        }
    }

}
