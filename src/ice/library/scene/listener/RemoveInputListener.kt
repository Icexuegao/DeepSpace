package ice.library.scene.listener

import arc.input.KeyCode
import arc.scene.event.InputEvent
import arc.scene.event.InputListener
import arc.scene.ui.layout.Table
import ice.ui.dialog.research.View

class RemoveInputListener(val group: View) : InputListener() {
    init {
        group.addListener(this)
    }
    override fun touchUp(event: InputEvent?, x: Float, y: Float, pointer: Int, button: KeyCode?) {
        val hit = group.hit(x, y, false)
        group.removeChild(hit)
        val find = group.links.find {
            val element = it.element
            if (element is Table){
             return@find  element==hit||element.children.contains(hit)
            }
            return@find false
        }?:return

        find.parent?.child?.remove(find)
        find.child.clear()
        group.links.remove(find)

        group.removeListener(this)
        super.touchUp(event, x, y, pointer, button)
    }

    override fun touchDown(event: InputEvent?, x: Float, y: Float, pointer: Int, button: KeyCode?): Boolean {
        return true
    }
}