package ice.ui.scene.action

import arc.math.Interp
import arc.scene.actions.Actions
import ice.Text.TextFx
import ice.music.IceSounds
import ice.ui.tex.Colors
import mindustry.entities.Effect
import mindustry.game.Team
import mindustry.type.UnitType

object IceActions {
    fun spawnAction(unit: UnitType, x: Float, y: Float, rotate: Float, team: Team): UnitSpawnAction {
        val action = Actions.action(UnitSpawnAction::class.java) { UnitSpawnAction() }
        action.runnable = Runnable {
            val spawn = unit.spawn(team, x, y)
            spawn.rotation = rotate
            TextFx.jumpTrail.at(x, y, rotate, Colors.b4, spawn.type)
            IceSounds.foldJump.at(spawn)
            Effect.shake(spawn.hitSize / 3f, spawn.hitSize / 4f, spawn)
        }
        return action
    }

    fun moveToAlphaAction(x: Float, y: Float, duration: Float, alpha: Float, interpolation: Interp): MoveToAlphaAction {
        val action = Actions.action(MoveToAlphaAction::class.java) { MoveToAlphaAction() }
        action.setAlpha(alpha)
        action.duration = duration
        action.interpolation = interpolation
        action.setPosition(x, y)
        return action
    }
}