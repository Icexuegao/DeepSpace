package universecore.scene.listener

import arc.input.KeyCode
import arc.math.geom.Vec2
import arc.scene.Element
import arc.scene.event.DragListener
import arc.scene.event.InputEvent
import arc.util.Tmp

class DragInputListener(var targetElement: Element) : DragListener() {
  var lastX: Float = 0f
  var lastY: Float = 0f

  override fun touchDown(event: InputEvent?, x: Float, y: Float, pointer: Int, button: KeyCode?): Boolean {
   val pos: Vec2 = targetElement.localToStageCoordinates(Tmp.v1.set(x, y))
    lastX = pos.x
    lastY = pos.y
    return true
  }

  override fun touchDragged(event: InputEvent?, x: Float, y: Float, pointer: Int) {
    val pos: Vec2 = targetElement.localToStageCoordinates(Tmp.v1.set(x, y))
    targetElement.moveBy(pos.x - lastX, pos.y - lastY)
    lastX = pos.x
    lastY = pos.y
  }
}