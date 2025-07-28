package ice.ui.dialog.research.node

import arc.func.Prov
import arc.scene.Element
import arc.scene.ui.layout.Scl
import ice.ui.dialog.research.ResearchDialog

open class Node(val x: Float, val y: Float, ep: Prov<Element>) {
    companion object {
        val view = ResearchDialog.view
        val nodeSize: Float = Scl.scl(60f)
    }
    val element: Element = ep.get()

    init {
        view.addChild(element)
        element.update {
            update()
        }
    }

    open fun update() {
        element.setPosition(view.panX + x, view.panY + y)
    }
}