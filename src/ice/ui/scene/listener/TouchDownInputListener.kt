package ice.ui.scene.listener

import arc.input.KeyCode
import arc.scene.event.InputEvent
import arc.scene.event.InputListener

/**点击侦测*/
open class TouchDownInputListener(private val runnable: Runnable) : InputListener() {
    override fun touchDown(event: InputEvent, x: Float, y: Float, pointer: Int, button: KeyCode): Boolean {
        runnable.run()
        return false
    }
}