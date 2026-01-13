package singularity.ui.dialogs;

import arc.Core;
import arc.func.Prov;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Fill;
import arc.scene.Element;
import arc.scene.style.Drawable;
import arc.scene.ui.Label;
import arc.scene.ui.layout.Scl;
import arc.util.Tmp;
import mindustry.Vars;
import mindustry.content.Liquids;
import mindustry.gen.Tex;
import mindustry.graphics.Pal;
import mindustry.ui.WarningBar;
import mindustry.ui.dialogs.BaseDialog;
import singularity.Sgl;
import singularity.graphic.SglDrawConst;
import singularity.ui.SglStyles;
import universecore.UncCore;

public class MainMenu extends BaseDialog {
  protected boolean launch = true;
  private static final Runnable lookForward = () -> Vars.ui.showInfo(Core.bundle.get("mod.lookForward"));

  //Sgl.ui.publicInfo.show();
  ButtonEntry[] buttonEntries = new ButtonEntry[]{
      new ButtonEntry(SglDrawConst.startIcon,
          () -> Core.bundle.get(launch? "misc.startGame": "misc.backToGame"),
          () -> Color.white, () -> { hide(); launch = false; }),
      new ButtonEntry(SglDrawConst.databaseIcon, Core.bundle.get("misc.modDatabase"), Pal.accent, lookForward),
      new ButtonEntry(SglDrawConst.configureIcon, Core.bundle.get("misc.modConfigure"), Color.lightGray, () -> {
        Sgl.ui.config.show();
      }),
      new ButtonEntry(SglDrawConst.publicInfoIcon, () -> Core.bundle.get("misc.publicInfo"), () -> Liquids.cryofluid.color, () -> {
        Sgl.ui.publicInfo.show();
      }),
      new ButtonEntry(SglDrawConst.aboutIcon, Core.bundle.get("misc.aboutMod"), Color.violet, () -> {
        Sgl.ui.aboutDialog.show();
      }),
      new ButtonEntry(SglDrawConst.contributeIcon, Core.bundle.get("misc.contribute"), Color.yellow, () -> Sgl.ui.support.show()),
  };

  public MainMenu() {
    super(Core.bundle.get("dialog.mainMenu.title"));
  }
  
  public void build() {
    cont.top().table(main -> {
      main.image(SglDrawConst.sglLaunchLogo).size(220, 110).padTop(30);
      main.row();
      main.image().color(Pal.accent).growX().height(3).pad(0).padTop(4).padBottom(4);
      main.row();
      main.add(new WarningBar()).growX().height(14).color(Color.lightGray).padBottom(30).padLeft(-5).padRight(-5);
      main.row();
      main.add("S  I  N  G  U  L  A  R  I  T  Y").get().setFontScale(2);
      main.row();
      main.add(new WarningBar()).growX().height(14).color(Color.lightGray).padTop(30).padLeft(-5).padRight(-5);
      main.row();
      main.image().color(Pal.accent).growX().height(3).pad(0).padTop(4);
      main.row();
      main.pane(menu -> {
        menu.defaults().pad(0).padTop(6).margin(0).width(680).height(64).top();
        for(ButtonEntry entry : buttonEntries){
          menu.button(b -> {
            b.table(Tex.buttonEdge3, i -> i.image(entry.region).size(55)).size(64);
            Label l = b.add("").width(550).padLeft(10).get();
            shown(() -> l.setText(entry.text.get()));
            
            b.add(new Element(){
              @Override
              public void draw(){
                Draw.color(Tmp.c1.set(entry.color.get()).lerp(Color.black, 0.3f));
                Draw.alpha(parentAlpha);
                Fill.square(x + width/2, y + height/2 - Scl.scl(6), width/8, 45);
                Draw.color(entry.color.get());
                Draw.alpha(parentAlpha);
                Fill.square(x + width/2, y + height/2, width/8, 45);
              }
            }).size(64);
          }, SglStyles.underline, entry.clicked);
          menu.row();
        }
      }).grow();
    }).growX().top().pad(0).margin(0);
    
    row();
    image().color(Color.white).growX().height(2).pad(0).padTop(4);
    row();
    table(t -> {
      t.add("Singularity:").left().padLeft(3);
      t.image().color(Color.white).growY().width(2).pad(0).margin(0).padLeft(4).colspan(4);
      t.add().growX();
      t.image().color(Color.white).width(2).growY().pad(0).margin(0).padRight(4).colspan(4);
      t.add("powered by UniverseCore:" + UncCore.version).right().padRight(3);
    }).growX();
  }
  
  private static class ButtonEntry{
    Drawable region;
    Prov<String> text;
    Prov<Color> color;
    
    Runnable clicked;
    
    public ButtonEntry(Drawable region, Prov<String> text, Prov<Color> color, Runnable clicked){
      this.region = region;
      this.text = text;
      this.color = color;
      this.clicked = clicked;
    }
    
    public ButtonEntry(Drawable region, String text, Color color, Runnable clicked){
      this(region, () -> text, () -> color, clicked);
    }
  }
}
