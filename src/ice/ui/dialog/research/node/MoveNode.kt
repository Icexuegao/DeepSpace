package ice.ui.dialog.research.node

import arc.func.Prov
import arc.scene.Element

open class MoveNode(x:Float, y:Float, ep: Prov<Element>):Node(x,y, ep) {
    var movex=0f
    var movey=0f
    override fun update() {
        element.setPosition(view.panX + x+movex, view.panY + y+movey)
    }
}