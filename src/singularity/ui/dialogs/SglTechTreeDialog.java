package singularity.ui.dialogs;

import arc.Core;
import arc.graphics.Color;
import arc.graphics.g2d.*;
import arc.input.KeyCode;
import arc.math.Mathf;
import arc.math.geom.Rect;
import arc.scene.Element;
import arc.scene.Group;
import arc.scene.event.ElementGestureListener;
import arc.scene.event.InputEvent;
import arc.scene.event.InputListener;
import arc.scene.event.Touchable;
import arc.scene.style.BaseDrawable;
import arc.scene.style.NinePatchDrawable;
import arc.scene.ui.Image;
import arc.scene.ui.layout.Scl;
import arc.scene.ui.layout.Table;
import arc.struct.OrderedMap;
import arc.struct.Seq;
import arc.util.Align;
import arc.util.Scaling;
import arc.util.Time;
import arc.util.Tmp;
import mindustry.Vars;
import mindustry.ctype.UnlockableContent;
import mindustry.gen.Tex;
import mindustry.graphics.Pal;
import mindustry.type.Planet;
import mindustry.ui.Fonts;
import mindustry.ui.Styles;
import mindustry.ui.dialogs.BaseDialog;
import singularity.contents.SglPlanets;
import singularity.game.researchs.ResearchProject;
import singularity.graphic.SglDraw;
import singularity.graphic.SglDrawConst;
import singularity.ui.layout.Line;
import singularity.ui.layout.Node;
import singularity.ui.layout.TechTreeLayout;

import static singularity.ui.layout.TechTreeLayout.checkCenter;

public class SglTechTreeDialog extends BaseDialog {
  public TechTreeLayout layout = new TechTreeLayout(){{
    alignWidth = Scl.scl(600);
    alignHeight = Scl.scl(180);
  }};
  public Planet planet;

  Seq<TechNodeCard> cards = new Seq<>();
  Seq<Line> lines = new Seq<>();

