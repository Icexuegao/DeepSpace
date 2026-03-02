package ice.ui

import arc.Core
import arc.Graphics
import arc.audio.Sound
import arc.util.OS
import ice.DeepSpace
import ice.audio.ISounds
import ice.core.SettingValue
import ice.graphics.IStyles
import ice.library.DeBugFragment
import ice.library.IFiles
import ice.library.struct.log
import ice.library.world.Load
import ice.ui.dialog.IcePlanetDialog
import ice.ui.fragment.ConversationFragment
import ice.ui.fragment.FleshFragment
import ice.ui.menusDialog.DataDialog
import mindustry.Vars
import mindustry.gen.Icon
import mindustry.gen.Sounds
import singularity.Sgl
import singularity.ui.fragments.ToolBarFrag

object UI : Load {
  val cgwidth = Core.graphics.width.toFloat()
  val cgheight = Core.graphics.height.toFloat()
  val sfxVolume: Float get() = Core.settings.getInt("sfxvol") / 100f
  var toolBarFrag: ToolBarFrag = Sgl.ui.toolBar

  override fun init() {
    toolBarFrag.addTool("deepSpaceMenu", { "模组菜单" }, { Icon.menu }, {
      MenusDialog.show()
    }) { false }
    toolBarFrag.addTool("data", { "打开当前内容数据" }, { Icon.book }, {
      Vars.control.input.block?.let {
        DataDialog.showUnlockableContent(it)
      } ?: run {
        Vars.control.input.lastUnit?.let {
          DataDialog.showUnlockableContent(it.type)
        }
      }
    }) { false }

    Sgl.ui.toolBar.addTool("debugMonitor", { "调试监视器" }, { Icon.wrench }, {
      Sgl.ui.debugInfos.hidden = !Sgl.ui.debugInfos.hidden
    }, { !Sgl.ui.debugInfos.hidden })
    //上一次保存的调试配置 调试常开真的很sb
    if (!SettingValue.启用调试模式) toolBarFrag.hideTool("debugMonitor")
    SettingValue.addDeBugRun {
      log {
        it
      }
      if (it) toolBarFrag.showTool("debugMonitor") else {
        log {
          Sgl.ui.debugInfos.hidden
        }
        toolBarFrag.hideTool("debugMonitor")
        Sgl.ui.debugInfos.hidden = true
      }
    }
    //解决第一次选择星球报错问题
    Core.settings.put("campaignselect", true)
    Vars.ui.planet = IcePlanetDialog
    FleshFragment.build(Vars.ui.hudGroup)
    //  ScenarioFragment.build(Vars.ui.hudGroup)
    DeBugFragment.build(Vars.ui.hudGroup)
    //  BossHealthFragment.build(Vars.ui.hudGroup)
    ConversationFragment.build(Vars.ui.hudGroup)
    //if (SettingValue.启用调试模式)// ShowProgress.build(Vars.ui.hudGroup)
    //  CharacterScenarioFragment.build(Vars.ui.hudGroup)

    if (OS.isWindows) {
      loadSystemCursors()
    }

    Vars.ui.menufrag.addButton("[#${SettingValue.difficulty.color}]${DeepSpace.displayName}[]", Icon.menu){
      MenusDialog.show()
      showSoundCloseV(ISounds.进入模组界面)
    }
  }
  fun showSoundCloseV(sound: Sound){
    //非常愚蠢
    sound.play()
    Sounds.uiButton.stop()
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