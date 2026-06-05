package singularity.ui.dialogs;

import arc.Core;
import arc.graphics.Color;
import arc.graphics.g2d.*;
import arc.scene.Element;
import arc.scene.event.Touchable;
import arc.scene.style.BaseDrawable;
import arc.scene.style.NinePatchDrawable;
import arc.scene.ui.Image;
import arc.scene.ui.layout.Scl;
import arc.scene.ui.layout.Table;
import arc.util.Align;
import arc.util.Scaling;
import arc.util.Time;
import arc.util.Tmp;
import ice.ui.menusDialog.DataDialog;
import mindustry.ctype.UnlockableContent;
import mindustry.gen.Tex;
import mindustry.graphics.Pal;
import mindustry.ui.Fonts;
import mindustry.ui.Styles;
import singularity.game.researchs.ResearchProject;
import singularity.graphic.SglDraw;
import singularity.graphic.SglDrawConst;
import singularity.ui.layout.Node;

public class TechNodeCard extends Table {
  final Node node;
  final ResearchProject project;
  final boolean isMark;

  boolean isReveal;

  TechNodeCard(Node node, ResearchProject project){
    this.node = node;

    this.isMark = node.isLineMark;
    this.project = node.isLineMark? null : project;

    isReveal = isMark || project.isRevealed();
    rebuild(project);
  }

  @Override
  public void updateVisibility(){
    visible = project == null || (project.getShowIfRevealess() && project.requiresRevealed()) || project.isRevealed();
  }

  @Override
  public void act(float delta){
    super.act(delta);

    if(!isReveal && project.isRevealed()){
      isReveal = true;
      rebuild(project);
    }
  }

