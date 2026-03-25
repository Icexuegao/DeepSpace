package singularity.ui.fragments.entityinfo;

import arc.Core;
import arc.func.Boolf;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Lines;
import arc.graphics.g2d.TextureRegion;
import arc.input.KeyCode;
import arc.math.Mat;
import arc.math.Mathf;
import arc.math.geom.Rect;
import arc.math.geom.Vec2;
import arc.scene.Element;
import arc.scene.Group;
import arc.scene.style.TextureRegionDrawable;
import arc.scene.ui.layout.Scl;
import arc.struct.OrderedMap;
import arc.struct.OrderedSet;
import arc.struct.Seq;
import arc.util.*;
import arc.util.pooling.Pool;
import arc.util.pooling.Pools;
import ice.core.SettingValue;
import ice.ui.UI;
import mindustry.Vars;
import mindustry.gen.*;
import mindustry.graphics.Pal;
import mindustry.input.Binding;
import mindustry.ui.Fonts;
import singularity.Sgl;
import singularity.core.UpdatePool;
import singularity.graphic.SglDraw;
import singularity.graphic.SglDrawConst;
import singularity.util.MathTransform;

import java.util.Iterator;
import java.util.Objects;

import static mindustry.Vars.tilesize;

public class EntityInfoFrag{
  public static final int MAX_LIMITED = 64;

  public OrderedMap<EntityInfoDisplay<?>, Boolf<Entityc>> displayMatcher = new OrderedMap<>();

  public OrderedSet<EntityEntry<?>> alphaQueue = new OrderedSet<>(){{
    orderedItems().ordered = false;
  }};

  private static final Mat mat = new Mat();
  private static final Rect tmp = new Rect();

  EntityEntry<?> hold;
  float timer, delta;

  boolean wasHold, showRange, mark, showAllUnits;
  int currCfg = 0;
  float holdTime;

  String showModeTip = "";
  float modeTipAlpha;

  boolean resizing, invalided;
  float sclAlpha, touchY = -1, lastScl = -1;

  static {
    Element cap = new Element();
    UpdatePool.INSTANCE.receive("lockScl", () -> {
      if (Core.app.isDesktop() && (Sgl.ui.entityInfoFrag.resizing || Core.input.alt())){
        Core.scene.setScrollFocus(cap);
      }
      else if (Core.scene.getScrollFocus() == cap) Core.scene.unfocus(cap);
    });
    Vars.control.input.addLock(() -> Core.app.isMobile() && Sgl.ui.entityInfoFrag.resizing);
  }