  Group zoom = new Group() {{
    setOrigin(Align.center);
    setScale(Scl.scl(0.5f));
    setTransform(true);
  }};
  Group view = new Group() {
    static final float corner = Scl.scl(15f);
    final Rect cull = new Rect();

    { setCullingArea(cull); }

    @Override
    public void act(float delta) {
      super.act(delta);

      Tmp.v1.set(0, 0);
      Tmp.v2.set(Core.scene.getWidth(), Core.scene.getHeight());

      stageToLocalCoordinates(Tmp.v1);
      stageToLocalCoordinates(Tmp.v2);

      cull.set(Tmp.v1.x, Tmp.v1.y, Tmp.v2.x - Tmp.v1.x, Tmp.v2.y - Tmp.v1.y);
    }

    @Override
    protected void drawChildren() {
      drawLinks();
      super.drawChildren();
    }

    void drawLinks(){
      Lines.stroke(4f);

      for (Line line : lines) {
        if(!cull.contains(line.beginX, line.beginY) && !cull.contains(line.endX, line.endY)) continue;


        Node curr = line.from;
        while (curr.project == null) {
          curr = curr.parents.first();
        }
        boolean isCompleted = curr.project.isCompleted();
        if (!(curr.project.showIfRevealess && curr.project.requiresRevealed()) && !curr.project.isRevealed()) continue;

        curr = line.to;
        while (curr.project == null) {
          curr = curr.children.first();
        }
        boolean childCompleted = curr.project.isCompleted();
        if (!(curr.project.showIfRevealess && curr.project.requiresRevealed()) && !curr.project.isRevealed()) continue;

        Draw.color(
            isCompleted?
                childCompleted?
                    Pal.accent:
                Tmp.c1.set(Pal.accent).lerp(Color.lightGray, Mathf.absin(10, 1)):
            Color.lightGray,
            parentAlpha
        );

        float originX = x + line.beginX;
        float originY = y + line.beginY;
        float toX = x + line.endX;
        float toY = y + line.endY;
        float off = line.offset;

        int n = Float.compare(toY, originY);

        if (Mathf.equal(originY, toY)){
          Lines.line(originX, originY, toX, toY);
        }
        else {
          Lines.beginLine();
          Lines.linePoint(originX, originY);
          Lines.linePoint(originX + off - corner, originY);
          Lines.linePoint(originX + off, originY + corner*n);
          Lines.linePoint(originX + off, toY - corner*n);
          Lines.linePoint(originX + off + corner, toY);
          Lines.linePoint(toX, toY);
          Lines.endLine();
        }
      }

      drawLineMark();
    }

    private void drawLineMark() {
      o: for (TechNodeCard card : cards) {
        Node node = card.node;
        if (node.isLineMark){
          Node curr = node;
          while (curr.project == null) {
            curr = curr.parents.first();
          }
          boolean isCompleted = curr.project.isCompleted();
          if (!(curr.project.showIfRevealess && curr.project.requiresRevealed()) && !curr.project.isRevealed()) continue;

          float centerOrig = checkCenter(node, true);
          
          float originX = x + node.getX();
          float originY = y + node.getY();
          boolean anyCompleted = false;

          for (Node child : node.children) {
            curr = child;
            while (curr.project == null) {
              curr = curr.children.first();
            }
            boolean childCompleted = curr.project.isCompleted();
            if (!(curr.project.showIfRevealess && curr.project.requiresRevealed()) && !curr.project.isRevealed()) continue o;

            anyCompleted |= childCompleted;

            Draw.color(
                isCompleted?
                    childCompleted?
                        Pal.accent:
                    Tmp.c1.set(Pal.accent).lerp(Color.lightGray, Mathf.absin(10, 1)):
                Color.lightGray,
                parentAlpha
            );

            int ordFrom = node.children.indexOf(child);
            float offOrd = ordFrom - centerOrig;
            float oY = originY - offOrd*Scl.scl(20);
            int n = Float.compare(oY, originY);

            float diff = Math.min(Math.abs(oY - originY), Scl.scl(15f));

            if (n == 0){
              Lines.line(originX, originY, originX + node.width/2f, oY);
            }
            else if (diff < Scl.scl(15)){
              Lines.beginLine();
              Lines.linePoint(originX, originY);
              Lines.linePoint(originX + diff, oY);
              Lines.linePoint(originX + node.width/2f, oY);
              Lines.endLine();
            }
            else {
              Lines.beginLine();
              Lines.linePoint(originX, originY);
              Lines.linePoint(originX, oY - n*diff);
              Lines.linePoint(originX + diff, oY);
              Lines.linePoint(originX + node.width/2f, oY);
              Lines.endLine();
            }
          }

          Draw.color(
              isCompleted?
                  anyCompleted?
                      Pal.accent:
                  Tmp.c1.set(Pal.accent).lerp(Color.lightGray, Mathf.absin(10, 1)):
              Color.lightGray,
              parentAlpha
          );
          Lines.line(originX - node.width/2f, originY, originX, originY);
        }
      }
    }
  };

  public SglTechTreeDialog() {
    super(Core.bundle.get("dialog.techtree.title"));

    zoom.addChild(view);
    cont.add(zoom);
    zoom.setFillParent(true);
    setBackground(SglDrawConst.grayUI);

    addCloseButton();

    rebuildNodes(SglPlanets.foryust);

    touchable = Touchable.enabled;

    addListener(new ElementGestureListener() {
      private boolean panEnable = false;
      private float lastZoom = -1f;

      @Override
      public void zoom(InputEvent event, float initialDistance, float distance) {
        if (lastZoom < 0) {
          lastZoom = zoom.scaleX;
        }

        zoom.setScale(Mathf.clamp(distance/initialDistance*lastZoom, 0.25f, 1f));
      }

      @Override
      public void touchDown(InputEvent event, float x, float y, int pointer, KeyCode button) {
        if (button != KeyCode.mouseLeft || pointer != 0) return;
        panEnable = true;
      }

      @Override
      public void touchUp(InputEvent event, float x, float y, int pointer, KeyCode button) {
        if (button != KeyCode.mouseLeft || pointer != 0) return;
        lastZoom = zoom.scaleX;
        panEnable = false;
      }

      @Override
      public void pan(InputEvent event, float tx, float ty, float deltaX, float deltaY) {
        if (!panEnable) return;

        view.moveBy(deltaX / zoom.scaleX, deltaY / zoom.scaleY);
      }
    });

    addListener(new InputListener() {
      @Override
      public boolean scrolled(InputEvent event, float x, float y, float amountX, float amountY) {
        float newScale = Mathf.clamp(zoom.scaleX - amountY / 10f * zoom.scaleX, 0.25f, 1f);
        zoom.setScale(newScale);

        return true;
      }

      @Override
      public void enter(InputEvent event, float x, float y, int pointer, Element fromActor) {
        requestScroll();
        super.enter(event, x, y, pointer, fromActor);
      }
    });
  }

