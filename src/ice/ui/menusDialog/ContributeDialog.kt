package ice.ui.menusDialog

import arc.Core
import arc.graphics.Color
import arc.scene.style.Drawable
import arc.scene.ui.Button
import arc.scene.ui.layout.Cell
import arc.scene.ui.layout.Table
import arc.util.Scaling
import ice.Ice
import ice.graphics.IStyles
import ice.graphics.IceColor
import ice.library.scene.ui.addLine
import ice.ui.dialog.BaseMenusDialog
import ice.world.meta.IceStats
import mindustry.gen.Icon
import mindustry.gen.Tex
import mindustry.graphics.Pal
import singularity.graphic.SglDrawConst
import singularity.ui.SglStyles

object ContributeDialog : BaseMenusDialog(IceStats.捐赠.localized(), IStyles.menusButton_contribute) {
  override fun build(cont: Table) {

    cont.pane { p ->
      p.defaults().growX().pad(5f)
      p.add(IceStats.支持详情.localized()).color(IceColor.b4).growX().wrap()
      p.row()
      p.addLine()
      p.row()
      p.image(SglDrawConst.sgl2).scaling(Scaling.fit).size(365f)
      p.row()
      p.add(IceStats.支持github.localized()).color(IceColor.b4).growX().wrap()
      p.row()
      buildButton(p, Icon.github, Pal.accent, "GitHub", IceStats.支持githubStar.localized()) { Core.app.openURI(Ice.githubProjectUrl) }
      p.row()
      p.add(IceStats.支持捐赠.localized()).color(IceColor.b4).padTop(20f).growX().wrap()
      p.row()
      buildButton(p, Icon.none, Pal.reactorPurple, IceStats.爱发电.localized(), IceStats.支持爱发电.localized()) {}
      p.row()
      buildButton(p, Icon.none, Pal.lancerLaser, IceStats.Patreon.localized(), IceStats.支持patreon.localized()) {}
    }.growX().fillY()
  }

  fun buildButton(table: Table, icon: Drawable, color: Color, name: String?, subText: String?, listener: Runnable?): Cell<Button> {
  return  table.button({ b: Button? ->
      b!!.left().defaults().left().padBottom(-12f)
      b.table { img ->
        img.image().growY().width(30f).color(color)
        img.row()
        img.image().height(6f).width(30f).color(color.cpy().mul(0.8f, 0.8f, 0.8f, 1f))
      }.growY().fillX().padLeft(-12f)

      b.table(Tex.buttonEdge3) { i -> i.image(icon).size(55f) }.size(64f)
      b.table { t ->
        t.defaults().left().growX()
        t.add(name).color(Pal.accent)
        t.row()
        t.add(subText).color(Pal.gray)
      }.grow().padLeft(5f)
    }, SglStyles.underline, listener)
  }
}