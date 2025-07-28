package ice.ui.dialog

import arc.scene.ui.layout.Scl
import ice.library.scene.texs.Colors
import ice.vars.SettingValue
import mindustry.type.Planet
import mindustry.ui.Fonts
import mindustry.ui.dialogs.PlanetDialog

object IcePlanetDialog : PlanetDialog() {
    val fontScl = 0.6f / Scl.scl()
    override fun renderProjections(planet: Planet) {
        super.renderProjections(planet)
        if (SettingValue.planetSectorId) {
            for (sec in planet.sectors) {
                planets.drawPlane(sec) {
                    Fonts.outline.draw(sec.id.toString(), 0f, 0f, Colors.b4, fontScl, true, 1)
                }
            }
        }
    }
}