  public void rebuildNodes(Planet planet){
    this.planet = planet;

    //Seq<ResearchProject> projects = Sgl.researches.listResearches(planet);
    OrderedMap<ResearchProject, Node> nodes = new OrderedMap<>();

  /*  for (ResearchProject project : projects) {
      Node node = new Node(project);
      nodes.put(project, node);
    }*/

    for (Node node : nodes.values()) {
      for (ResearchProject project : node.project.dependencies) {
        Node parent = nodes.get(project);
        parent.addChildren(node);
      }
    }

    layout.reset();
    layout.inputNodes(nodes.values());
    layout.init();

    cards.forEach(Element::remove);
    cards.clear();
    lines.clear();
    for (Node node : layout.getRawContexts()) {
      TechNodeCard card = new TechNodeCard(node, node.project);
      cards.add(card);

      view.addChild(card);
      card.pack();
      node.width = card.getWidth();
      node.height = card.getHeight();
    }

    layout.layout();

    lines.addAll(layout.buildLines(Scl.scl(180), Scl.scl(20)));

    for (TechNodeCard card : cards) {
      card.setPosition(card.node.getX(), card.node.getY(), Align.center);
    }
  }

  static class TechNodeCard extends Table {
    final Node node;
    final ResearchProject project;
    final boolean isMark;

    boolean isReveal;

    TechNodeCard(Node node, ResearchProject project) {
      this.node = node;

      this.isMark = node.isLineMark;
      this.project = node.isLineMark? null: project;

      isReveal = isMark || project.isRevealed();
      rebuild(project);
    }

    @Override
    public void updateVisibility() {
      visible = project == null || (project.showIfRevealess && project.requiresRevealed()) || project.isRevealed();
    }

    @Override
    public void act(float delta) {
      super.act(delta);

      if (!isReveal && project.isRevealed()) {
        isReveal = true;
        rebuild(project);
      }
    }

