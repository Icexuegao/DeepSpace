package ice.ui

import arc.Core
import arc.Graphics
import arc.util.OS
import ice.DeepSpace
import ice.core.SettingValue
import ice.graphics.IceColor
import ice.library.IFiles
import ice.library.world.Load
import ice.ui.dialog.IcePlanetDialog
import ice.ui.fragment.*
import mindustry.Vars
import mindustry.gen.Icon
import singularity.Sgl
import singularity.ui.fragments.ToolBarFrag

object UI : Load {
  val cgwidth = Core.graphics.width.toFloat()
  val cgheight = Core.graphics.height.toFloat()
  val sfxVolume = Core.settings.getInt("sfxvol") / 100f
  var toolBarFrag: ToolBarFrag = Sgl.ui.toolBar

  override fun init() {
    toolBarFrag.addTool("deepSpaceMenu", { Icon.menu }, {
      MenusDialog.show()
    }) { false }
    toolBarFrag.addTool("TexTNotification",{ Icon.add},{

    }){false}
    //解决第一次选择星球报错问题
    Core.settings.put("campaignselect", true)
    Vars.ui.planet = IcePlanetDialog
    FleshFragment.build(Vars.ui.hudGroup)
    ScenarioFragment.build(Vars.ui.hudGroup)
    DeBugFragment.build(Vars.ui.hudGroup)
    //  BossHealthFragment.build(Vars.ui.hudGroup)
    ConversationFragment.build(Vars.ui.hudGroup)
    if (SettingValue.启用调试模式) ShowProgress.build(Vars.ui.hudGroup)
    //  CharacterScenarioFragment.build(Vars.ui.hudGroup)
    DeepSpace.mod.meta.author = "[#${IceColor.b4}]Alon[]"
    DeepSpace.mod.meta.displayName = "[#${IceColor.b4}]Deep Space[]"

    if (OS.isWindows) {
      loadSystemCursors()
    }

    Vars.ui.menufrag.addButton("[#${SettingValue.difficulty.color}]DeepSpace[]", Icon.menu, MenusDialog::show)
    Core.atlas.regionMap.put("logo", IFiles.findModPng("logo"))
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