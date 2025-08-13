package ice.game

import ice.library.scene.tex.Colors
import mindustry.game.Team

object IceTeam {
    val afehs: Team = Team.all[92].apply {
        name = "afehs"
        color.set(Colors.b4)
    }
    val flesh: Team = Team.all[93].apply {
        name = "flesh"
        color.set(Colors.b4)
    }
}