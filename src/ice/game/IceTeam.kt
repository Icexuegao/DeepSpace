package ice.game

import ice.graphics.IceColor
import mindustry.game.Team
import universecore.world.Load

object IceTeam :Load {
  val 教廷: Team = Team.blue.apply {
    name = "afehs"
    setPalette(IceColor.b4, IceColor.b6, IceColor.b7)
  }
  val 血肉: Team = Team.all[93].apply {
    name = "flesh"
    color.set(IceColor.r2)
  }
  val 帝国: Team = Team.all[94].apply {
    name = "empire"
    color.set(IceColor.r5)
  }

  val none: Team = Team.get(255)
}