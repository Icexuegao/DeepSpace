package ice.game

import ice.graphics.IceColor
import mindustry.game.Team

object IceTeam {
    fun load() = Unit
    val 教廷: Team = Team.all[92].apply {
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
}