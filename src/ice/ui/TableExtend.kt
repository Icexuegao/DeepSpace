package ice.ui

import arc.flabel.FLabel
import arc.func.*
import arc.graphics.Color
import arc.math.Mathf
import arc.scene.Action
import arc.scene.Element
import arc.scene.Group
import arc.scene.actions.Actions
import arc.scene.event.EventListener
import arc.scene.style.Drawable
import arc.scene.ui.*
import arc.scene.ui.ScrollPane.ScrollPaneStyle
import arc.scene.ui.TextButton.TextButtonStyle
import arc.scene.ui.layout.Cell
import arc.scene.ui.layout.Stack
import arc.scene.ui.layout.Table
import arc.struct.Seq
import ice.library.scene.element.IceScrollPane
import ice.library.scene.element.ProgressBar
import ice.library.scene.layout.ProgressAttribute
import ice.library.scene.tex.Colors
import ice.library.scene.tex.IStyles
import ice.library.scene.tex.IStyles.background22
import ice.library.scene.ui.layout.ITable
import ice.library.struct.asDrawable
import ice.library.struct.getT
import ice.library.util.toStringi
import mindustry.Vars
import mindustry.ctype.UnlockableContent
import mindustry.gen.Icon
import mindustry.gen.Tex
import mindustry.ui.Styles
import mindustry.world.Block
import java.util.*

fun Table.iTable(back: Drawable? = null, cons: Cons<ITable>): Cell<ITable> {
    return add(cons.getT(ITable(back)))
}

fun Table.iTableG(back: Drawable? = null, cons: Cons<ITable>): Cell<ITable> {
    return add(cons.getT(ITable(back))).grow()
}

fun Table.iTableGX(back: Drawable? = null, cons: Cons<ITable>): Cell<ITable> {
    return add(cons.getT(ITable(back))).growX()
}

fun Table.iTableGY(back: Drawable? = null, cons: Cons<ITable>): Cell<ITable> {
    return add(cons.getT(ITable(back))).growY()
}

fun Table.iPane(back: Drawable? = null, consumer: Cons<ITable>): Cell<IceScrollPane> {
    val table = ITable(back)
    val pane = IceScrollPane(consumer.getT(table))
    pane.setClip(true)
    pane.setOverscroll(overscrollX = false, overscrollY = false)
    return add(pane)
}

fun Table.iPaneG(back: Drawable? = null, consumer: Cons<ITable>): Cell<IceScrollPane> {
    return iPane(back, consumer).grow()
}

fun <T : Element> T.actionsR(vararg action: Action): T {
    addAction(Actions.sequence(*action))
    return this
}

fun <T : Element> T.colorR(color: Color): T {
    setColor(color)
    return this
}

fun <T : Group> T.clearR(): T {
    clear()
    return this
}

fun <T : FLabel> T.restartR(newText: CharSequence): T {
    restart(newText)
    return this
}

fun Table.imageButton(
    text: String, image: Drawable, style: TextButtonStyle, imagesize: Float, clicked: Runnable = Runnable {}
): Cell<TextButton> {
    val button = TextButton(text, style)
    button.image(image).size(imagesize)
    button.cells.reverse()
    button.clicked(clicked)
    return add(button).marginLeft(6f)
}

fun Table.icePane(back: Drawable? = null, consumer: Cons<Table>): Cell<IceScrollPane> {
    val table = Table()
    if (back != null) table.background = back
    consumer.get(table)
    val pane = IceScrollPane(table)
    pane.setClip(true)
    pane.setOverscroll(overscrollX = false, overscrollY = false)
    return add(pane)
}

fun Table.icePane(
    style: ScrollPaneStyle = Styles.noBarPane, cont: Table, pane: IceScrollPane.() -> Unit
): Cell<IceScrollPane> {
    val iceScrollPane = IceScrollPane(cont, style)
    pane.invoke(iceScrollPane)
    return add(iceScrollPane)
}

fun Element.addListeners(listener: EventListener): Element {
    addListener(listener)
    return this
}

fun <T : Element> T.itooltip(string: String): T {
    addListener(Tooltip { tool ->
        tool.background(IStyles.background32).margin(20f)
        tool.add(string, Colors.b4)
    })
    return this
}

fun <T : Element> Cell<T>.itooltip(string: String): Cell<T> {
    get().itooltip(string)
    return this
}

fun <T : Element> T.setPositions(x: Float, y: Float): T {
    setPosition(x, y)
    return this
}

fun Table.addIceSlider(
    name: String, min: Float, max: Float, setpSize: Float, value: Float, valueFloatc: Floatc
): Cell<Stack> {
    val slider = Slider(min, max, setpSize, false)
    slider.value = value
    slider.style = IStyles.defaultSlider
    slider.moved(valueFloatc)
    val t2 = Table()
    val fLabel = FLabel(name)
    slider.changed {
        fLabel.restart("$name:${slider.value.toStringi(2)}")
    }
    fLabel.setColor(Colors.b3)
    t2.add(fLabel)
    t2.marginLeft(30f).marginRight(30f)
    return add(Stack(t2, slider)).size(400f, 45f).expandX().left().pad(5f)
}

fun Table.addProgressBar(attribute: ProgressAttribute, fraction: Floatp): Cell<ProgressBar> {
    return add(ProgressBar(attribute, fraction))
}

fun Table.addLine(name: String? = null, color: Color = Colors.b1) {
    table {
        name?.let { n -> add(n).color(color).row() }
        it.add(Image(IStyles.whiteui)).color(color).height(3f).growX().row()
    }.growX().row()
}

fun Table.addCheckBox(name: String, checked: Boolp, run: Cons<CheckBox>): Cell<CheckBox> {
    val button = CheckBox(name, IStyles.checkBoxStyle).apply {
        isChecked = checked.get()
        imageCell.size(32f, 44f).expand().left()
        changed {
            run.get(this)
        }
        update {
            isChecked = checked.get()
        }
    }
    return add(button).margin(10f).top().left().pad(5f)
}

fun Table.addCheckBox(name: String, color: Color, checked: Boolp, run: Cons<CheckBox>): Cell<CheckBox> {
    val button = CheckBox(name, IStyles.checkBoxStyle).apply {
        label.setColor(color)
        isChecked = checked.get()
        imageCell.size(32f, 44f).expand().left()
        changed {
            run.get(this)
        }
        update {
            isChecked = checked.get()
        }
    }
    return add(button).margin(10f).top().left().pad(5f)
}

object ItemSelection {
    private var search: TextField? = null
    fun <T : UnlockableContent?> buildTable(
        block: Block?,
        table: Table,
        items: Seq<T>,
        holder: Prov<T>,
        consumer: Cons<T>,
        closeSelect: Boolean = false,
        rows: Int = 4,
        columns: Int = 6
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
                }.itooltip("${item.localizedName}\n${item.name}").get().apply {
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