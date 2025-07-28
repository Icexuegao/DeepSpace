package ice.ui.dialog.research.node

import arc.func.Prov
import arc.scene.Element
import arc.struct.Seq

open class LinkNode(val nameId: String, x: Float, y: Float, ep: Prov<Element>) : Node(x, y, ep) {
    var parent: LinkNode? = null
    var child = Seq<LinkNode>()

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
            it.parent = this
        }
        child.add(node)
        return this
    }

    fun setParent(node: LinkNode): LinkNode {
        this.parent = node
        node.addChild(this)
        return this
    }
}