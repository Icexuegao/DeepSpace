package ice.ui.dialog.research.node

import arc.scene.Element
import arc.scene.ui.layout.Scl
import arc.struct.Seq

open class LinkNode(val name: String, x: Float, y: Float, element: Element) : Node(x, y, element) {
    var parent = Seq<LinkNode>()
    var child = Seq<LinkNode>()
    var nodeSize: Float = Scl.scl(60f)

    init {
        view.links.add(this)
    }

    open fun unlocked(): Boolean {
        return true
    }

    open fun getOffset(): Float {
        return nodeSize / 2
    }

    fun addChild(vararg node: LinkNode): LinkNode {
        node.forEach {
            it.parent.addUnique(this)
        }
        child.add(node)
        return this
    }

    fun setParent(node: LinkNode): LinkNode {
        parent.addUnique(node)
        node.addChild(this)
        return this
    }
}