package ice.ui.menusDialog

import arc.Core
import arc.graphics.Color
import arc.scene.style.Drawable
import arc.scene.ui.Button
import arc.scene.ui.Image
import arc.scene.ui.layout.Cell
import arc.scene.ui.layout.Stack
import arc.scene.ui.layout.Table
import arc.struct.Seq
import arc.util.Scaling
import ice.DeepSpace
import ice.graphics.IStyles
import ice.graphics.IceColor
import ice.library.IFiles
import ice.library.scene.ui.addCR
import ice.library.scene.ui.addLine
import ice.library.scene.ui.iPaneG
import ice.library.scene.ui.iTable
import ice.library.scene.ui.itooltip
import ice.ui.dialog.BaseMenusDialog
import ice.ui.menusDialog.ModInfoDialog.getQQImage
import ice.world.meta.IceStats
import mindustry.gen.Icon
import mindustry.gen.Tex
import mindustry.graphics.Pal
import singularity.graphic.SglDrawConst
import singularity.ui.SglStyles

object SponsoredDialog : BaseMenusDialog(IceStats.捐赠.localized(), IStyles.menusButton_contribute) {
  init {
    SponsoredTable("小笨喵∽", "3881959748", 20f).itooltip("希望大家多赞助赞助作者")
    SponsoredTable("minphea∽", "3757625379", 30f).itooltip("加入对立神教吧!")
    SponsoredTable("一无柠檬汁", "3591484752", 2f).itooltip("灌注alon喵")
  }

  override fun build(cont: Table) {
    cont.iPaneG { p ->
      p.defaults().growX().pad(5f)
      p.add(IceStats.支持详情.localized()).color(IceColor.b4).growX().wrap()
      p.row()
      p.addLine()
      p.row()
      p.image(SglDrawConst.sgl2).scaling(Scaling.fit).size(365f)
      p.row()
      p.add(IceStats.支持github.localized()).color(IceColor.b4).growX().wrap()
      p.row()
      buildButton(p, Icon.github, Pal.accent, "GitHub", IceStats.支持githubStar.localized()) { Core.app.openURI(DeepSpace.githubProjectUrl) }
      p.row()
      p.add(IceStats.支持捐赠.localized()).color(IceColor.b4).padTop(20f).growX().wrap()
      p.row()
      buildButton(p, Icon.none, Pal.reactorPurple, IceStats.爱发电.localized(), IceStats.支持爱发电.localized()) {}
      p.row()
      buildButton(p, Icon.none, Pal.lancerLaser, IceStats.Patreon.localized(), IceStats.支持patreon.localized()) {}.padBottom(10f).row()


      p.addLine("亲爱的赞助者").padBottom(20f)
      p.iTable { itable ->

        itable.table(SglDrawConst.grayUIAlpha) { table ->
          table.defaults().pad(8f)
          table.table(Tex.underline) { t ->
            t.left().defaults().left().fill()
            t.addCR("感谢愿意支持DeepSpace的赞助者们,每一份赞助都是对我们最大的鼓励,我们铭记在心")
          }.growX().row()

          table.iTable { cons ->
            cons.defaults().pad(4f)
            cons.left()
            cons.setRowsize(5)
            SponsoredTable.sponsoreds.sort { it, it2 ->
              it2.amount.compareTo(it.amount)
            }
            SponsoredTable.sponsoreds.forEach { assisted ->
              cons.table(IFiles.createNinePatch("contributors")) { t ->
                t.top().defaults().center().top().pad(16f).padTop(12f)
                t.add(assisted).fillY()
                t.row()
              }.fillY()
            }
          }
          table.row()
        }.grow().margin(8f).row()

      }.padBottom(20f).row()

    }
  }

  fun buildButton(table: Table, icon: Drawable, color: Color, name: String?, subText: String?, listener: Runnable?): Cell<Button> {
    return table.button({ b: Button? ->
      b!!.left().defaults().left().padBottom(-12f)
      b.table { img ->
        img.image().growY().width(30f).color(color)
        img.row()
        img.image().height(6f).width(30f).color(color.cpy().mul(0.8f, 0.8f, 0.8f, 1f))
      }.growY().fillX().padLeft(-12f)

      b.table(Tex.buttonEdge3) { i -> i.image(icon).size(32f) }.size(64f)
      b.table { t ->
        t.defaults().left().growX()
        t.add(name).color(Pal.accent)
        t.row()
        t.add(subText).color(Pal.gray)
      }.grow().padLeft(5f)
    }, SglStyles.underline, listener)
  }

  private class SponsoredTable(name: String, number: String, val amount: Float) : Table() {
    companion object {
      val sponsoreds = Seq<SponsoredTable>()
    }

    init {
      add(Stack(Image(getQQImage(number)), Image(IFiles.findModPng("wdwd")))).size(180f).get()
      row()

      add(name).color(IceColor.y2).row()
      add("$amount").color(IceColor.y2)


      sponsoreds.add(this)
    }
  }
}