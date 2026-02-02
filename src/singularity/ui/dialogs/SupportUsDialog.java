package singularity.ui.dialogs;

import arc.Core;
import arc.graphics.Color;
import arc.scene.style.Drawable;
import arc.scene.ui.layout.Table;
import arc.util.Scaling;
import mindustry.gen.Icon;
import mindustry.gen.Tex;
import mindustry.graphics.Pal;
import mindustry.ui.dialogs.BaseDialog;
import singularity.Sgl;
import singularity.graphic.SglDrawConst;
import singularity.ui.SglStyles;

public class SupportUsDialog extends BaseDialog {
  public SupportUsDialog() {
    super(Core.bundle.get("misc.contribute"));

    addCloseButton();

    cont.table(SglDrawConst.grayUIAlpha, main -> {
      main.pane(p -> {
        p.defaults().growX().pad(5);
        p.add(Core.bundle.get("dialog.support.info")).growX().wrap();
        p.row();
        p.image().color(Pal.accent).height(4).pad(0).padTop(4).padBottom(8).growX();
        p.row();
        p.image(SglDrawConst.sgl2).scaling(Scaling.fit).size(365);
        p.row();
        p.add(Core.bundle.get("dialog.support.star")).growX().wrap();
        p.row();
        buildButton(p, Icon.github, Pal.accent, "GitHub", Core.bundle.get("dialog.support.githubStar"), () -> Core.app.openURI(Sgl.githubProject));
        p.row();
        p.add(Core.bundle.get("dialog.support.donate")).padTop(20).growX().wrap();
        p.row();
        buildButton(p, Icon.none, Pal.reactorPurple, Core.bundle.get("misc.afdian"), Core.bundle.get("dialog.support.afdian"), () -> {});
        p.row();
        buildButton(p, Icon.none, Pal.lancerLaser, "Patreon", Core.bundle.get("dialog.support.patreon"), () -> {});
      }).growX().fillY();
    }).margin(12).growX().fillY().maxWidth(735);
  }

  private void buildButton(Table table, Drawable icon, Color color, String name, String subText, Runnable listener) {
    table.button(b -> {
      b.left().defaults().left().padBottom(-12);

      b.table(img -> {
        img.image().growY().width(30f).color(color);
        img.row();
        img.image().height(6).width(30f).color(color.cpy().mul(0.8f, 0.8f, 0.8f, 1f));
      }).growY().fillX().padLeft(-12);

      b.table(Tex.buttonEdge3, i -> i.image(icon).size(55)).size(64);

      b.table(t -> {
        t.defaults().left().growX();
        t.add(name).color(Pal.accent);
        t.row();
        t.add(subText).color(Pal.gray);
      }).grow().padLeft(5);
    }, SglStyles.underline, listener);
  }
}
