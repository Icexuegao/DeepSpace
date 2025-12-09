package ice.library.scene.element.typinglabel

/** Container representing a token, parsed parameters and its position in text.  */
class TokenEntry(var token: String, var category: TokenCategory, var index: Int, var floatValue: Float, var stringValue: String?) : Comparable<TokenEntry> {
    var effect: Effect? = null

    override fun compareTo(other: TokenEntry): Int {
        return index.compareTo(other.index)
    }
}
