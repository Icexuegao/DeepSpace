package ice.ui.dialog.research.node

import arc.Events
import arc.scene.Element
import arc.scene.ui.layout.Scl
import arc.scene.ui.layout.Table
import ice.ui.dialog.research.ResearchDialog

open class Node(val x: Float, val y: Float, val element: Element) {
    companion object {
        val view = ResearchDialog.view
    }

    init {
        element.tapped {
            ResearchDialog.selectANode = this@Node
            Events.fire(ResearchDialog.SelectANodeEvent())
        }
        view.addChild(element)
        element.update {
            update()
        }
    }

    open fun show(table: Table) {}
    open fun update() {
        element.setPosition(view.panX + Scl.scl(x), view.panY + Scl.scl(y))
    }
}