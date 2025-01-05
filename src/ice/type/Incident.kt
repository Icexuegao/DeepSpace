package ice.type

import arc.Core
import arc.flabel.FLabel
import arc.graphics.g2d.Draw
import arc.math.Interp
import arc.scene.Element
import arc.scene.actions.Actions
import arc.scene.ui.layout.Stack
import arc.scene.ui.layout.Table
import arc.util.Align
import ice.content.IceUnitTypes
import ice.ui.scene.action.IceActions
import ice.ui.scene.action.IceActions.moveToAlphaAction
import mindustry.Vars
import mindustry.game.Team
import mindustry.ui.Styles

object Incident {

    val graphicsheight = Core.graphics.height.toFloat()
    val graphicswidth = Core.graphics.width.toFloat()
    val shelter = Core.graphics.height.toFloat() * 0.15f
    val heightDraw = shelter

    fun announce(text: String, duration: Float) {
        val time = duration / 3
        val table = object : Table() {
            init {
                Vars.ui.hudfrag.shown = false
            }

            override fun remove(): Boolean {
                Vars.ui.hudfrag.shown = true
                return super.remove()
            }
        }

        val top1 = object : Element() {
            init {
                val tile = Vars.world.tile(50, 50)
                color.a = 0f
                actions(
                    Actions.run {
                        Vars.control.input.logicCutscene = true
                        Vars.control.input.logicCamPan.set(tile)
                        Vars.control.input.logicCamSpeed = time / 100
                        Vars.control.input.logicCutsceneZoom = 0.1f
                    },
                    moveToAlphaAction(x, y - shelter, time, 1f, Interp.pow3Out),
                    Actions.delay(2f),
                    IceActions.spawnAction(IceUnitTypes.missionary, tile.worldx(), tile.worldy(), 0f, Team.sharded),
                    Actions.delay(0.5f),
                    IceActions.spawnAction(
                        IceUnitTypes.footman, tile.worldx(), tile.worldy() - 10 * 8, 0f, Team.sharded
                    ),
                    Actions.delay(0.5f),
                    IceActions.spawnAction(
                        IceUnitTypes.footman, tile.worldx(), tile.worldy() + 10 * 8, 0f, Team.sharded
                    ),
                    moveToAlphaAction(x, y + shelter, time, 0f, Interp.pow3In),
                    Actions.run {
                        Vars.control.input.logicCutscene = false
                    },
                )
            }

            override fun draw() {
                super.draw()
                Draw.alpha(color.a)
                Styles.black.draw(x, y + graphicsheight, graphicswidth, heightDraw)
            }
        }
        val bottom = object : Element() {
            init {
                color.a = 0f
                actions(
                    moveToAlphaAction(x, y + shelter, time, 1f, Interp.pow3Out),
                    Actions.delay(time),
                    moveToAlphaAction(x, y - shelter, time, 0f, Interp.pow3In),
                )
            }

            override fun draw() {
                super.draw()
                Draw.alpha(color.a)
                Styles.black.draw(x, y, graphicswidth, -heightDraw)
            }
        }
        val fLabel = object : FLabel(text) {
            init {
                color.a(0f)
            }
        }




        fLabel.style = Styles.outlineLabel
        fLabel.setAlignment(Align.center)
        fLabel.update {
            fLabel.setPosition(graphicswidth / 2 - fLabel.width / 2, graphicsheight / 2)
        }
        fLabel.actions(
            Actions.alpha(1f, time, Interp.pow3In),
            Actions.delay(time),
            Actions.alpha(0f, time, Interp.pow3In),
            Actions.remove()
        )
        table.update {
            table.setPosition(0f, 0f)
        }
        table.add(Stack(top1, bottom, fLabel)).update {
            it.x = 0f
            it.y = 0f
        }
        // table.touchable = Touchable.disabled
        table.actions(Actions.delay(duration + 1, Actions.remove()))
        table.pack()
        table.act(0.1f)
        Core.scene.add(table)

    }
}