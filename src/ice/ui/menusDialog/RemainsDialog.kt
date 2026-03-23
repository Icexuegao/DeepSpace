package ice.ui.menusDialog

import arc.scene.ui.Image
import arc.scene.ui.Label
import arc.scene.ui.layout.Table
import arc.util.Scaling
import ice.content.Remainss
import ice.graphics.IStyles
import ice.graphics.IceColor
import ice.library.IFiles
import ice.library.scene.ui.iTable
import ice.library.scene.ui.iTableG
import ice.library.scene.ui.icePane
import ice.type.Remains
import ice.ui.dialog.BaseMenusDialog
import ice.world.meta.IceStats

object RemainsDialog : BaseMenusDialog(IceStats.遗物.localized(), IStyles.menusButton_remains) {

  var tempRemain = Remainss.娜雅的手串
  lateinit var tiTleTable: Table
  lateinit var enableTable: Table
  lateinit var remainsTable: Table
  var slotPos: Int = 8
  override fun init() {
    Remains.remainsSeq.sort {it.level.toFloat()}.reversed().forEach {
      if (it.unlock) {
        it.setEnabled(true)
      }
    }
  }

  override fun build(cont: Table) {
    cont.table {
      it.add(Image(IStyles.remains, Scaling.fit))
    }.row()
    cont.iTableG {cont1 ->

      cont1.iTable {ta ->
        ta.table {gh ->
          gh.table(IFiles.createNinePatch("adwdddqddw")) {
            it.add(Label {
              "正在生效: ${Remains.getEnableds().size} / $slotPos"
            }).color(IceColor.b4)
          }.margin(5f).marginLeft(20f).marginRight(20f).pad(10f).row()
          gh.iTable {
            it.setRowsize(4)
            enableTable = it
          }
        }.growX().row()
        ta.iTable {gh ->
          gh.image(IStyles.whiteui).color(IceColor.b1).height(3f).growX().row()
          gh.iTable {
            it.top()
            it.add("已拥有:").color(IceColor.b4).pad(10f).row()
            it.icePane {ip ->
              ip.setRowsize(7)
              remainsTable = ip
            }
          }
        }.growX()
      }.expand().top()

      cont1.iTable {tiTle ->
        tiTleTable = tiTle
      }.minWidth(400f).expandX().top()

    }.padTop(30f)
    flunRemains()
  }

  fun flunRemains() {
    flunTiTleTable()
    flunRemainsSeq()
    flunEnableSeq()
  }

  fun flunTiTleTable() {
    tiTleTable.clearChildren()
    tiTleTable.add(tempRemain.getTiTleTable()).grow()
  }

  private fun flunRemainsSeq() {
    remainsTable.clearChildren()
    Remains.getNoEnableds().forEach {item ->
      item.rebuildRemains(remainsTable)
    }
  }

  private fun flunEnableSeq() {
    enableTable.clearChildren()
    Remains.getEnableds().forEach {item ->
      item.rebuildEnableRemains(enableTable)
    }
    (1..(slotPos - Remains.getEnableds().size)).forEach {_ ->
      enableTable.add(Image(IStyles.button.up)).size(60f).pad(10f)
    }
  }
}