package ice.ui.menus

import arc.flabel.FLabel
import arc.scene.ui.layout.Table
import ice.music.IceMusics
import ice.ui.TableExtend.addBar
import ice.ui.TableExtend.addCheckBox
import ice.ui.TableExtend.addIceSlider
import ice.ui.TableExtend.addLine
import ice.ui.TableExtend.restartR
import ice.ui.tex.Colors
import mindustry.ui.Styles

object SettingDialog {

    fun set(table: Table) {
        rebuild(table)
    }

    private fun rebuild(cont: Table) {
        cont.pane(Styles.noBarPane) { it ->

            addLine(it, "音乐")
            addBar(it, "", Colors.b2, { IceMusics.getPosition("title") / 168f }) {}.size(300f, 40f).padTop(30f).row()
            addCheckBox(it, "主菜单音乐", SettingValue::getMenuMusic) { box ->
                SettingValue.menuMusic(box.isChecked)
            }.row()
            addIceSlider(
                it, "主菜单音乐音量", 0f, 10f, 0.1f, SettingValue.getMenuMusicVolume(), SettingValue::setTitleVolume
            ).row()

            addLine(it, "游戏")
            addCheckBox(it, "显示星球区块id", SettingValue::getPlanetSectorId) {
                SettingValue.planetSectorId(it.isChecked)
            }.row()
            addCheckBox(it, "调试", SettingValue::getDebugMode) {
                SettingValue.setDebugMode(it.isChecked)
            }.row()
            addIceSlider(it, "视野最大缩放", 0f, 40f, 0.1f, SettingValue.getMaxZoom()) {
                SettingValue.setMaxZoomLim(it)
            }.tooltip("数值越大,放大能看到的越少").row()
            addIceSlider(it, "视野最小缩放", 0.1f, 1.5f, 0.1f, SettingValue.getMinZoom()) {
                SettingValue.setMinZoomLim(it)
            }.tooltip("数值越小,缩小能看到的越多").row()

            addLine(it, "模式")

            val fLabel = FLabel(SettingValue.difficulty.bun)
            fLabel.setColor(SettingValue.difficulty.color)
            it.table {
                it.add(fLabel)
            }.growX().pad(5f).row()
            it.table { it2 ->
                SettingValue.ModeDifficulty.entries.forEach { mod ->
                    addCheckBox(it2, mod.na, mod.color, { SettingValue.difficulty == mod }) {
                        SettingValue.difficulty = mod
                        fLabel.restartR(mod.bun).setColor(mod.color)
                    }
                }
            }.growX().row()
        }.grow()
    }
}