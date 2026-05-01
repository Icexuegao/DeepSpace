package ice.content.unit

import arc.func.Prov
import ice.graphics.IceColor

import ice.world.content.unit.IceUnitType
import mindustry.Vars
import mindustry.ai.UnitCommand
import mindustry.ai.types.MinerAI

class Harvester : IceUnitType("harvester") {
  init {
    localization {
      zh_CN {
        localizedName = "收割"
        description = "轻型空中工程单位.搭载高效激光共振钻头,专精于资源采集作业"
      }
    }
    speed = 2f
    flying = true
    hitSize = 0.75f*8f
    isEnemy = false
    mineTier = 2
    mineSpeed = 3f
    engineColor = IceColor.b4
    defaultCommand = UnitCommand.mineCommand
    aiController = Prov(::MinerAI)
    mineItems.add(Vars.content.items())
  }
}