package ice.library.type

import arc.Core
import arc.math.Interp
import arc.scene.actions.Actions
import arc.scene.ui.Label
import arc.scene.ui.layout.Stack
import arc.scene.ui.layout.Table
import arc.util.Align
import ice.content.IceUnitTypes
import ice.library.scene.action.IceActions
import ice.library.scene.element.DrawIcon
import ice.library.scene.element.Shelter
import ice.library.scene.texs.Texs
import mindustry.Vars
import mindustry.game.Team
import mindustry.ui.Styles

object Incident {

    val graphicsheight = Core.graphics.height.toFloat()
    val graphicswidth = Core.graphics.width.toFloat()
    val shelter = Core.graphics.height.toFloat() * 0.15f

    fun announce(text: String, duration: Float) {
        val time = duration / 3
        val tile = Vars.world.tile(50, 50)
        val table = object : Table() {
            init {
                Vars.control.input.logicCutscene = true
                Vars.control.input.logicCamPan.set(tile)
                Vars.control.input.logicCamSpeed = time / 100
                Vars.control.input.logicCutsceneZoom = 0.1f
                Vars.ui.hudfrag.shown = false
            }

            override fun remove(): Boolean {
                Vars.control.input.logicCutscene = false
                Vars.ui.hudfrag.shown = true
                return super.remove()
            }
        }
        val fLabel = object : Label(text) {
            init {
                color.a(0f)
                style = Styles.outlineLabel
                setAlignment(Align.center)
                update {
                    setPosition((graphicswidth - width) / 2, graphicsheight / 2)
                }
                actions(Actions.alpha(1f, 3f, Interp.pow3Out), Actions.delay(3f), Actions.alpha(0f, 3f, Interp.pow3In))
            }
        }
        table.x = 0f
        table.y = 0f
        val drawIcon = DrawIcon(
            graphicswidth / 2, graphicsheight / 2, Texs.missionaryIcon
        )
        drawIcon.actions(
            Actions.alpha(0f),
            Actions.alpha(1f, 2f, Interp.pow3Out),
            Actions.delay(4f),
            Actions.alpha(0f, 3f, Interp.pow3In)
        )

        val drawn = DrawIcon(
            graphicswidth / 2 + 550, graphicsheight / 2, Texs.missionaryIconTurNingRight1
        )
        drawn.actions(
            Actions.alpha(0f),
            Actions.alpha(1f, 5f, Interp.pow3Out),
            Actions.delay(1f),
            Actions.alpha(0f, 3f, Interp.pow3In)
        )

        val draws = DrawIcon(
            graphicswidth / 2 - 550, graphicsheight / 2, Texs.missionaryIconTurNingLeft1
        )
        draws.actions(
            Actions.alpha(0f),
            Actions.alpha(1f, 5f, Interp.pow3Out),
            Actions.delay(1f),
            Actions.alpha(0f, 3f, Interp.pow3In)
        )

        val drawn1 = DrawIcon(
            graphicswidth / 2 + 600, graphicsheight / 2, Texs.missionaryIconTurNingRight2
        )
        drawn1.actions(
            Actions.alpha(0f),
            Actions.alpha(1f, 6f, Interp.pow3Out),
            Actions.delay(0f),
            Actions.alpha(0f, 3f, Interp.pow3In)
        )

        val draws1 = DrawIcon(
            graphicswidth / 2 - 600, graphicsheight / 2, Texs.missionaryIconTurNingLeft2
        )
        draws1.actions(
            Actions.alpha(0f),
            Actions.alpha(1f, 6f, Interp.pow3Out),
            Actions.delay(0f),
            IceActions.moveToAlphaAction(600f, 200f, 3f, 0f, Interp.pow3In)
        )



        table.add(
            Stack(
                drawIcon,
                drawn,
                draws,
                drawn1,
                draws1,
                Shelter.ShelterUp(0f, shelter, graphicswidth, shelter, 3f, 3f),
                fLabel,
                Shelter.ShelterDown(0f, shelter, graphicswidth, shelter, 3f, 3f)
            )
        )
        table.actions(
            Actions.delay(3 + 2f),
            IceActions.spawnAction(IceUnitTypes.传教者, tile.worldx(), tile.worldy(), 0f, Team.sharded),
            Actions.delay(0.5f),
            IceActions.spawnAction(IceUnitTypes.仆从, tile.worldx(), tile.worldy() - 10 * 8, 0f, Team.sharded),
            Actions.delay(0.5f),
            IceActions.spawnAction(IceUnitTypes.仆从, tile.worldx(), tile.worldy() + 10 * 8, 0f, Team.sharded),
            Actions.delay(3f),
            Actions.remove()
        )
        table.pack()
        table.act(0.1f)
        Core.scene.add(table)
    }
}