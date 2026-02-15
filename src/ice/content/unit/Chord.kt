package ice.content.unit

import arc.func.Func
import ice.ai.CarryTaskAI
import ice.ui.bundle.BaseBundle.Companion.bundle
import ice.world.content.unit.IceUnitType

class Chord : IceUnitType("chord") {
  init {
    drag = 0.017f
    accel = 0.05f
    armor = 8f
    speed = 3.5f
    flying = true
    health = 60f
    hitSize = 12f
    isEnemy = false
    useUnitCap = false
    rotateSpeed = 3f
    lowAltitude = false
    itemCapacity = 30
    allowedInPayloads = false
    logicControllable = false
    playerControllable = false
    controller = Func { CarryTaskAI() }
    bundle {
      desc(zh_CN, "和弦")
    }
  }
}