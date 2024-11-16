package ice.Alon.ui

import arc.Core
import arc.Events
import arc.graphics.Color
import arc.scene.ui.TextButton
import arc.scene.ui.layout.Scl
import arc.scene.ui.layout.Stack
import arc.scene.ui.layout.Table
import mindustry.Vars
import mindustry.game.EventType.ClientLoadEvent
import mindustry.type.Planet
import mindustry.ui.Fonts
import mindustry.ui.Styles
import mindustry.ui.dialogs.PlanetDialog

class SectorID {
    /**
     * 星球ID
     */
    companion object {
        var shown = true
        fun load() {
            shown = Core.settings.getBool("planetSectorId", false)
            val font = Fonts.outline
            val fontScl = 0.6f / Scl.scl()
            Vars.ui.planet = object : PlanetDialog() {
                init {
                    shown { rebuildButton() }
                }

                override fun renderProjections(planet: Planet) {
                    super.renderProjections(planet)
                    if (shown) {
                        val alpha = state.uiAlpha
                        if (!(alpha < 1.0E-4f)) {
                            for (sec in planet.sectors) {
                                planets.drawPlane(
                                    sec
                                ) {
                                    font.draw(
                                        sec.id.toString(), 0.0f, 0.0f, Color.white, fontScl, true, 1
                                    )
                                }
                            }
                        }
                    }
                }

                fun rebuildButton() {
                    val stack = getChildren()[0] as Stack
                    val table = stack.children[3] as Table
                    table.row()
                    table.table(
                        Styles.black6
                    ) { t: Table ->
                        t.button(
                            "显示星球区块id", Styles.flatTogglet
                        ) {
                            shown = !shown
                            Core.settings.put("planetSectorId", shown)
                        }.width(200f).height(40f).growX().checked { shown }
                    }.fillX()
                }
            }

        }
    }
}