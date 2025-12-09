package ice.library.scene.element.typinglabel

import arc.graphics.Color
import arc.graphics.g2d.Font
import arc.util.pooling.Pool.Poolable

/** Extension of [Font.Glyph] with additional data exposed to the user.  */
class TypingGlyph : Font.Glyph(), Poolable {
    /** The color of the run this glyph belongs to, as an ABGR8888 int.  */
    var runColor: Int = -0x1

    /** Internal index associated with this Internal use only. Defaults to -1.  */
    var internalIndex: Int = -1

    /** Color of this If set to null, the run's color will be used. Defaults to null.  */
    var color : Color?=null


    /** Stores original glyph properties.  */
    var originalWidth: Int = 0
    var originalHeight: Int = 0
    var originalXoffset: Int = 0
    var originalYoffset: Int = 0
    fun set(from: Font.Glyph) {
        id = from.id
        srcX = from.srcX
        srcY = from.srcY
        width = from.width
        height = from.height
        u = from.u
        v = from.v
        u2 = from.u2
        v2 = from.v2
        xoffset = from.xoffset
        yoffset = from.yoffset
        xadvance = from.xadvance
        kerning = from.kerning // Keep the same instance, there's no reason to deep clone it
        fixedWidth = from.fixedWidth

        runColor = -0x1
        internalIndex = -1
        color = null
        originalWidth = 0
        originalHeight = 0
        originalXoffset = 0
        originalYoffset = 0
    }

    override fun reset() {
        id = 0
        srcX = 0
        srcY = 0
        width = 0
        height = 0
        u = 0f
        v = 0f
        u2 = 0f
        v2 = 0f
        xoffset = 0
        yoffset = 0
        xadvance = 0
        kerning = null
        fixedWidth = false

        runColor = -0x1
        internalIndex = -1
        color =null

        originalWidth = 0
        originalHeight = 0
        originalXoffset = 0
        originalYoffset = 0
    }
}