  @SuppressWarnings({"unchecked", "rawtypes"})
  public void build(Group parent){
    parent.fill((x, y, w, h) -> {
      if (!SettingValue.INSTANCE.get显示实体信息()){
        alphaQueue.clear();

        return;
      }

      update();

      mat.set(Draw.proj());
      Draw.proj(Core.camera.mat);

      if (hold != null && hold.entity != null) {
        float mv = 0.8f +  Mathf.absin(6, 0.2f);
        TextureRegion region = ((TextureRegionDrawable) SglDrawConst.matrixArrow).getRegion();

        Draw.color(Pal.accentBack);
        Draw.rect(region, hold.x() + (hold.size()*2)*mv, hold.y(), hold.size()/1.75f + 4, hold.size()/1.75f + 4, 90);
        Draw.rect(region, hold.x() - (hold.size()*2)*mv, hold.y(), hold.size()/1.75f + 4, hold.size()/1.75f + 4, -90);

        Draw.color(Pal.accent);
        Draw.rect(region, hold.x() + (hold.size()*2)*mv, hold.y(), hold.size()/1.75f, hold.size()/1.75f, 90);
        Draw.rect(region, hold.x() - (hold.size()*2)*mv, hold.y(), hold.size()/1.75f, hold.size()/1.75f, -90);
      }

      for (int i = alphaQueue.size - 1; i >= 0; i--) {
        EntityEntry<?> entry = alphaQueue.orderedItems().get(i);
        if(entry.alpha <= 0.001f) continue;

        float heightOff = entry.size();
        float maxWight = 0;

        float scl = SettingValue.INSTANCE.get信息面板缩放()*Math.max(entry.size()/60f, 0.32f);
        for (EntityInfoDisplay<?> display : entry.display) {
          maxWight = Math.max(maxWight, display.wight(scl));
        }

        for (EntityInfoDisplay<?> display : entry.display) {
          heightOff += ((EntityInfoDisplay)display).draw(entry, Vars.player.team(), maxWight, heightOff, entry.alpha*SettingValue.INSTANCE.get状态指示器不透明度(), scl);
        }
      }
      Draw.proj(mat);

      if (modeTipAlpha > 0.001) {
        float alpha = 1 - Mathf.pow(1 - modeTipAlpha, 4);
        Fonts.def.draw(showModeTip, Core.graphics.getWidth()/2f, Core.graphics.getHeight()*0.3f - 1f, Tmp.c1.set(Color.gray).a(alpha), Scl.scl(1.4f), true, Align.center);
        Fonts.def.draw(showModeTip, Core.graphics.getWidth()/2f, Core.graphics.getHeight()*0.3f, Tmp.c1.set(Color.white).a(alpha), Scl.scl(1.4f), true, Align.center);
      }

      if (sclAlpha > 0.001) {
        float alpha = 1 - Mathf.pow(1 - sclAlpha, 4);
        String str = Strings.autoFixed(SettingValue.INSTANCE.get信息面板缩放(), 2) + "x";
        String str1 = Core.bundle.get(Core.app.isMobile()? "infos.zoomMobile": "infos.zoomDesktop");
        Fonts.def.draw(str1, Core.graphics.getWidth()/2f, Core.graphics.getHeight()*0.24f - 1f, Tmp.c1.set(Color.gray).a(alpha), Scl.scl(1.4f), true, Align.center);
        Fonts.def.draw(str1, Core.graphics.getWidth()/2f, Core.graphics.getHeight()*0.24f, Tmp.c1.set(Color.white).a(alpha), Scl.scl(1.4f), true, Align.center);
        Fonts.def.draw(str, Core.graphics.getWidth()/2f, Core.graphics.getHeight()*0.2f - 1f, Tmp.c1.set(Color.gray).a(alpha), Scl.scl(1.3f), true, Align.center);
        Fonts.def.draw(str, Core.graphics.getWidth()/2f, Core.graphics.getHeight()*0.2f, Tmp.c1.set(Color.white).a(alpha), Scl.scl(1.3f), true, Align.center);
      }

      if (showRange){
        Vec2 v = Core.input.mouse();

        Lines.stroke(4, Color.lightGray);
        Draw.alpha(0.3f + Mathf.absin(Time.globalTime, 5, 0.3f));
        SglDraw.dashCircle(v.x, v.y, SettingValue.INSTANCE.get范围显示模式选中半径(), 40, 180, MathTransform.gradientRotateDeg(Time.globalTime/5, 45, 8));
      }
    });

    Interval t = new Interval();
    UI.INSTANCE.getToolBarFrag().addTool(
        "showInfos",
        () -> Core.bundle.get(SettingValue.INSTANCE.get显示实体信息()? "infos.showInfos": "infos.hideInfos"),
        () -> SettingValue.INSTANCE.get显示实体信息()? SglDrawConst.showInfos: SglDrawConst.unShowInfos,
        () -> {
          SettingValue.INSTANCE.set显示实体信息(!SettingValue.INSTANCE.get显示实体信息()) ;
          if (SettingValue.INSTANCE.get显示实体信息()){
            UI.INSTANCE.getToolBarFrag().showTool("changeMode");
            UI.INSTANCE.getToolBarFrag().showTool("infoScl");
          }
          else {
            UI.INSTANCE.getToolBarFrag().hideTool("changeMode");
            UI.INSTANCE.getToolBarFrag().hideTool("infoScl");
          }

          if(t.get(60)){
            Sgl.config.save();
          }
        },
            SettingValue.INSTANCE::get显示实体信息
    );
    UI.INSTANCE.getToolBarFrag().addTool(
        "changeMode",
        () -> Core.bundle.get("infos.changeMode"),
        () -> showRange? SglDrawConst.showRange: wasHold? SglDrawConst.hold: showAllUnits? Icon.admin: Icon.zoom,
        this::changeMode,
        () -> false
    );
    UI.INSTANCE.getToolBarFrag().addTool(
        "infoScl",
        () -> Core.bundle.get("infos.resizeInfoScl"),
        () -> Icon.resize,
        () -> {
          resizing = !resizing;
          if (!resizing) {
            Sgl.config.save();
          }
        },
        () -> resizing
    );
  }

