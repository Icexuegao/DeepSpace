package ice.ui.menus.data

import arc.scene.ui.Image
import arc.scene.ui.layout.Table
import ice.ui.TableExtend.icePane
import ice.ui.TableExtend.rowc
import ice.ui.menus.MenusDialog
import ice.ui.tex.Colors
import ice.ui.tex.IceTex
import mindustry.ctype.ContentType

object DataDialog {
    lateinit var contents: Table
    lateinit var contentInfo: Table
    var content = Button.物品
    fun set(table: Table) {
        rebuild(table)
    }

    private fun rebuild(table: Table) {

        val rowc = table.table { ta ->
            Button.entries.forEach {
                addButton(ta, it.name, it)
            }
        }.height(60f).growX().rowc().table.width - MenusDialog.backMargin * 2
        table.add(Image(IceTex.whiteui)).color(Colors.b1).height(3f).growX().row()

        table.table { ta ->
            ta.icePane { p ->
                contents = p
            }.growY().width(rowc / 3)
            ta.add(Image(IceTex.whiteui)).color(Colors.b1).width(3f).growY()
            ta.icePane { p ->
                contentInfo = p
            }.growY().width(rowc / 3 * 2)
        }.grow()
    }

    private fun addButton(t: Table, name: String, c: Button) {
        t.button(name, IceTex.rootButton) {
            contents.clear()
            content = c
            DataBuildContent.flush()
        }.grow()
    }

    enum class Button(val content: ContentType) {
        物品(ContentType.item),
        流体(ContentType.liquid),
        建筑(ContentType.block),
        状态(ContentType.status),
        单位(ContentType.unit),
        天气(ContentType.weather),
        子弹(ContentType.bullet),
        战役(ContentType.sector),
        星球(ContentType.planet)
    }
}


