package ice.ui.dialog

import arc.scene.ui.Image
import arc.scene.ui.Label
import arc.scene.ui.layout.Table
import arc.struct.Seq
import ice.content.Remainss
import ice.graphics.IStyles
import ice.graphics.IceColor
import ice.library.scene.ui.iTable
import ice.library.scene.ui.iTableG
import ice.library.scene.ui.icePane
import ice.type.Remains
import ice.world.meta.IceStats
import mindustry.gen.Icon

object RemainsDialog : BaseMenusDialog(IceStats.遗物.localized(), Icon.logic) {
    val remainsSeq = Seq<Remains>()
    val enableSeq = Seq<Remains>()
    var tempRemain = Remainss.娜雅的手串
    lateinit var tiTleTable: Table
    lateinit var enableTable: Table
    lateinit var remainsTable: Table
    var slotPos: Int = 4

    override fun build() {
        cont.iTable {
            tiTleTable = it
            flunTiTleTable()
        }.pad(50f).padTop(100f).minHeight(360f).row()

        cont.iTable { ta ->
            ta.add(Label { "正在生效:[${enableSeq.size} / $slotPos]" }).color(IceColor.b4).pad(10f).row()
            ta.iTableG {
                it.setRowsize(5)
                enableTable = it
                flunEnableSeq()
            }
        }.pad(30f).row()
        cont.image(IStyles.whiteui).color(IceColor.b1).height(3f).growX().row()
        cont.iTableG {
            it.top()
            it.add("已拥有:").color(IceColor.b4).pad(10f).row()
            it.icePane { ip ->
                ip.setRowsize(10)
                remainsTable = ip
                flunRemains()
            }
        }
    }

    fun flunRemains() {
        flunTiTleTable()
        flunRemainsSeq()
        flunEnableSeq()
    }

    fun flunTiTleTable() {
        tiTleTable.clearChildren()
        tiTleTable.image(tempRemain.icon).size(90f).pad(10f).row()
        tiTleTable.add(tempRemain.name).pad(5f).color(tempRemain.color).row()
        tiTleTable.add(tempRemain.customTable).row()
        tiTleTable.add("效果: ${tempRemain.effect}").color(tempRemain.color)
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