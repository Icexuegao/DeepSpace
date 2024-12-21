package ice.ui.dialogs

import arc.graphics.Color
import arc.scene.ui.layout.Scl
import ice.ui.menus.SettingValue
import mindustry.Vars
import mindustry.type.Planet
import mindustry.ui.Fonts
import mindustry.ui.dialogs.PlanetDialog

/**
 * 星球ID
 **/
object SectorID {
    fun load() {
        val fontScl = 0.6f / Scl.scl()
        Vars.ui.planet = object : PlanetDialog() {
            override fun renderProjections(planet: Planet) {
                super.renderProjections(planet)
                if (SettingValue.shown) {
                    for (sec in planet.sectors) {
                        planets.drawPlane(sec) {
                            Fonts.outline.draw(
                                sec.id.toString(), 0f, 0f, Color.white, fontScl, true, 1
                            )
                        }
                    }
                }
            }
        }
    }
}