package ice.ui.menus

import arc.flabel.FLabel
import arc.func.Boolp
import arc.func.Cons
import arc.func.Floatc
import arc.func.Floatp
import arc.graphics.Color
import arc.scene.ui.CheckBox
import arc.scene.ui.Image
import arc.scene.ui.Slider
import arc.scene.ui.layout.Cell
import arc.scene.ui.layout.Stack
import arc.scene.ui.layout.Table
import ice.graphics.IceColors
import ice.library.IceString
import ice.music.IceMusics
import ice.scene.element.IceBar
import ice.ui.tex.Bundle
import ice.ui.tex.IceTex
import mindustry.ui.Styles
import java.util.concurrent.atomic.AtomicBoolean

object SettingDialog {

    fun set(table: Table) {
        rebuild(table)
    }

    private fun rebuild(cont: Table) {
        cont.clear()
        cont.pane(Styles.noBarPane) { it ->

            addLine(it, "音乐")
            addBar(it, "音乐播放中...", IceColors.b3, { IceMusics.get("title").position / 168f }) { b ->
                if (IceMusics.get("title").isPlaying) b.name = "音乐播放中..." else b.name = "音乐暂停"
            }.size(300f, 40f).pad(20f).row()
            addCheckBox(it, "主菜单音乐", { SettingValue.menuMusic }) { box ->
                SettingValue.menuMusic(box.isChecked)
            }.row()
            addSlider(it, "主菜单音乐音量", IceColors.b3, 0f, 10f, 0.1f, SettingValue.menuMusicVolume) {
                SettingValue.setTitleVolume(it)
            }.row()

            addLine(it, "游戏")
            addCheckBox(it, "显示星球区块id", { SettingValue.shown }) { box ->
                SettingValue.planetSectorId(box.isChecked)
            }.row()
            addSlider(it, "视野最大缩放", IceColors.b3, 0f, 40f, 0.1f, SettingValue.maxZoom) {
                SettingValue.setMaxZoomLim(it)
            }.tooltip("数值越大,放大能看到的越少").row()
            addSlider(it, "视野最小缩放", IceColors.b3, 0.1f, 1.5f, 0.1f, SettingValue.minZoom) {
                SettingValue.setMinZoomLim(it)
            }.tooltip("数值越小,缩小能看到的越多").row()

            addLine(it, "模式")
            it.table { it2 ->
                val fLabel = FLabel("1111111")
                it2.table { it3 ->
                    val f = AtomicBoolean(false)
                    val f1 = AtomicBoolean(false)
                    val f2 = AtomicBoolean(false)
                    val b = arrayOf(f, f1, f2)
                    complexity(it3, "神赐", Color.yellow, { f.get() }) {
                        f.set(!f.get())
                        if (f.get()) {
                            b.forEach { b1 ->
                                if (b1 != f) {
                                    b1.set(!b1.get())
                                }
                            }

                            fLabel.restart(Bundle.easy)
                            fLabel.setColor(it.style.fontColor)
                        } else {
                            fLabel.restart(Bundle.general)
                            fLabel.setColor(Color.gray)
                        }
                    }.expandX()

                    complexity(it3, "洗礼", IceColors.b1, { f1.get() }) {
                        f1.set(!f1.get())
                        if (f1.get()) {
                            b.forEach { b1 ->
                                if (b1 != f1) {
                                    b1.set(!b1.get())
                                }
                            }
                            fLabel.restart(Bundle.general)
                            fLabel.setColor(it.style.fontColor)
                        } else {
                            fLabel.restart(Bundle.general)
                            fLabel.setColor(Color.gray)
                        }

                    }.expandX()

                    complexity(it3, "棘罪", Color.red, { f2.get() }) {
                        f2.set(!f2.get())
                        if (f2.get()) {
                            b.forEach { b1 ->
                                if (b1 != f2) {
                                    b1.set(!b1.get())
                                }
                            }
                            fLabel.restart(Bundle.hard)
                            fLabel.setColor(it.style.fontColor)
                        } else {
                            fLabel.restart(Bundle.general)
                            fLabel.setColor(Color.gray)
                        }
                    }.expandX()
                }.left().row()

                it2.table { it3 ->
                    it3.add(fLabel).width(325f)
                }.left()
            }.size(350f, 100f).row()
        }.grow()
    }

    private fun addLine(table: Table, name: String) {
        table.add(name).color(IceColors.b1).row()
        table.add("").update {
            it.name = "${table.width}"
        }.row()
        table.add(Image(IceTex.whiteui)).color(IceColors.b1).height(3f).growX().row()
    }

    private fun addCheckBox(table: Table, name: String, checked: Boolp, run: Cons<CheckBox>): Cell<CheckBox> {
        val button = CheckBox(name, IceTex.checkBoxStyle).apply {
            imageCell.size(32f, 44f).expand().left()
            changed {
                run.get(this)
            }
            update {
                isChecked = checked.get()
            }
        }//每次加载初始化
        button.isChecked = checked.get()
        val add = table.add(button)
        add.margin(10f).top().left().pad(5f)
        return add
    }

    private fun complexity(
        table: Table, name: String, color: Color, checked: Boolp, run: Cons<CheckBox>
    ): Cell<CheckBox> {

        val checkBoxStyle = IceTex.getComplexityStyle()
        val button = CheckBox(name, checkBoxStyle).apply {
            isChecked = checked.get()
            imageCell.size(32f, 44f).expand().left()
            changed {
                if (isChecked) checkBoxStyle.fontColor = color else checkBoxStyle.fontColor = Color.gray
                run.get(this)
            }
            update {
                isChecked = checked.get()
            }
        }//每次加载初始化

        val margin = table.add(button)
        margin.margin(10f).expandX().top().pad(5f)
        return margin
    }

    private fun addBar(table: Table, name: String, color: Color, fraction: Floatp, b: Cons<IceBar>): Cell<IceBar> {
        return table.add(IceBar(name, color, fraction).update { bar ->
            b.get(bar)
        })
    }

    private fun addSlider(
        table: Table,
        name: String,
        color: Color,
        min: Float,
        max: Float,
        setpSize: Float,
        value: Float,
        valueFloatc: Floatc
    ): Cell<Stack> {
        val slider = Slider(min, max, setpSize, false)
        slider.setValue(value)
        slider.setStyle(IceTex.defaultSlider)
        slider.moved(valueFloatc)
        val t2 = Table()
        val fLabel = FLabel(name)
        slider.changed {
            fLabel.restart("$name:${IceString.decimalFormat(slider.value,2)}")
        }
        fLabel.setColor(color)
        t2.add(fLabel)
        t2.marginLeft(30f).marginRight(30f)
        return table.add(Stack(t2, slider)).size(400f, 45f).expandX().left().pad(5f)
    }
}