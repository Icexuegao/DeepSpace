package ice.ui.dialog.research

import arc.graphics.g2d.Draw
import arc.graphics.g2d.Lines
import arc.scene.Group
import arc.scene.ui.layout.Scl
import arc.struct.Seq
import arc.util.Align
import ice.graphics.IceColor
import ice.ui.dialog.research.node.LinkNode
import mindustry.graphics.Pal

open class View : Group() {
    var panX: Float = 0f
    var panY: Float = 0f
    var lastZoom: Float = -1f
    val links = Seq<LinkNode>()
    var initializer = false
    override fun layout() {
        super.layout()
        if (!initializer) {
            initializer = true
            panX = width / 2
            panY = height / 2
        }
    }

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

        links.forEach {
            val element = it.element
            val parent1s = it.parent
            it.child

            parent1s.forEach { parent1 ->
                val color = if (parent1.unlocked() && it.unlocked()) {
                    IceColor.b4
                } else if (parent1.unlocked() && !it.unlocked()) {
                    Pal.lightishGray
                } else Pal.remove

                Lines.stroke(Scl.scl(4f), color)
                Draw.alpha(parentAlpha * color.a)
                val parx = parent1.element
                val x1 = parx.x + parent1.getOffset()
                val y1 = parx.y + parent1.getOffset()
                val x2 = element.x + it.getOffset()
                val y2 = element.y + it.getOffset()
                // Lines.curve(x1,y1,x1+20f,y1-20f,x1+40f,y1-40f,x2,y2,20)
                // Lines.curve(x1,y1,x1-20f,y1+20f,x1-40f,y1+40f,x2,y2,20)
                Lines.line(x1, y1, x2,
                    y2)
            }
            /*childs.forEach { value ->
                Lines.stroke(Scl.scl(4f), if (value.unlocked()) IceColor.b4 else Pal.remove)
                Draw.alpha(parentAlpha * color.a)
                Lines.line(element.x + it.getOffset(), element.y + it.getOffset(), value.element.x + value.getOffset(),
                    value.element.y + value.getOffset())
            }*/
        }
        Draw.sort(false)
        Draw.reset()
        super.drawChildren()
    }
}