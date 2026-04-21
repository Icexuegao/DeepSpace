package ice.content.unit

import arc.func.Func
import ice.ai.CarryTaskAI

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
    itemCapacity = 40
    allowedInPayloads = false
    logicControllable = false
    playerControllable = false
    controller = Func { CarryTaskAI() }
    localization {
      zh_CN {
        this.localizedName = "和弦"
        description = "轻型空中工程单位.配备大容量货舱与稳定悬挂系统,专精于物资运输任务.常三机编队协同作业"
      }
    }
  }
}
