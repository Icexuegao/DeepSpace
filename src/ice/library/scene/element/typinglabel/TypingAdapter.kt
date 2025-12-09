
package ice.library.scene.element.typinglabel

/** Simple listener for label events. You can derive from this and only override what you are interested in.  */
class TypingAdapter : TypingListener {
    override fun event(event: String?) {
    }

    override fun end() {
    }

    override fun replaceVariable(variable: String?): String? {
        return null
    }

    override fun onChar(ch: Char?) {
    }
}
