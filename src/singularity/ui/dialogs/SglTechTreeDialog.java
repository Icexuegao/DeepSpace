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
import arc.util.*;
import ice.content.IPlanets;
import ice.ui.menusDialog.DataDialog;
import mindustry.Vars;
import mindustry.ctype.UnlockableContent;
import mindustry.gen.Tex;
import mindustry.graphics.Pal;
import mindustry.type.Planet;
import mindustry.ui.Fonts;
import mindustry.ui.Styles;
import mindustry.ui.dialogs.BaseDialog;
import singularity.Sgl;
import singularity.game.researchs.ResearchProject;
import singularity.graphic.SglDraw;
import singularity.graphic.SglDrawConst;
import singularity.ui.layout.Line;
import singularity.ui.layout.Node;
import singularity.ui.layout.TechTreeLayout;

import static singularity.ui.layout.TechTreeLayout.checkCenter;

public class SglTechTreeDialog extends Table {
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
        if (!(curr.project.getShowIfRevealess() && curr.project.requiresRevealed()) && !curr.project.isRevealed()) continue;

        curr = line.to;
        while (curr.project == null) {
          curr = curr.children.first();
        }
        boolean childCompleted = curr.project.isCompleted();
        if (!(curr.project.getShowIfRevealess() && curr.project.requiresRevealed()) && !curr.project.isRevealed()) continue;

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
          if (!(curr.project.getShowIfRevealess() && curr.project.requiresRevealed()) && !curr.project.isRevealed()) continue;

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
            if (!(curr.project.getShowIfRevealess() && curr.project.requiresRevealed()) && !curr.project.isRevealed()) continue o;

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

    zoom.addChild(view);
    add(zoom);
    zoom.setFillParent(true);

    rebuildNodes(IPlanets.INSTANCE.get阿德里());

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

    Seq<ResearchProject> projects = Sgl.researches.listResearches(planet);
    OrderedMap<ResearchProject, Node> nodes = new OrderedMap<>();

    for (ResearchProject project : projects) {
      Node node = new Node(project);
      nodes.put(project, node);
    }

    for (Node node : nodes.values()) {
      for (ResearchProject project : node.project.getDependencies()) {
        Node parent = nodes.get(project);
        if (parent != null) {
          parent.addChildren(node);
        }
      }
    }

    layout.reset();
    layout.inputNodes(nodes.values());
    layout.init();

    for (TechNodeCard card : cards) {
      card.remove();
    }
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

}