  @SuppressWarnings({"rawtypes", "unchecked"})
  public void update(){
    if (!invalided && Core.app.isMobile() && resizing && Core.input.isTouched()){
      sclAlpha = Mathf.lerpDelta(sclAlpha, 1, 0.1f);
      if (touchY >= 0){
        SettingValue.INSTANCE.set信息面板缩放(Mathf.clamp(lastScl - (touchY - Core.input.mouseY())/Core.graphics.getHeight()*2, 0.5f, 4f))  ;
      }
      else{
        lastScl = SettingValue.INSTANCE.get信息面板缩放();
        touchY = Core.input.mouseY();
      }
    }
    else{
      lastScl = -1;
      touchY = -1;
    }

    if (Core.input.alt()){
      if (!mark) holdTime = Time.globalTime;
      mark = true;
    }
    else{
      if (!invalided && mark && Time.globalTime - holdTime < 30){
        changeMode();
      }
      invalided = false;

      mark = false;
    }

    if (!invalided && Core.app.isDesktop() && Time.globalTime - holdTime > 30 && (resizing || Core.input.alt())){
      sclAlpha = Mathf.lerpDelta(sclAlpha, 1, 0.1f);
      float scroll = Core.input.axis(KeyCode.scroll);
      if (scroll != 0){
        SettingValue.INSTANCE.set信息面板缩放(SettingValue.INSTANCE.get信息面板缩放()+scroll/20) ;
        SettingValue.INSTANCE.set信息面板缩放(Mathf.clamp(SettingValue.INSTANCE.get信息面板缩放(), 0.5f, 4f));
      }
    }

    if (hold != null && hold.entity == null) hold = null;

    boolean touched = Core.input.keyTap(Binding.select) && Core.input.alt();
    if (touched){
      hold = null;
      mark = false;
    }

    delta += Time.delta;
    if (!touched && Time.globalTime - timer < SettingValue.INSTANCE.get信息显示刷新间隔()) return;
    timer = Time.globalTime;

    Vec2 v = Core.input.mouseWorld();

    float dist = 0;
    for (Entityc e : Groups.all) {
      float size = showRange? 0: e instanceof Hitboxc h ? h.hitSize() / 2 : e instanceof Building b ? b.block.size*tilesize/2f : 10;

      if (showRange) {
        Core.camera.project(Tmp.v1.set(v));
        Tmp.v1.x += SettingValue.INSTANCE.get范围显示模式选中半径();
      }
      float range = showRange? Math.abs(Core.camera.unproject(Tmp.v1).x - v.x): 0;

      if (e instanceof Posc ent
          && ((showAllUnits && ent instanceof Hitboxc && tmp.set(0, 0, Core.graphics.getWidth(), Core.graphics.getHeight()).contains(Core.camera.project(Tmp.v1.set(ent))))
          || (!showRange && Math.abs(ent.x() - v.x) < size && Math.abs(ent.y() - v.y) < size)
          || (showRange && ent.dst(v.x, v.y) < range))
      ){
        EntityEntry entry = Pools.obtain(EntityEntry.class, EntityEntry::new);
        entry.entity = e;
        entry.hovering = true;
        entry.alpha = 0;

        EntityEntry existed;
        if ((existed = alphaQueue.get(entry)) == null) {
          displayMatcher.each((display, cons) -> {
            if (cons.get(e)) entry.display.add(display);
          });

          if (!entry.display.isEmpty()) {
            alphaQueue.add(entry);
          }
          else Pools.free(entry);
        }
        else{
          existed.hovering = true;
          Pools.free(entry);
        }

        if (touched) {
          float dis = ent.dst(v);
          if (dis < dist || hold == null) {
            hold = existed;
            dist = dis;
            invalided = true;
          }
        }
      }
    }

    if (!alphaQueue.isEmpty()) alphaQueue.orderedItems().sort((a, b) -> {
      Vec2 pos = Core.input.mouseWorld();
      return (int) (a.entity.dst(pos) - b.entity.dst(pos));
    });

    int count = 0;
    Iterator<EntityEntry<?>> itr = alphaQueue.iterator();
    while(itr.hasNext()){
      EntityEntry<?> entry = itr.next();
      if (count <= MAX_LIMITED && count >=  SettingValue.INSTANCE.get最多信息显示数目()){
        entry.hovering = false;
        entry.alpha = Mathf.approach(entry.alpha, 0, 0.025f*delta);
        continue;
      }

      boolean isHovered = entry.entity.isAdded() && ((wasHold && !showRange) || entry.hovering || (hold != null && hold.entity == entry.entity) || Core.input.alt());
      entry.alpha = Mathf.approach(entry.alpha, isHovered ? 1: 0, (isHovered? 0.1f: 0.025f)*delta);

      entry.hovering = false;

      if (!isHovered && entry.alpha < 0.001f){
        itr.remove();
        Pools.free(entry);
        continue;
      }

      for (EntityInfoDisplay display : entry.display) {
        display.updateVar(entry, delta);
      }

      count++;
    }

    modeTipAlpha = Mathf.approach(modeTipAlpha, 0, 0.006f*delta);
    sclAlpha = Mathf.approach(sclAlpha, 0, 0.006f*delta);
    delta = 0;
  }

