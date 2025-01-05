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
import arc.scene.ui.TextButton.TextButtonStyle
import arc.scene.ui.layout.Cell
import arc.scene.ui.layout.Stack
import arc.scene.ui.layout.Table
import ice.library.tool.StringTool
import ice.ui.scene.element.IceScrollPane
import ice.ui.scene.element.MusicsBar
import ice.ui.tex.Colors
import ice.ui.tex.IceTex

/**Table扩展类 扩展原版Table方法*/
object TableExtend {

    private fun <T> Cons<T>.getT(t: T): T {
        get(t)
        return t
    }

    annotation class AnnotationTable

    @AnnotationTable
    fun Table.tableG(back: Drawable? = null, cons: Cons<Table>): Cell<Table> {
        return add(cons.getT(Table(back))).grow()
    }

    fun Table.tableGX(back: Drawable? = null, cons: Cons<Table>): Cell<Table> {
        return add(cons.getT(Table(back))).growX()
    }

    fun Table.tableGY(cons: Cons<Table>): Cell<Table> {
        return add(cons.getT(Table())).growY()
    }

    fun Table.tableE(cons: Cons<Table>): Cell<Table> {
        return add(cons.getT(Table())).expand()
    }

    fun Table.tableEX(cons: Cons<Table>): Cell<Table> {
        return add(cons.getT(Table())).expandX()
    }

    fun Table.tableEY(cons: Cons<Table>): Cell<Table> {
        return add(cons.getT(Table())).expandY()
    }


    annotation class AnnotationElement

    @AnnotationElement
    fun <T : Element> T.actionsR(vararg action: Action): T {
        addAction(Actions.sequence(*action))
        return this
    }

    fun <T : Element> T.colorR(color: Color): T {
        setColor(color)
        return this
    }

    annotation class AnnotationGroup

    @AnnotationGroup
    fun <T : Group> T.clearR(): T {
        clear()
        return this
    }

    annotation class AnnotationLabel

    @AnnotationLabel
    fun <T : Label> T.setWrapR(b: Boolean): T {
        setWrap(b)
        return this
    }

    fun <T : FLabel> T.restartR(newText: CharSequence): T {
        restart(newText)
        return this
    }

    annotation class AnnotationCell

    @AnnotationCell
    fun <T : Element> Cell<T>.rowc(): Cell<T> {
        table.row()
        return this
    }

    annotation class AnnotationDefined

    @AnnotationDefined
    fun Table.imageButton(
        text: String, image: Drawable, style: TextButtonStyle, imagesize: Float, clicked: Runnable = Runnable {}
    ): Cell<TextButton> {
        val button = TextButton(text, style)
        button.image(image).size(imagesize)
        button.cells.reverse()
        button.clicked(clicked)
        return add(button).marginLeft(6f)
    }

    fun Table.icePane(consumer: Cons<Table>): Cell<IceScrollPane> {
        val table = Table()
        consumer.get(table)
        val pane = IceScrollPane(table)
        pane.setOverscroll(overscrollX = false, overscrollY = false)
        return add(pane)
    }

    fun Table.icePaneG(consumer: Cons<Table>): Cell<IceScrollPane> {
        return icePane(consumer).grow()
    }


    fun Element.addListeners(listener: EventListener): Element {
        addListener(listener)
        return this
    }

    fun Element.setPositions(x: Float, y: Float): Element {
        setPosition(x, y)
        return this
    }

    fun slider(min: Float, max: Float, step: Float, defvalue: Float, onUp: Boolean, listener: Floatc?): Slider {
        val slider = Slider(min, max, step, false)
        slider.setValue(defvalue)
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
        slider.setValue(value)
        slider.setStyle(IceTex.defaultSlider)
        slider.moved(valueFloatc)
        val t2 = Table()
        val fLabel = FLabel(name)
        slider.changed {
            fLabel.restart("$name:${StringTool.decimalFormat(slider.value, 2)}")
        }
        fLabel.setColor(Colors.b3)
        t2.add(fLabel)
        t2.marginLeft(30f).marginRight(30f)
        return table.add(Stack(t2, slider)).size(400f, 45f).expandX().left().pad(5f)
    }

    fun addBar(table: Table, name: String, color: Color, fraction: Floatp, b: Cons<MusicsBar>): Cell<MusicsBar> {
        return table.add(MusicsBar(name, color, fraction).update { bar ->
            b.get(bar)
        })
    }

    fun addLine(table: Table, name: String) {
        table.add(name).color(Colors.b1).row()
        table.add(Image(IceTex.whiteui)).color(Colors.b1).height(3f).growX().row()
    }

    fun addLinet(table: Table, name: String, color: Color) {
        table.table {
            it.add(name).color(color).row()
            it.add(Image(IceTex.whiteui)).color(color).height(3f).growX().row()
        }.growX().row()
    }

    fun addLinet(table: Table, name: String) {
        table.table {
            it.add(name).color(Colors.b1).row()
            it.add(Image(IceTex.whiteui)).color(Colors.b1).height(3f).growX().row()
        }.grow().row()
    }

    fun addCheckBox(table: Table, name: String, checked: Boolp, run: Cons<CheckBox>): Cell<CheckBox> {
        val button = CheckBox(name, IceTex.checkBoxStyle).apply {
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
        val button = Button(TextureRegionDrawable(IceTex.buttonUp), TextureRegionDrawable(IceTex.buttonDown))
        button.changed(run::run)
        val table1 = Table()
        table1.add(button)
        table1.add(FLabel(name).colorR(Colors.b1)).padLeft(4f)
        return table.add(table1).margin(10f).pad(5f)
    }

    fun addCheckBox(table: Table, name: String, color: Color, checked: Boolp, run: Cons<CheckBox>): Cell<CheckBox> {
        val button = CheckBox(name, IceTex.checkBoxStyle).apply {
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
}