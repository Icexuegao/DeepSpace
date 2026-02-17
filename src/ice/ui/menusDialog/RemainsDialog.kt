package ice.ui.menusDialog

import arc.scene.ui.Image
import arc.scene.ui.Label
import arc.scene.ui.layout.Table
import arc.struct.Seq
import arc.util.Scaling
import ice.content.Remainss
import ice.graphics.IStyles
import ice.graphics.IceColor
import ice.library.IFiles
import ice.library.scene.ui.addLine
import ice.library.scene.ui.iTable
import ice.library.scene.ui.iTableG
import ice.library.scene.ui.icePane
import ice.type.Remains
import ice.ui.dialog.BaseMenusDialog
import ice.world.meta.IceStats

object RemainsDialog : BaseMenusDialog(IceStats.遗物.localized(), IStyles.menusButton_remains) {
  val remainsSeq = Seq<Remains>()
  val enableSeq = Seq<Remains>()
  var tempRemain = Remainss.娜雅的手串
  lateinit var tiTleTable: Table
  lateinit var enableTable: Table
  lateinit var remainsTable: Table
  var slotPos: Int = 8

  override fun build(cont: Table) {
    cont.table {
      it.add(Image(IStyles.remains, Scaling.fit))
    }.row()
    cont.iTableG { cont1 ->

      cont1.iTable { ta ->
        ta.table { gh ->
          gh.table(IFiles.createNinePatch("adwdddqddw")) {
            it.add(Label {
              "正在生效: ${enableSeq.size} / $slotPos"
            }).color(IceColor.b4)
          }.margin(5f).marginLeft(20f).marginRight(20f).pad(10f).row()
          gh.iTable {
            it.setRowsize(4)
            enableTable = it
          }
        }.row()
        ta.iTable { gh ->
          gh.image(IStyles.whiteui).color(IceColor.b1).height(3f).growX().row()
          gh.iTable {
            it.top()
            it.add("已拥有:").color(IceColor.b4).pad(10f).row()
            it.icePane { ip ->
              ip.setRowsize(7)
              remainsTable = ip
            }
          }
        }
      }.expand().top()

      cont1.iTable { tiTle ->
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
    tiTleTable.image(tempRemain.icon).size(120f).pad(30f).padTop(0f).row()

    tiTleTable.table {

      it.table(IFiles.createNinePatch("Uwdwdqddw")) { it1 ->
        it1.add("遗物").color(IceColor.b4).expandX().left().padLeft(4f)
      }.width(100f).height(30f).color(IceColor.b6).expandX().left().row()
      it.add(tempRemain.name).fontScale(1.5f).pad(5f).padLeft(0f).color(tempRemain.color).expandX().left().row()
    }.grow().row()

    tiTleTable.addLine().pad(3f)
    tiTleTable.table {
      it.add("效果: ${tempRemain.effect}").pad(5f).fontScale(1.3f).wrap().color(tempRemain.color).grow()
    }.marginLeft(9f).grow().row()
    tiTleTable.add(tempRemain.customTable).grow().row()
  }

  private fun flunRemainsSeq() {
    remainsTable.clearChildren()
    remainsSeq.forEach { item ->
      item.rebuildRemains(remainsTable)
    }
  }

  private fun flunEnableSeq() {
    enableTable.clearChildren()
    enableSeq.forEach { item ->
      item.rebuildEnableRemains(enableTable)
    }
    (1..(slotPos - enableSeq.size)).forEach { _ ->
      enableTable.add(Image(IStyles.button.up)).size(60f).pad(10f)
    }
  }
}