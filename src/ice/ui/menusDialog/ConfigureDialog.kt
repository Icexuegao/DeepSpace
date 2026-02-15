package ice.ui.menusDialog

import arc.Core
import arc.flabel.FLabel
import arc.func.Boolp
import arc.func.Cons2
import arc.graphics.Color
import arc.scene.ui.ImageButton
import arc.scene.ui.layout.Cell
import arc.scene.ui.layout.Table
import ice.audio.IMusics
import ice.audio.ISounds
import ice.core.SettingValue
import ice.entities.ModeDifficulty
import ice.graphics.IStyles
import ice.graphics.IceColor
import ice.library.scene.element.typinglabel.TLabel
import ice.library.scene.ui.addIceSlider
import ice.library.scene.ui.addLine
import ice.library.scene.ui.addProgressBar
import ice.library.scene.ui.iPaneG
import ice.ui.dialog.BaseMenusDialog
import ice.ui.dialog.research.node.UCLinkNode
import ice.world.meta.IceStats
import mindustry.Vars
import mindustry.ui.Styles

object ConfigureDialog : BaseMenusDialog(IceStats.设置.localized(), IStyles.menusButton_configure) {
  override fun build(cont: Table) {
    cont.iPaneG { it ->
      it.addLine("音乐")
      it.addProgressBar(IStyles.pa1) { IMusics.title.position / 168f }.padTop(10f).row()
      it.addBox("启用主菜单音乐", SettingValue::启用主菜单音乐) { box, _ ->
        SettingValue.启用主菜单音乐 = box.isChecked
      }.row()
      it.addIceSlider("主菜单音乐音量", 0f, 10f, 0.1f, SettingValue.menuMusicVolume) {
        SettingValue.menuMusicVolume = it
      }.row()

      it.addLine("游戏")
      it.addBox("启用多合成角标常显", SettingValue::启用多合成角标常显) { box, _ ->
        SettingValue.启用多合成角标常显 = box.isChecked
      }.row()
      it.addBox("启用多合成配方缓存", SettingValue::启用多合成配方缓存) { box, _ ->
        SettingValue.启用多合成配方缓存 = box.isChecked
      }.row()
      it.addBox("启用包裹物品绘制", SettingValue::启用包裹物品绘制) { box, _ ->
        SettingValue.启用包裹物品绘制 = box.isChecked
      }.row()
      it.addBox("启用包裹物品时限", SettingValue::启用包裹物品时限) { box, _ ->
        SettingValue.启用包裹物品时限 = box.isChecked
      }.row()
      it.addBox("启用扭曲效果绘制", SettingValue::启用扭曲效果绘制) { box, _ ->
        SettingValue.启用扭曲效果绘制 = box.isChecked
      }.row()
      it.addIceSlider("视野最大缩放限制", 0f, 40f, 0.1f, SettingValue.视野最大缩放限制) {
        SettingValue.视野最大缩放限制 = it
      }.row()
      it.addIceSlider("视野缩放最小限制", 0.1f, 1.5f, 0.1f, SettingValue.视野缩放最小限制) {
        SettingValue.视野缩放最小限制 = it
      }.row()
      it.addLine("模式")
      val fLabel = TLabel(SettingValue.difficulty.bun).also {
        it.setColor(SettingValue.difficulty.color)
      }
      it.table {
        it.add(fLabel)
      }.growX().pad(5f).row()
      it.table { it2 ->
        ModeDifficulty.entries.forEach { mod ->
          it2.addBox(
            mod.na, { SettingValue.difficulty == mod }, if (SettingValue.difficulty == mod) mod.color else IceColor.b4
          ) { _, la ->
            SettingValue.difficulty = mod
            //  fLabel.restartR(mod.bun).setColor(mod.color)
            fLabel.restart(mod.bun)
            fLabel.setColor(mod.color)
            la.update {
              la.setColor(if (SettingValue.difficulty == mod) mod.color else IceColor.b4)
            }
          }
        }
      }.growX().row()
      it.addLine("调试")
      it.addBox("恢复默认配置", { true }, clean = true) { _, _ ->
        SettingValue.clear()
        Vars.ui.showInfoOnHidden("游戏将退出以重新加载配置") {
          Core.app.exit()
        }
      }.row()
      it.addBox("启用星球区块ID", SettingValue::启用星球区块ID) { box, _ ->
        SettingValue.启用星球区块ID = box.isChecked
      }.row()

      it.addBox("启用调试模式", SettingValue::启用调试模式) { box, _ ->
        SettingValue.启用调试模式 = box.isChecked
      }.row()
      it.addBox("删除科技树所有缓存物品", { true }, clean = true) { _, _ ->
        ResearchDialog.view.links.forEach {
          if (it is UCLinkNode) {
            it.reset()
            ResearchDialog.selectANode = null
          }
        }
      }.row()
      it.addBox("星球区块调试", SettingValue::星球区块调试) { box, _ ->
        SettingValue.星球区块调试 = box.isChecked
      }.row()
    }
  }

  fun Table.addBox(
    name: String, checked: Boolp, color: Color = IceColor.b4, clean: Boolean = false, run: Cons2<ImageButton, FLabel>
  ): Cell<Table> {
    val label = FLabel(name)
    val button = ImageButton(if (clean) IStyles.cleanBoxStyle else IStyles.checkBoxStyle).apply {
      isChecked = checked.get()
      imageCell.size(32f, 44f).expand().left()
      clicked {
        ISounds.remainInstall.play()
        run.get(this, label)
      }
      update {
        isChecked = checked.get()
      }
    }

    return add(Table().apply {
      add(button).padRight(4f)
      add(label.also { it.setColor(color) })
    }).margin(10f).top().left().pad(5f)
  }
}