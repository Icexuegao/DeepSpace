package ice.scene

import arc.input.KeyCode
import arc.scene.Element
import arc.scene.event.InputEvent
import arc.scene.event.InputListener

class MyInputListener {


    /**拖动侦测,给元素添加检测器,参数传table*/
    open class DragInputListener( e: Element) : InputListener() {
        private var e: Element
        private var statX = 0f
        private var statY = 0f

        init {
            this.e = e
        }

        override fun touchDown(event: InputEvent, x: Float, y: Float, pointer: Int, button: KeyCode): Boolean {
            statX = x
            statY = y
            return true
        }

        override fun touchDragged(event: InputEvent, x: Float, y: Float, pointer: Int) {
                e.x += x - statX
                e.y += y - statY
        }

        override fun touchUp(event: InputEvent, x: Float, y: Float, pointer: Int, button: KeyCode) {}
    }

    open class TouchDownInputListener(private val runnable: Runnable) : InputListener() {
        override fun touchDown(event: InputEvent, x: Float, y: Float, pointer: Int, button: KeyCode): Boolean {
            runnable.run()
            return false
        }
    }
}