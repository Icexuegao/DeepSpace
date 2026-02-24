package ice.content.unit

import ice.library.util.toColor
import ice.ui.bundle.BaseBundle
import ice.world.content.unit.IceUnitType
import mindustry.ai.UnitCommand
import mindustry.gen.BuildingTetherPayloadUnit

class WorkerBee : IceUnitType("unit_workerBee", BuildingTetherPayloadUnit::class.java) {
  init {
    BaseBundle.bundle {
      desc(zh_CN, "工蜂", "轻型空中工程单位.配备精密的模块化建造系统,负责大型单位的建造与装配任务")
    }
    defaultCommand = UnitCommand.assistCommand

    flying = true
    health = 360f
    armor = 1f
    hitSize = 6f
    speed = 1.5f
    drag = 0.05f
    envDisabled = 0
    engineSize = 2f
    engineOffset = 5.5f
    outlineColor = "313131".toColor()
    hidden = true
    isEnemy = false
    targetable = false
    lowAltitude = true
    useUnitCap = true
    createWreck = false
    createScorch = false
    logicControllable = false
    playerControllable = false
    allowedInPayloads = false
  }
}