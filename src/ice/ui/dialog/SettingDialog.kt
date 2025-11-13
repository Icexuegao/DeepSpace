package ice.ui.dialog

import arc.flabel.FLabel
import ice.library.meta.stat.IceStats
import ice.library.scene.tex.IStyles
import ice.music.IceMusics
import ice.ui.*
import ice.vars.SettingValue
import mindustry.gen.Icon
import mindustry.ui.Styles
import mindustry.ui.dialogs.PlanetDialog

object SettingDialog: BaseDialog(IceStats.设置.localized(), Icon.filters){

    override fun build() {
        cont.pane(Styles.noBarPane) { it ->
            it.addLine("音乐")
            it.addProgressBar(IStyles.pa1) { IceMusics.title.position / 168f }.padTop(10f).row()
            it.addCheckBox("主菜单音乐", SettingValue::menuMusic) { box ->
                SettingValue.menuMusic = box.isChecked
            }.row()
            it.addIceSlider("主菜单音乐音量", 0f, 10f, 0.1f, SettingValue.menuMusicVolume) {
                SettingValue.menuMusicVolume = it
            }.row()

            it.addLine("游戏")
            it.addCheckBox("显示星球区块id", SettingValue::planetSectorId) {
                SettingValue.planetSectorId = it.isChecked
            }.row()

            it.addCheckBox("调试", SettingValue::debugMode) {
                SettingValue.debugMode = it.isChecked
            }.row()
            it.addCheckBox("星球区块调试",  PlanetDialog::debugSelect){
                PlanetDialog.debugSelect=it.isChecked
            }.row()

            it.addIceSlider("视野最大缩放", 0f, 40f, 0.1f, SettingValue.maxZoom) {
                SettingValue.maxZoom = it
            }.itooltip("数值越大,放大能看到的越少").row()
            it.addIceSlider("视野最小缩放", 0.1f, 1.5f, 0.1f, SettingValue.minZoom) {
                SettingValue.minZoom = it
            }.itooltip("数值越小,缩小能看到的越多").row()
            it.addLine("模式")
            val fLabel = FLabel(SettingValue.difficulty.bun).also { it.setColor(SettingValue.difficulty.color) }
            it.table {
                it.add(fLabel)
            }.growX().pad(5f).row()
            it.table { it2 ->
                SettingValue.ModeDifficulty.entries.forEach { mod ->
                    it2.addCheckBox(mod.na, mod.color, { SettingValue.difficulty == mod }) {
                        SettingValue.difficulty = mod
                        fLabel.restartR(mod.bun).setColor(mod.color)
                    }
                }
            }.growX().row()
        }.grow()
    }
}