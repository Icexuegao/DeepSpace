package ice.ui

import arc.func.Cons
import arc.func.Prov
import arc.math.Mathf
import arc.scene.ui.TextField
import arc.scene.ui.layout.Table
import arc.struct.Seq
import ice.library.scene.texs.Texs.background22
import ice.library.scene.ui.layout.ITable
import ice.library.struct.asDrawable
import mindustry.Vars
import mindustry.ctype.UnlockableContent
import mindustry.gen.Icon
import mindustry.gen.Tex
import mindustry.ui.Styles
import mindustry.world.Block
import java.util.*

object ItemSelection {
    private var search: TextField? = null
    fun <T : UnlockableContent?> buildTable(
        block: Block?, table: Table, items: Seq<T>, holder: Prov<T>, consumer: Cons<T>
    ) {
        buildTable(block, table, items, holder, consumer, false, 4, 6)
    }

    fun <T : UnlockableContent?> buildTable(
        block: Block?,
        table: Table,
        items: Seq<T>,
        holder: Prov<T>,
        consumer: Cons<T>,
        closeSelect: Boolean,
        rows: Int,
        columns: Int
    ) {
        val cont = ITable()
        cont.top()
        cont.defaults().size(50f)
        cont.setRowsize(columns)

        search?.clearText()
        val rebuild = {
            cont.clearChildren()
            val text = if (search != null) search!!.text else ""
            val list = items.select { u: T ->
                (text.isEmpty() || u!!.localizedName.lowercase(Locale.getDefault())
                    .contains(text.lowercase(Locale.getDefault())) || u.name.contains(text))
            }

            list.forEach { item ->
                item!!
                cont.button(Tex.whiteui, Styles.clearNoneTogglei, Mathf.clamp(item.selectionSize, 0f, 40f)) {
                    if (closeSelect) Vars.control.input.config.hideConfig()
                }.tooltip("${item.localizedName}\n${item.name}").get().apply {
                    style.imageUp = item.uiIcon.asDrawable()
                    changed { consumer[if (isChecked) item else null] }
                    update { isChecked = holder.get() === item }
                }
            }
        }

        rebuild()
        val main = Table(background22)
        main.table { search: Table ->
            search.image(Icon.zoom).padLeft(4f)
            this.search = search.field(null) { _: String -> rebuild() }.padBottom(4f).left().growX().get()
            this.search?.messageText = "@players.search"
        }.fillX().margin(13f).row()

        main.icePane(Styles.noBarPane, cont) {
            if (block != null) {
                setScrollYForce(block.selectScroll)
                update { block.selectScroll = getScrollY() }
            }
        }.maxHeight(50f * rows)
        table.top().add(main).margin(10f)
    }
}