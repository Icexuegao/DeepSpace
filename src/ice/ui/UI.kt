package ice.ui

import arc.scene.ui.layout.Scl
import ice.Ice
import ice.ui.dialogs.DeBugDialog
import ice.ui.dialogs.IceContentInfoDialog
import ice.ui.menus.MenusDialog
import ice.ui.menus.SettingValue
import ice.ui.tex.Colors
import mindustry.Vars
import mindustry.gen.Icon
import mindustry.type.Planet
import mindustry.ui.Fonts
import mindustry.ui.dialogs.PlanetDialog


object UI {

    fun init() {
        Ice.ice.meta.displayName = "Deep Space"
        Ice.ice.meta.author = Ice.author.random()
        Ice.ice.meta.version = "0.12"
        Ice.ice.meta.java = true
        Ice.ice.meta.description = "一个多方位内容的模组,从星球到建筑,摒弃过量数值内容,争取用机制来减少同质化问题"
        Vars.ui.content = IceContentInfoDialog()
        sectorID()
        DeBugDialog.show()
        Vars.ui.menufrag.addButton("[#${Colors.rand}]DeepSpace[]", Icon.menu, MenusDialog::show)
    }

    private fun sectorID() {
        val fontScl = 0.6f / Scl.scl()
        Vars.ui.planet = object : PlanetDialog() {
            override fun renderProjections(planet: Planet) {
                super.renderProjections(planet)
                if (SettingValue.getPlanetSectorId()) {
                    for (sec in planet.sectors) {
                        planets.drawPlane(sec) {
                            Fonts.outline.draw(sec.id.toString(), 0f, 0f, Colors.b4, fontScl, true, 1)
                        }
                    }
                }
            }
        }
    }
}




