package ice.content.unit

import arc.func.Prov
import ice.graphics.IceColor
import ice.ui.bundle.BaseBundle.Companion.bundle
import ice.world.content.unit.IceUnitType
import mindustry.ai.UnitCommand
import mindustry.ai.types.MinerAI

class Harvester : IceUnitType("harvester") {
  init {
    speed = 2f
    flying = true
    hitSize = 10f
    isEnemy = false
    mineTier = 2
    mineSpeed = 3f
    engineColor = IceColor.b4
    defaultCommand = UnitCommand.mineCommand
    aiController = Prov(::MinerAI)
    bundle {
      desc(zh_CN, "收割", "轻型空中工程单位.搭载高效激光共振钻头,专精于资源采集作业")
    }
  }
}