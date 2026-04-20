package ice.type

import arc.func.Cons
import arc.scene.style.TextureRegionDrawable
import arc.scene.ui.ImageButton
import arc.scene.ui.layout.Table
import arc.struct.Seq
import arc.util.Scaling
import ice.DeepSpace
import ice.audio.ISounds
import ice.graphics.IStyles
import ice.graphics.IceColor
import ice.library.IFiles
import ice.library.scene.ui.addLine
import ice.library.scene.ui.itooltip
import ice.library.struct.ConfigPropertyDelegate
import ice.ui.UI
import ice.ui.bundle.Localizable
import ice.ui.menusDialog.DataDialog
import ice.ui.menusDialog.RemainsDialog
import mindustry.Vars

open class Remains(val name: String) :Localizable {
  companion object {
    val remainsSeq = Seq<Remains>()

    fun getEnableds(): Seq<Remains> {
      return remainsSeq.select { it.unlock }
    }

    fun getNoEnableds(): Seq<Remains> {
      return remainsSeq.select { !it.unlock }
    }
  }

  var level = 0
  var effect = ""
  var icon = TextureRegionDrawable(IFiles.findModPng(name))
  var remainsColor = IceColor.b4
  var install = {}
  var uninstall = {}
  var disabled: () -> Boolean = { Vars.state.isGame }
  var customTable = Table()
  var buttonStyle = IStyles.button5

  var unlock: Boolean by ConfigPropertyDelegate(false, "${DeepSpace.modName}-remains-$name-enabled")

  init {
    remainsSeq.add(this)
  }

  fun setDescriptionTable(table: Cons<Table>) {
    table.get(customTable)
  }

  fun setEnabled(enabled: Boolean) {
    if (enabled) install() else uninstall()
    unlock = enabled
    DataDialog.contentDialog.flunAll()
  }

  open fun getTiTleTable(): Table {
    return Table().also {
      it.image(icon).scaling(Scaling.fit).size(120f).pad(30f).padTop(0f).row()
      it.table { table ->
        table.table(IStyles.gradient_right) { it1 ->
          it1.add("遗物").color(IceColor.b4).expandX().left().padLeft(4f)
        }.width(100f).height(30f).color(IceColor.b6).expandX().left().row()
        table.add(localizedName).color(remainsColor).fontScale(1.5f).pad(5f).padLeft(0f).expandX().left().row()
      }.grow().row()
      it.addLine().pad(3f)
      it.table { table ->
        table.add("效果: $effect").color(remainsColor).pad(5f).fontScale(1.3f).wrap().grow()
      }.marginLeft(9f).grow().row()
      customTable.add(description).grow().wrap().pad(5f).color(remainsColor).row()
      it.add(customTable).grow().row()
    }
  }

  override var localizedName: String = ""

  override var description: String = ""

  override var details: String = ""

  fun rebuildEnableRemains(table: Table) {
    table.button(icon, buttonStyle) {
      setEnabled(false)
      ISounds.remainUninstall.play(UI.sfxVolume + 1)
      RemainsDialog.flunRemains()
    }.disabled {
      disabled()
    }.size(60f).pad(10f).itooltip(localizedName).get().hovered {
      if (RemainsDialog.tempRemain != this) {
        RemainsDialog.tempRemain = this
        RemainsDialog.flunRemains()
      }
    }
  }

  fun rebuildRemains(table: Table) {
    val button = ImageButton(icon, IStyles.button)
    button.clicked {
      if (getEnableds().size < RemainsDialog.slotPos) {
        setEnabled(true)
        ISounds.remainInstall.play()
        RemainsDialog.flunRemains()
      }
    }

    button.resizeImage(32f)


    table.add(button).disabled {
      disabled()
    }.size(60f).pad(10f).itooltip(localizedName).get()
    button.hovered {
      if (RemainsDialog.tempRemain != this) {
        RemainsDialog.tempRemain = this
        RemainsDialog.flunRemains()
      }
    }
  }
}