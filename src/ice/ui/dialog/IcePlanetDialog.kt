package ice.ui.dialog

import arc.scene.ui.layout.Scl
import ice.graphics.IceColor
import ice.Ice
import mindustry.type.Planet
import mindustry.ui.Fonts
import mindustry.ui.dialogs.PlanetDialog

object IcePlanetDialog : PlanetDialog() {
    val fontScl = 0.6f / Scl.scl()
    override fun renderProjections(planet: Planet) {
        super.renderProjections(planet)
        if (Ice.configIce.启用星球区块ID) {
            for (sec in planet.sectors) {
                planets.drawPlane(sec) {
                    Fonts.outline.draw(sec.id.toString(), 0f, 0f, IceColor.b4, fontScl, true, 1)
                }
            }
        }
    }
}