  private void changeMode() {
    hold = null;

    showRange = wasHold = showAllUnits = false;
    currCfg++;
    currCfg%=4;

    switch(currCfg){
      case 0:
        showModeTip = Core.bundle.get("infos.holdOff");
        break;
      case 1:
        wasHold = true;
        showModeTip = Core.bundle.get("infos.holdShowed");
        break;
      case 2:
        showRange = true;
        showModeTip = Core.bundle.get("infos.holdShowRang");
        break;
      case 3:
        showAllUnits = true;
        showModeTip = Core.bundle.get("infos.holdShowAll");
    }

    modeTipAlpha = 1;
  }

  public static class EntityEntry<T extends Entityc & Posc> implements Pool.Poolable{
    T entity;
    float alpha;
    boolean hovering;
    Seq<EntityInfoDisplay<T>> display = new Seq<>();

    public float x(){
      return entity.x();
    }

    public float y(){
      return entity.y();
    }

    public float size(){
      return entity instanceof Hitboxc h ? h.hitSize() / 2 : entity instanceof Building b ? b.block.size*tilesize/2f : 10;
    }

    @Override
    public void reset() {
      entity = null;
      alpha = 0;
      hovering = false;
     // extra().clear();
      display.clear();
    }

    @SuppressWarnings("rawtypes")
    @Override
    public boolean equals(Object o) {
      if (this == o) return true;
      if (!(o instanceof EntityEntry e)) return false;
      return entity == e.entity;
    }

    @Override
    public int hashCode() {
      return Objects.hash(entity);
    }
  }
}
