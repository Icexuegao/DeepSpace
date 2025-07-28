package ice.ui.dialog

import arc.flabel.FLabel
import ice.music.IceMusics
import ice.ui.*
import ice.vars.SettingValue
import mindustry.ui.Styles

object SettingDialog {
    val cont = MenusDialog.cont
    fun show() {
        cont.pane(Styles.noBarPane) { it ->
            it.addLine("音乐")
            addBar(it) { IceMusics.title.position / 168f }.padTop(10f).row()
            addCheckBox(it, "主菜单音乐", SettingValue::menuMusic) { box ->
                SettingValue.menuMusic = box.isChecked
            }.row()
            addIceSlider(it, "主菜单音乐音量", 0f, 10f, 0.1f, SettingValue.menuMusicVolume) {
                SettingValue.menuMusicVolume = it
            }.row()

            it.addLine("游戏")
            addCheckBox(it, "显示星球区块id", SettingValue::planetSectorId) {
                SettingValue.planetSectorId = it.isChecked
            }.row()

            addCheckBox(it, "调试", SettingValue::debugMode) {
                SettingValue.debugMode = it.isChecked
            }.row()
            addIceSlider(it, "视野最大缩放", 0f, 40f, 0.1f, SettingValue.maxZoom) {
                SettingValue.maxZoom = it
            }.tooltip("数值越大,放大能看到的越少").row()
            addIceSlider(it, "视野最小缩放", 0.1f, 1.5f, 0.1f, SettingValue.minZoom) {
                SettingValue.minZoom = it
            }.tooltip("数值越小,缩小能看到的越多").row()
            it.addLine("模式")
            val fLabel = FLabel(SettingValue.difficulty.bun).also { it.setColor(SettingValue.difficulty.color) }
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