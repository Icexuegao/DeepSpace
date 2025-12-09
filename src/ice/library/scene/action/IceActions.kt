package ice.library.scene.action

import arc.math.Interp
import arc.scene.actions.Actions
import ice.audio.ISounds
import ice.graphics.IceColor
import ice.world.meta.IceEffects
import mindustry.entities.Effect
import mindustry.game.Team
import mindustry.type.UnitType

object IceActions {
    fun spawnAction(unit: UnitType, x: Float, y: Float, rotate: Float, team: Team): UnitSpawnAction {
        val action = Actions.action(UnitSpawnAction::class.java) { UnitSpawnAction() }
        action.runnable = Runnable {
            val spawn = unit.spawn(team, x, y)
            spawn.rotation = rotate
            IceEffects.jumpTrail.at(x, y, rotate, IceColor.b4, spawn.type)
            ISounds.foldJump.at(spawn)
            Effect.shake(spawn.hitSize / 3f, spawn.hitSize / 4f, spawn)
        }
        return action
    }

    fun moveToAlphaAction(x: Float, y: Float, duration: Float, alpha: Float, interpolation: Interp= Interp.linear): MoveToAlphaAction {
        val action = Actions.action(MoveToAlphaAction::class.java) { MoveToAlphaAction() }
        action.setAlpha(alpha)
        action.duration = duration
        action.interpolation = interpolation
        action.setPosition(x, y)
        return action
    }
}