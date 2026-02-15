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
      desc(zh_CN, "收割", "全新设计的采矿单位,搭载了高效的激光共振钻头")
    }
  }
}