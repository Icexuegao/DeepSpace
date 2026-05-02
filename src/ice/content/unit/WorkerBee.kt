package ice.content.unit

import universecore.util.toColor
import ice.world.content.unit.IceUnitType
import mindustry.ai.UnitCommand
import mindustry.gen.BuildingTetherPayloadUnit

class WorkerBee :IceUnitType("unit_workerBee", BuildingTetherPayloadUnit::class.java) {
  init {
    localization {
      zh_CN {
        this.localizedName = "工蜂"
        description = "轻型空中工程单位.配备精密的模块化建造系统,负责大型单位的建造与装配任务"
      }
      en {
        this.localizedName = "Worker Bee"
        description = "Light aerial engineering unit with modular construction system"
      }
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