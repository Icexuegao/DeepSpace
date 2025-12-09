package ice.ui.dialog.research.node

import arc.Events
import arc.scene.Element
import arc.scene.ui.layout.Scl
import arc.scene.ui.layout.Table
import ice.ui.dialog.research.ResearchDialog
import mindustry.gen.Building
import mindustry.type.Item

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
    open fun acceptItem(source: Building?, item: Item?): Boolean {
        return false
    }
    open fun handleItem(source: Building?, item: Item?) {

    }
    open fun shouldShown():Boolean{
        return false
    }
    open fun show(table: Table) {}
    open fun update() {
        element.setPosition(view.panX + Scl.scl(x), view.panY + Scl.scl(y))
    }
}