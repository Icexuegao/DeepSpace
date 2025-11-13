package ice.library.game

import ice.library.scene.tex.IceColor
import mindustry.game.Team

object IceTeam {
    val afehs: Team = Team.all[92].apply {
        name = "afehs"
        color.set(IceColor.b4)
    }
    val flesh: Team = Team.all[93].apply {
        name = "flesh"
        color.set(IceColor.b4)
    }
}