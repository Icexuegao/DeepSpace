package ice.ui

import arc.flabel.FLabel
import arc.func.Boolp
import arc.func.Cons
import arc.func.Floatc
import arc.func.Floatp
import arc.graphics.Color
import arc.scene.Action
import arc.scene.Element
import arc.scene.Group
import arc.scene.actions.Actions
import arc.scene.event.EventListener
import arc.scene.style.Drawable
import arc.scene.style.TextureRegionDrawable
import arc.scene.ui.*
import arc.scene.ui.ScrollPane.ScrollPaneStyle
import arc.scene.ui.TextButton.TextButtonStyle
import arc.scene.ui.layout.Cell
import arc.scene.ui.layout.Stack
import arc.scene.ui.layout.Table
import ice.library.scene.element.IceScrollPane
import ice.library.scene.element.ProgressBar
import ice.library.scene.texs.Colors
import ice.library.scene.texs.IStyles
import ice.library.scene.texs.Texs
import ice.library.scene.ui.layout.ITable
import ice.library.struct.getT
import ice.library.util.toStringi
import mindustry.ui.Styles

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

fun Table.iPaneGY(back: Drawable? = null, consumer: Cons<ITable>): Cell<IceScrollPane> {
    return iPane(back, consumer).growY()
}

fun Table.iPaneGX(back: Drawable? = null, consumer: Cons<ITable>): Cell<IceScrollPane> {
    return iPane(back, consumer).growX()
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

fun <T : Element> T.setPositions(x: Float, y: Float): T {
    setPosition(x, y)
    return this
}

fun slider(min: Float, max: Float, step: Float, defvalue: Float, onUp: Boolean, listener: Floatc?): Slider {
    val slider = Slider(min, max, step, false)
    slider.value = defvalue
    if (listener != null) {
        if (!onUp) {
            slider.moved(listener)
        } else {
            slider.released { listener[slider.value] }
        }
    }
    return slider
}

fun addIceSlider(
    table: Table, name: String, min: Float, max: Float, setpSize: Float, value: Float, valueFloatc: Floatc
): Cell<Stack> {
    val slider = Slider(min, max, setpSize, false)
    slider.value = value
    slider.style = Texs.defaultSlider
    slider.moved(valueFloatc)
    val t2 = Table()
    val fLabel = FLabel(name)
    slider.changed {
        fLabel.restart("$name:${slider.value.toStringi(2)}")
    }
    fLabel.setColor(Colors.b3)
    t2.add(fLabel)
    t2.marginLeft(30f).marginRight(30f)
    return table.add(Stack(t2, slider)).size(400f, 45f).expandX().left().pad(5f)
}

fun addBar(table: Table, fraction: Floatp): Cell<ProgressBar> {
    return table.add(ProgressBar(IStyles.pa1, fraction))
}

fun Table.addLine(name: String?=null){
    name?.let {
        add(it).color(Colors.b1).row()
    }
    add(Image(Texs.whiteui)).color(Colors.b1).height(3f).growX().row()
}


fun addLinet(table: Table, name: String, color: Color) {
    table.table {
        it.add(name).color(color).row()
        it.add(Image(Texs.whiteui)).color(color).height(3f).growX().row()
    }.growX().row()
}


fun addCheckBox(table: Table, name: String, checked: Boolp, run: Cons<CheckBox>): Cell<CheckBox> {
    val button = CheckBox(name, Texs.checkBoxStyle).apply {
        isChecked = checked.get()
        imageCell.size(32f, 44f).expand().left()
        changed {
            run.get(this)
        }
        update {
            isChecked = checked.get()
        }
    }
    return table.add(button).margin(10f).top().left().pad(5f)
}

fun addCleanButton(table: Table, name: String, run: Runnable): Cell<Table> {
    val button = Button(TextureRegionDrawable(Texs.buttonUp), TextureRegionDrawable(Texs.buttonDown))
    button.changed(run::run)
    val table1 = Table()
    table1.add(button)
    table1.add(FLabel(name).colorR(Colors.b1)).padLeft(4f)
    return table.add(table1).margin(10f).pad(5f)
}

fun addCheckBox(table: Table, name: String, color: Color, checked: Boolp, run: Cons<CheckBox>): Cell<CheckBox> {
    val button = CheckBox(name, Texs.checkBoxStyle).apply {
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
    return table.add(button).margin(10f).top().left().pad(5f)
}