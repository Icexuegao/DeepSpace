package ice.ui

import arc.Core
import arc.Graphics
import arc.graphics.Texture
import arc.graphics.g2d.Draw
import arc.util.OS
import ice.Ice
import ice.core.SettingValue
import ice.graphics.IceColor
import ice.library.IFiles
import ice.library.universecore.ui.ToolBarFrag
import ice.library.util.accessField
import ice.library.world.Load
import ice.ui.dialog.IcePlanetDialog
import ice.ui.dialog.MenusDialog
import ice.ui.fragment.*
import mindustry.Vars
import mindustry.gen.Icon
import mindustry.graphics.MenuRenderer
import mindustry.ui.fragments.MenuFragment

object UI : Load {
    val cgwidth = Core.graphics.width.toFloat()
    val cgheight = Core.graphics.height.toFloat()
    val sfxVolume = Core.settings.getInt("sfxvol") / 100f
    var MenuFragment.renderer: MenuRenderer by accessField("renderer")
    var menuRender = object : MenuRenderer() {
        val spacea = IFiles.findIcePng("spacea")
        override fun render() {
            Draw.color()
            Draw.rect(
                spacea, cgwidth / 2, cgheight / 2,
                cgwidth, cgheight
            )
        }
    }
    var toolBarFrag = ToolBarFrag()

    override fun init() {
        toolBarFrag.init()
        toolBarFrag.addTool("deepSpaceMenu", { Icon.menu }, {
            MenusDialog.show()
        }) { false }
        //字体缩放模糊问题
        Core.atlas.textures.forEach {
            val fid = Texture.TextureFilter.nearest
            it.setFilter(fid, fid)
        }
        //解决第一次选择星球报错问题
        Core.settings.put("campaignselect", true)
        Vars.ui.menufrag.renderer = menuRender
        Vars.ui.planet = IcePlanetDialog
        FleshFragment.build(Vars.ui.hudGroup)
        ScenarioFragment.build(Vars.ui.hudGroup)
        DeBugFragment.build(Vars.ui.hudGroup)
        //  BossHealthFragment.build(Vars.ui.hudGroup)
        ConversationFragment.build(Vars.ui.hudGroup)
        ShowProgress.build(Vars.ui.hudGroup)
        //  CharacterScenarioFragment.build(Vars.ui.hudGroup)
        Ice.mod.meta.author = "[#${IceColor.b4}]Alon[]"
        Ice.mod.meta.displayName = "[#${IceColor.b4}]Deep Space[]"

        if (OS.isWindows) {
            loadSystemCursors()
        }

        Vars.ui.menufrag.addButton("[#${SettingValue.difficulty.color}]DeepSpace[]", Icon.menu, MenusDialog::show)
        Core.atlas.regionMap.put("logo", IFiles.findIcePng("logo"))
    }

    fun loadSystemCursors() {
        Graphics.Cursor.SystemCursor.arrow.set(IFiles.newCursor("cursor"))
        Graphics.Cursor.SystemCursor.hand.set(IFiles.newCursor("hand"))
        Graphics.Cursor.SystemCursor.ibeam.set(IFiles.newCursor("ibeam"))
        Vars.ui.drillCursor = IFiles.newCursor("drill")
        Vars.ui.unloadCursor = IFiles.newCursor("unload")
        Vars.ui.targetCursor = IFiles.newCursor("target")
        Vars.ui.repairCursor = IFiles.newCursor("repair")
        Core.graphics.restoreCursor()
    }
}