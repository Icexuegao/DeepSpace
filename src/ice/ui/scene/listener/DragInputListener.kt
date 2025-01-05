package ice.ui.scene.listener

import arc.input.KeyCode
import arc.scene.Element
import arc.scene.event.InputEvent
import arc.scene.event.InputListener

/**拖动侦测,给元素添加检测器,参数传table*/
open class DragInputListener(private var e: Element) : InputListener() {
    private var statX = 0f
    private var statY = 0f

    override fun touchDown(event: InputEvent, x: Float, y: Float, pointer: Int, button: KeyCode): Boolean {
        statX = x
        statY = y
        return true
    }

    override fun touchDragged(event: InputEvent, x: Float, y: Float, pointer: Int) {
        e.x += x - statX
        e.y += y - statY
    }
}