package ice.vars

import arc.Core
import arc.Events
import arc.Graphics.Cursor.SystemCursor
import arc.graphics.g2d.Draw
import ice.Ice
import ice.library.IFiles
import ice.library.IFiles.newCursor
import ice.library.scene.texs.Colors
import ice.library.util.accessField
import ice.ui.dialog.IcePlanetDialog
import ice.ui.dialog.MenusDialog
import ice.ui.fragment.DeBugFragment
import ice.ui.fragment.FleshFragment
import ice.ui.fragment.ScenarioFragment
import ice.ui.fragment.VoiceoverFragment
import mindustry.Vars
import mindustry.game.EventType
import mindustry.gen.Icon
import mindustry.graphics.MenuRenderer
import mindustry.ui.fragments.MenuFragment

object UI {
    val cgwidth = Core.graphics.width.toFloat()
    val cgheight = Core.graphics.height.toFloat()
    var MenuFragment.renderer: MenuRenderer by accessField("renderer")
    private val menuRender = object : MenuRenderer() {
        val spacea = IFiles.findIcePng("spacea")
        override fun render() {
            Draw.color()
            Draw.rect(spacea, cgwidth / 2, cgheight / 2,
                cgwidth, cgheight)
        }
    }

    fun init() {
        Vars.ui.menufrag.renderer = menuRender
        Events.run(EventType.Trigger.update) {
            if (Vars.ui.planet.isShown) {
                Vars.ui.planet.hide()
                IcePlanetDialog.show()
            }
        }
        FleshFragment.build(Vars.ui.hudGroup)
        ScenarioFragment.build(Vars.ui.hudGroup)
        DeBugFragment.build(Vars.ui.hudGroup)
      //  BossHealthFragment.build(Vars.ui.hudGroup)
        VoiceoverFragment.build(Vars.ui.hudGroup)
        //  ConversationFragment.build(Vars.ui.hudGroup)
        Ice.ice.meta.author = "[#${Colors.b4}]alon[]"
        Ice.ice.meta.displayName = "[#${Colors.b4}]Deep Space[]"

        if (!Vars.mobile) {
            loadSystemCursors()
        }
        Vars.ui.menufrag.addButton("[#${SettingValue.difficulty.color}]DeepSpace[]", Icon.menu, MenusDialog::show)
        Core.atlas.regionMap.put("logo", IFiles.findIcePng("logo"))
    }

    fun loadSystemCursors() {
        SystemCursor.arrow.set(newCursor("cursor"))
        SystemCursor.hand.set(newCursor("hand"))
        SystemCursor.ibeam.set(newCursor("ibeam"))
        Vars.ui.drillCursor = newCursor("drill")
        Vars.ui.unloadCursor = newCursor("unload")
        Vars.ui.targetCursor = newCursor("target")
        Vars.ui.repairCursor = newCursor("repair")
        Core.graphics.restoreCursor()
    }

}




