package ice.ui

import arc.Core
import arc.graphics.Color
import arc.math.Interp
import arc.scene.actions.Actions
import arc.scene.style.Drawable
import arc.scene.ui.Dialog
import arc.scene.ui.layout.Table
import arc.struct.Seq
import ice.DeepSpace
import ice.Ice
import ice.graphics.IStyles
import ice.graphics.IceColor
import mindustry.Vars
import mindustry.ui.dialogs.BaseDialog
import singularity.Sgl
import singularity.ui.fragments.notification.Notification
import universe.ui.markdown.Markdown
import universe.ui.markdown.MarkdownStyles
import universecore.struct.ConfigPropertyDelegate
import universecore.struct.texture.asDrawable
import universecore.util.DataPackable

object Documents {
  val 中子能 = DocumentNotificationData("中子能") {
    DocumentNotification("中子能", "有关中子能的详细描述", getDialog("<<中子能操作手册>>", "nuclear_energy_blocks.md")).apply {
      icons = IStyles.nuclear.asDrawable()
    }
  }

  val 节点配置 = DocumentNotificationData("节点配置") {
    DocumentNotification("节点配置", "有关配置面板的详细描述", getDialog("<<节点配置面板>>", "matrix_grid_config_help.md")).apply {
      icons = IStyles.matrix.asDrawable()
    }
  }

  // val text = getDialog("<<text>>", "test.md")

  class DocumentNotificationData(name: String, val noti: () -> DocumentNotification) {
    companion object{
      val setKey= Seq<String>(false)
      fun reset(){
        setKey.forEach {
          DeepSpace.globals.put(it, false)
        }
      }
    }
    var showed: Boolean by ConfigPropertyDelegate(false, "DocumentNotification_${name}")
    init {
      setKey.add("DocumentNotification_${name}")
    }
    /** 只展示一次 */
    fun shouldShowOne() {
      if (!showed) {
        showed=true
        Sgl.ui.notificationFrag.notify(noti())
      }
    }
  }

  fun getDialog(title: String, md: String): BaseDialog {
    return BaseDialog(title).apply {
      val element =
        Markdown(Vars.mods.getMod(Ice::class.java).root.child("documents").child("zh_CN").child(md).readString(), MarkdownStyles.defaultMD)
      cont.pane {
        it.add(element).grow()
      }.grow()
      addCloseButton()
    }
  }

  class DocumentNotification(name: String, description: String, var dialog: Dialog) : Notification("操作指南: $name", description) {
    lateinit var icons: Drawable

    companion object {
      const val typeID: Long = 12139764028768494L
      fun assign() {
        DataPackable.assignType(typeID) { args: Array<Any> ->
        }
      }
    }

    override fun getIcon() = icons

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