package ice.library.scene.listener

import arc.input.KeyCode
import arc.scene.Element
import arc.scene.Group
import arc.scene.event.InputEvent
import arc.scene.event.InputListener
import ice.library.struct.log
import ice.ui.dialog.research.View

class LinkListener(val group: View) : InputListener() {
    init {
        group.addListener(this)
    }

    var i = 0
    var e1: Element? = null
    var e2: Element? = null
    override fun touchUp(event: InputEvent?, x: Float, y: Float, pointer: Int, button: KeyCode?) {
        if (i == 0) {
            i++
            e1 = group.hit(x, y, false)
        } else if (i == 1) {
            e2 = group.hit(x, y, false)
            log { group.links.size }
            val n1 = group.links.find {
                val element = it.element

                if (element is Group) {
                    return@find element.children.contains(e1)
                }
                if (element == e1) {
                    return@find true
                }
                return@find false
            }
            val n2 = group.links.find {
                val element = it.element

                if (element is Group) {
                    return@find element.children.contains(e2)
                }
                if (element == e2) {
                    return@find true
                }
                return@find false
            }
            if (n1 == null) {
                log { "n1=null" }
                return
            }
            if (n2 == null) {
                log { "n2=null" }
                return
            }
            n1.child.addUnique(n2)
            n2.parent = n1
            group.removeListener(this)
            return
        }
        super.touchUp(event, x, y, pointer, button)
    }

    override fun touchDown(event: InputEvent?, x: Float, y: Float, pointer: Int, button: KeyCode?): Boolean {
        return true
    }
}