    private void rebuild(ResearchProject project) {
      clearChildren();

      if (!isMark) {
        NinePatchDrawable drawable = ((NinePatchDrawable) Tex.buttonSideRightOver).tint(SglDrawConst.matrixNet);
        Table tab = table(Tex.buttonSideRight, t -> {
          t.update(() -> t.setBackground(
              project.dependenciesCompleted()?
                  project.isCompleted()?
                      Tex.buttonSideRightDown:
                  project.isProcessing()? drawable: Tex.buttonSideRightOver:
              Tex.buttonSideRight
          ));

          t.table(new BaseDrawable(){
            @Override
            public void draw(float x, float y, float width, float height) {
              if (project.isCompleted()){
                Draw.color(Pal.accent, 0.3f*parentAlpha);
              }
              else Draw.color(Pal.darkerGray, 0.7f*parentAlpha);
              Fill.rect(x + width/2f, y + height/2f, width, height);

              Draw.color(Pal.darkestGray, parentAlpha);
              Fill.circle(x + width/2f, y + height/2f, width/2f - Scl.scl(4f));

              float frameStroke = Scl.scl(6f);
              float barStroke = Scl.scl(3f);
              float progress = project.progress();
              float subProgress = project.inspire == null || project.inspire.applied? 0: project.inspire.provProgress;
              float parentAlpha1 = Draw.getColor().a;
              float rad = width/2f - frameStroke/2f;

              Draw.color(Color.black, parentAlpha1);
              Lines.stroke(frameStroke);
              Lines.circle(x + width/2, y + height/2, rad);
              Draw.color(SglDrawConst.matrixNet, 0.6f*parentAlpha1);
              Lines.circle(x + width/2, y + height/2, rad);
              Draw.color(Color.black, 0.6f*parentAlpha1);
              Lines.stroke(barStroke);
              Lines.circle(x + width/2, y + height/2, rad);

              if (project.isProcessing()) {
                Lines.stroke(barStroke);
                Draw.color(SglDrawConst.matrixNetDark, parentAlpha);
                SglDraw.dashCircle(
                    x + width/2, y + height/2, width/2f - Scl.scl(3f), 10, 180, -Time.globalTime
                );
              }

              if (progress > 0) {
                Lines.stroke(barStroke);
                Draw.color(SglDrawConst.matrixNet, parentAlpha1);
                SglDraw.arc(
                    x + width/2, y + height/2, rad,
                    -360f*progress, 90
                );
              }

              if (subProgress > 0){
                Lines.stroke(frameStroke);
                Draw.color(SglDrawConst.matrixNet, 0.5f*parentAlpha1);
                float angel = -360f*Math.min(subProgress, 1 - progress);
                SglDraw.arc(
                    x + width/2, y + height/2, rad, angel,
                    90 - progress*360f
                );

                Draw.color(Color.black, 0.2f*parentAlpha1);
                Lines.stroke(frameStroke/3f);
                SglDraw.arc(
                    x + width/2, y + height/2, rad, angel,
                    90 - progress*360f
                );
              }
            }
          }, img -> {
            if (isReveal) {
              img.image(project.icon != null ? project.icon : project.contents.first().uiIcon).size(32).scaling(Scaling.fit);
            }
            else {
              Font.Glyph g = Fonts.outline.getData().getGlyph('?');
              img.image(new TextureRegion(Fonts.outline.getRegion().texture, g.u, g.v2, g.u2, g.v)).size(32).scaling(Scaling.fit).color(SglDrawConst.fexCrystal);
            }
          }).width(64f).growY().get().fill((x, y, w, h) -> {
            Lines.stroke(3f);
            Draw.color();
            Draw.alpha(parentAlpha);
            SglDraw.arc(
                x + w/2f, y + h/2f, w/3f,
                15f, 90f
            );
            SglDraw.arc(
                x + w/2f, y + h/2f, w/3f,
                70f, 0f
            );
            SglDraw.arc(
                x + w/2f, y + h/2f, w/3f,
                15f, 210f
            );
            SglDraw.arc(
                x + w/2f, y + h/2f, w/3f,
                10f, 250f
            );
          });
          t.table(new BaseDrawable(){
            @Override
            public void draw(float x, float y, float width, float height) {
              if (project.isCompleted()){
                Draw.color(Pal.accent, 0.3f*parentAlpha);
              }
              else Draw.color(Pal.darkerGray, 0.7f*parentAlpha);
              Fill.tri(x, y, x, y + height, x + width/3f, y);

              Fill.quad(
                  x + width/3f + Scl.scl(45f), y,
                  x + Scl.scl(45f), y + height,
                  x + Scl.scl(95f), y + height,
                  x + width/3f + Scl.scl(95f), y
              );

              Fill.quad(
                  x + width/3f + Scl.scl(130f), y,
                  x + Scl.scl(130f), y + height,
                  x + Scl.scl(160f), y + height,
                  x + width/3f + Scl.scl(160f), y
              );

              Fill.quad(
                  x + width/3f + Scl.scl(190f), y,
                  x + Scl.scl(190f), y + height,
                  x + Scl.scl(200f), y + height,
                  x + width/3f + Scl.scl(200f), y
              );
            }
          }, info -> {
            if (isReveal) info.add(project.localizedName).growX().labelAlign(Align.left);
            else info.add(Core.bundle.get("misc.revealess")).growX().labelAlign(Align.left);

            info.row();
            info.image().color(Color.darkGray).height(3f).growX().pad(0).padTop(6f).padBottom(6f);
            info.row();

            if (isReveal) {
              if (project.contents.size > 336/32f) {
                info.pane(Styles.noBarPane, conts -> {
                  for (UnlockableContent content : project.contents) {
                    conts.button(b -> b.image(content.uiIcon).scaling(Scaling.fit).pad(4f), Styles.cleart, () -> {
                      Vars.ui.content.show(content);
                    }).size(32f).padLeft(4f);
                  }
                }).scrollY(false).left();
              } else {
                info.table(conts -> {
                  for (UnlockableContent content : project.contents) {
                    conts.button(b -> b.image(content.uiIcon).scaling(Scaling.fit).pad(4f), Styles.cleart, () -> {
                      Vars.ui.content.show(content);
                    }).size(32f).padLeft(4f);
                  }
                }).left();
              }
            }
            else info.add("???").growX().height(32f).labelAlign(Align.left).padLeft(4f);
          }).left().grow().margin(4f);
          t.row();
          t.table(desc -> {
            desc.table(new BaseDrawable(){
              @Override
              public void draw(float x, float y, float width, float height) {
                if (isReveal) {
                  if (project.isCompleted() || (project.inspire != null && project.inspire.applied)){
                    Draw.color(Pal.accent, 0.3f*parentAlpha);
                  }
                  else Draw.color(Pal.darkerGray, 0.7f*parentAlpha);
                }
                else Draw.color(SglDrawConst.fexCrystal, 0.3f*parentAlpha);

                Fill.rect(x + width/2, y + height/2, width, height);
              }
            }, prog -> {
              if (isReveal) {
                prog.image(SglDrawConst.techPoint).scaling(Scaling.fit).size(22f).color(SglDrawConst.matrixNet);
                prog.add("").fontScale(0.75f).padLeft(4f).fill()
                    .update(l -> {
                      l.setColor(project.isCompleted() ? Pal.accent : Color.lightGray);
                      l.setText(Integer.toString(project.getResearched()));
                    });
                prog.add("/").color(Color.lightGray).fontScale(0.75f).fill();
                prog.add("").color(Pal.accent).fontScale(0.75f).fill()
                    .update(l -> l.setText(project.hideTechs ? "?" : Integer.toString(project.getRealRequireTechs())));
                prog.add().growX();

                if (project.inspire != null) {
                  prog.add(project.inspire.localized).color(Color.lightGray).fontScale(0.75f).fill();
                  prog.image(SglDrawConst.inspire).scaling(Scaling.fit).size(22f).color(SglDrawConst.matrixNet);
                }
              }
              else prog.add(Core.bundle.get("misc.reveal") + ": " + project.reveal.localized()).growX().padLeft(4f).labelAlign(Align.left);
            }).growX().margin(4f);
            desc.row();
            desc.add(isReveal? project.description: "???").width(388f).pad(5f).wrap().labelAlign(Align.left).color(Color.lightGray);
          }).colspan(2).left().grow();
        }).margin(4f).width(420).fillY().get();

        tab.addChild(new Image(Tex.whiteui, Tmp.c1.set(Pal.darkerGray).a(0.6f)){{
          fillParent = true;
          visible(() -> !project.dependenciesCompleted());
          touchable = Touchable.disabled;
        }});

        tab.addChild(new Table(){{
          fillParent = true;
          visible(project::isProcessing);
          touchable = Touchable.childrenOnly;

          top().right().button(t -> {
            t.image(new BaseDrawable(){
              @Override
              public void draw(float x, float y, float width, float height) {
                Draw.color(SglDrawConst.matrixNet, parentAlpha);
                Lines.stroke(Scl.scl(2f));
                Lines.square(x + width/2, y + height/2, width/6f, 45f);
                Lines.stroke(Scl.scl(3f));
                Lines.circle(x + width/2, y + height/2, width/2 - Scl.scl(6f));
                SglDraw.dashCircle(x + width/2, y + height/2,
                    width/2 - Scl.scl(3f), 8, 180, Time.globalTime);
              }
            }).size(36f);
          }, () -> {
            //TODO
          }).fill().top().right().margin(8f).padRight(-8f).padTop(-8f);
        }});
      }
      else {
        add().size(420, 0);
      }
    }
  }
}
