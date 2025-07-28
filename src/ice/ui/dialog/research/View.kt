package ice.ui.dialog.research

import arc.graphics.g2d.Draw
import arc.graphics.g2d.Lines
import arc.scene.Group
import arc.scene.ui.layout.Scl
import arc.struct.Seq
import arc.util.Align
import ice.ui.dialog.research.node.LinkNode
import ice.library.scene.texs.Colors

class View : Group() {
    var panX: Float = 0f
    var panY: Float = 0f
    var lastZoom: Float = -1f
    val links = Seq<LinkNode>()

    init {
        rebuild()
    }

    fun rebuild() {
        setOrigin(Align.center)
        isTransform = true
    }

    //传递一个应该在此处闪耀的堆栈索引数组
    public override fun drawChildren() {
        Draw.sort(true)
        Lines.stroke(Scl.scl(4f), Colors.b4.cpy().a(parentAlpha))
        links.forEach {
            val element = it.element
            val parent1 = it.parent
            val childs = it.child

            if (parent1 != null) {
                val parx = parent1.element
                Lines.line(parx.x + parent1.getOffset(), parx.y + parent1.getOffset(), element.x + it.getOffset(),
                    element.y + it.getOffset())
            }
            childs.forEach { value ->
                Lines.line(element.x + it.getOffset(), element.y + it.getOffset(), value.element.x + value.getOffset(),
                    value.element.y + value.getOffset())
            }
        }
        Draw.sort(false)
        Draw.reset()
        super.drawChildren()
    }
}