  private void rebuild(ResearchProject project){
    clearChildren();

    if(!isMark){
      NinePatchDrawable drawable = ((NinePatchDrawable) Tex.buttonSideRightOver).tint(SglDrawConst.getMatrixNet());
      Table tab = table(Tex.buttonSideRight, t -> {
        t.update(() -> t.setBackground(project.dependenciesCompleted()? project.isCompleted()? Tex.buttonSideRightDown : project.isProcessing()? drawable : Tex.buttonSideRightOver : Tex.buttonSideRight));

        Table table = t.table(new BaseDrawable() {
          @Override
          public void draw(float x, float y, float width, float height){
            if(project.isCompleted()){
              Draw.color(Pal.accent, 0.3f*parentAlpha);
            } else Draw.color(Pal.darkerGray, 0.7f*parentAlpha);
            Fill.rect(x + width/2f, y + height/2f, width, height);

            Draw.color(Pal.darkestGray, parentAlpha);
            Fill.circle(x + width/2f, y + height/2f, width/2f - Scl.scl(4f));

            float frameStroke = Scl.scl(6f);
            float barStroke = Scl.scl(3f);
            float progress = project.progress();
            float subProgress = project.getInspire() == null || project.getInspire().getApplied()? 0 : project.getInspire().getProvProgress();
            float parentAlpha1 = Draw.getColor().a;
            float rad = width/2f - frameStroke/2f;

            Draw.color(Color.black, parentAlpha1);
            Lines.stroke(frameStroke);
            Lines.circle(x + width/2, y + height/2, rad);
            Draw.color(SglDrawConst.getMatrixNet(), 0.6f*parentAlpha1);
            Lines.circle(x + width/2, y + height/2, rad);
            Draw.color(Color.black, 0.6f*parentAlpha1);
            Lines.stroke(barStroke);
            Lines.circle(x + width/2, y + height/2, rad);

            if(project.isProcessing()){
              Lines.stroke(barStroke);
              Draw.color(SglDrawConst.getMatrixNetDark(), parentAlpha);
              SglDraw.dashCircle(x + width/2, y + height/2, width/2f - Scl.scl(3f), 10, 180, -Time.globalTime);
            }

            if(progress > 0){
              Lines.stroke(barStroke);
              Draw.color(SglDrawConst.getMatrixNet(), parentAlpha1);
              SglDraw.arc(x + width/2, y + height/2, rad, -360f*progress, 90);
            }

            if(subProgress > 0){
              Lines.stroke(frameStroke);
              Draw.color(SglDrawConst.getMatrixNet(), 0.5f*parentAlpha1);
              float angel = -360f*Math.min(subProgress, 1 - progress);
              SglDraw.arc(x + width/2, y + height/2, rad, angel, 90 - progress*360f);

              Draw.color(Color.black, 0.2f*parentAlpha1);
              Lines.stroke(frameStroke/3f);
              SglDraw.arc(x + width/2, y + height/2, rad, angel, 90 - progress*360f);
            }
          }
        }, img -> {
          if(isReveal){
            img.image(project.getIcon().found()? project.getIcon() : project.getContents().first().uiIcon).size(32).scaling(Scaling.fit);
          } else {
            Font.Glyph g = Fonts.outline.getData().getGlyph('?');
            img.image(new TextureRegion(Fonts.outline.getRegion().texture, g.u, g.v2, g.u2, g.v)).size(32).scaling(Scaling.fit).color(SglDrawConst.getFexCrystal());
          }
        }).width(64f).growY().get();


        var ref = new Object() {
          Element fill = null;
        };
        ref.fill = table.fill((x, y, w, h) -> {
          Lines.stroke(3f);
          Draw.color();
          Draw.alpha(parentAlpha);
          x = ref.fill.x;
          y = ref.fill.y;

          SglDraw.arc(x + w/2f, y + h/2f, w/3f, 15f, 90f);
          SglDraw.arc(x + w/2f, y + h/2f, w/3f, 70f, 0f);
          SglDraw.arc(x + w/2f, y + h/2f, w/3f, 15f, 210f);
          SglDraw.arc(x + w/2f, y + h/2f, w/3f, 10f, 250f);
        });


        t.table(new BaseDrawable() {
          @Override
          public void draw(float x, float y, float width, float height){
            if(project.isCompleted()){
              Draw.color(Pal.accent, 0.3f*parentAlpha);
            } else Draw.color(Pal.darkerGray, 0.7f*parentAlpha);
            Fill.tri(x, y, x, y + height, x + width/3f, y);

            Fill.quad(x + width/3f + Scl.scl(45f), y, x + Scl.scl(45f), y + height, x + Scl.scl(95f), y + height, x + width/3f + Scl.scl(95f), y);

            Fill.quad(x + width/3f + Scl.scl(130f), y, x + Scl.scl(130f), y + height, x + Scl.scl(160f), y + height, x + width/3f + Scl.scl(160f), y);

            Fill.quad(x + width/3f + Scl.scl(190f), y, x + Scl.scl(190f), y + height, x + Scl.scl(200f), y + height, x + width/3f + Scl.scl(200f), y);
          }
        }, info -> {
          if(isReveal) info.add(project.getLocalizedName()).growX().labelAlign(Align.left);
          else info.add("未揭示").growX().labelAlign(Align.left);

          info.row();
          info.image().color(Color.darkGray).height(3f).growX().pad(0).padTop(6f).padBottom(6f);
          info.row();

          if(isReveal){
            if(project.getContents().size > 336/32f){
              info.pane(Styles.noBarPane, conts -> {
                for(UnlockableContent content : project.getContents()){
                  conts.button(b -> b.image(content.uiIcon).scaling(Scaling.fit).pad(4f), Styles.cleart, () -> {
                    DataDialog.INSTANCE.showUnlockableContent(content);
                  }).size(32f).padLeft(4f);
                }
              }).scrollY(false).left();
            } else {
              info.table(conts -> {
                for(UnlockableContent content : project.getContents()){
                  conts.button(b -> b.image(content.uiIcon).scaling(Scaling.fit).pad(4f), Styles.cleart, () -> {
                    DataDialog.INSTANCE.showUnlockableContent(content);
                  }).size(32f).padLeft(4f);
                }
              }).left();
            }
          } else info.add("???").growX().height(32f).labelAlign(Align.left).padLeft(4f);
        }).left().grow().margin(4f);
        t.row();
        t.table(desc -> {
          desc.table(new BaseDrawable() {
            @Override
            public void draw(float x, float y, float width, float height){
              if(isReveal){
                if(project.isCompleted() || (project.getInspire() != null && project.getInspire().getApplied())){
                  Draw.color(Pal.accent, 0.3f*parentAlpha);
                } else Draw.color(Pal.darkerGray, 0.7f*parentAlpha);
              } else Draw.color(SglDrawConst.getFexCrystal(), 0.3f*parentAlpha);

              Fill.rect(x + width/2, y + height/2, width, height);
            }
          }, prog -> {
            if(isReveal){
              prog.image(SglDrawConst.techPoint).scaling(Scaling.fit).size(22f).color(SglDrawConst.getMatrixNet());
              prog.add("").fontScale(0.75f).padLeft(4f).fill().update(l -> {
                l.setColor(project.isCompleted()? Pal.accent : Color.lightGray);
                l.setText(Integer.toString(project.getResearched()));
              });
              prog.add("/").color(Color.lightGray).fontScale(0.75f).fill();
              prog.add("").color(Pal.accent).fontScale(0.75f).fill().update(l -> l.setText(project.getHideTechs()? "?" : Integer.toString(project.getRealRequireTechs())));
              prog.add().growX();

              if(project.getInspire() != null){
                prog.add(project.getInspire().getLocalized()).color(Color.lightGray).fontScale(0.75f).fill();
                prog.image(SglDrawConst.inspire).scaling(Scaling.fit).size(22f).color(SglDrawConst.getMatrixNet());
              }
            } else
              prog.add(Core.bundle.get("misc.reveal") + ": " + project.getReveal().localized()).growX().padLeft(4f).labelAlign(Align.left);
          }).growX().margin(4f);
          desc.row();
          desc.add(isReveal? project.getDescription() : "???").width(388f).pad(5f).wrap().labelAlign(Align.left).color(Color.lightGray);
        }).colspan(2).left().grow();
      }).margin(4f).width(420).fillY().get();

      tab.addChild(new Image(Tex.whiteui, Tmp.c1.set(Pal.darkerGray).a(0.6f)) {{
        fillParent = true;
        visible(() -> !project.dependenciesCompleted());
        touchable = Touchable.disabled;
      }});

      tab.addChild(new Table() {{
        fillParent = true;
        visible(project::isProcessing);
        touchable = Touchable.childrenOnly;

        top().right().button(t -> {
          t.image(new BaseDrawable() {
            @Override
            public void draw(float x, float y, float width, float height){
              Draw.color(SglDrawConst.getMatrixNet(), parentAlpha);
              Lines.stroke(Scl.scl(2f));
              Lines.square(x + width/2, y + height/2, width/6f, 45f);
              Lines.stroke(Scl.scl(3f));
              Lines.circle(x + width/2, y + height/2, width/2 - Scl.scl(6f));
              SglDraw.dashCircle(x + width/2, y + height/2, width/2 - Scl.scl(3f), 8, 180, Time.globalTime);
            }
          }).size(36f);
        }, () -> {
          //TODO
        }).fill().top().right().margin(8f).padRight(-8f).padTop(-8f);
      }});
    } else {
      add().size(420, 0);
    }
  }
}
