package ice.ui

import arc.Core
import arc.Events
import arc.graphics.Color
import arc.math.Interp
import arc.scene.actions.Actions
import arc.scene.style.TextureRegionDrawable
import arc.scene.ui.Dialog
import arc.scene.ui.layout.Table
import ice.Ice
import ice.graphics.IceColor
import ice.library.world.Load
import mindustry.Vars
import mindustry.game.EventType.WorldLoadEndEvent
import mindustry.gen.Icon
import mindustry.ui.dialogs.BaseDialog
import singularity.Sgl
import singularity.ui.fragments.notification.Notification
import universecore.ui.elements.markdown.Markdown
import universecore.ui.elements.markdown.MarkdownStyles
import universecore.util.DataPackable

object Documents : Load {
  val 中子能 = getDialog("<<中子能操作手册>>", "nuclear_energy_blocks.md")
  val 节点配置 = getDialog("<<节点配置面板>>", "matrix_grid_config_help.md")
  val text = getDialog("<<text>>", "test.md")
  override fun init() {
    Events.on(WorldLoadEndEvent::class.java) {
      Sgl.ui.notificationFrag.notify(DocumentNotification("中子能", "有关中子能的详细描述", 中子能))
      Sgl.ui.notificationFrag.notify(DocumentNotification("节点配置", "有关配置面板的详细描述", 节点配置))
     // Sgl.ui.notificationFrag.notify(DocumentNotification("text", "有关text的详细描述", text))
    }
  }

  fun getDialog(title: String, md: String): BaseDialog {
    return BaseDialog(title).apply {
      val element = Markdown(Vars.mods.getMod(Ice::class.java).root.child("documents").child("zh_CN").child(md).readString(), MarkdownStyles.defaultMD)
      cont.pane {
        it.add(element).grow()
      }.grow()
      addCloseButton()
    }
  }

  class DocumentNotification(name: String, description: String, var dialog: Dialog) : Notification("操作指南: $name", description) {
    companion object {
      const val typeID: Long = 12139764028768494L
      fun assign() {
        DataPackable.assignType(typeID) { args: Array<Any> ->
        }
      }
    }

    override fun getIcon(): TextureRegionDrawable = Icon.bookOpen

    override fun activity() {
      dialog.show(Core.scene, Actions.sequence(Actions.alpha(0f), Actions.fadeIn(0.4f, Interp.fade)))
    }

    override fun buildWindow(table: Table?) {
    }

    override fun getIconColor(): Color {
      return IceColor.b4
    }

    override fun getTitleColor(): Color {
      return IceColor.b4
    }

    override fun getInformationColor(): Color {
      return IceColor.b4
    }

    override fun typeID() = typeID
  }
}