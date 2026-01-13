package singularity.ui.fragments.entityinfo;

import arc.Core;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.GlyphLayout;
import arc.graphics.g2d.ScissorStack;
import arc.graphics.g2d.TextureRegion;
import arc.math.Mathf;
import arc.math.geom.Rect;
import arc.math.geom.Vec2;
import arc.util.Align;
import arc.util.Time;
import arc.util.Tmp;
import mindustry.game.Team;
import mindustry.gen.*;
import mindustry.graphics.Pal;
import mindustry.ui.Fonts;
import singularity.Sgl;

import static arc.graphics.g2d.Draw.xscl;
import static arc.graphics.g2d.Draw.yscl;

public enum HealthBarStyle {
  mindustry(9, 19, 153, 7, 14, 30, 10),
  tech(31, 14, 165, 6, 38, 28, 9),
  shape(6, 15, 7, 4, 12, 28, 10);

  static final Rect scissor = new Rect();
  static final Vec2 v1 = new Vec2(), v2 = new Vec2();
  static final Color c = new Color();

  public TextureRegion frame, healthBar, empBar;
  public float healthBarOffsetX, healthBarOffsetY;
  public float empBarOffsetX, empBarOffsetY;
  public float infoOffsetX, infoOffsetY;
  public float sclX = 4, sclY = 4;
  public float infoHeight = 1;

  public static void loadAll(){
    for (HealthBarStyle style : values()) {
      style.load();
    }
  }

  HealthBarStyle(float healthOffX, float healthOffY, float empOffX, float empOffY, float infoOffX, float infoOffY, float infoHeight){
    healthBarOffsetX = healthOffX;
    healthBarOffsetY = healthOffY;
    empBarOffsetX = empOffX;
    empBarOffsetY = empOffY;
    infoOffsetX = infoOffX;
    infoOffsetY = infoOffY;

    this.infoHeight = infoHeight;
  }

  HealthBarStyle(float healthOffX, float healthOffY, float empOffX, float empOffY, float infoOffX, float infoOffY, float sclX, float sclY, float infoHeight){
    healthBarOffsetX = healthOffX;
    healthBarOffsetY = healthOffY;
    empBarOffsetX = empOffX;
    empBarOffsetY = empOffY;
    infoOffsetX = infoOffX;
    infoOffsetY = infoOffY;

    this.sclX = sclX;
    this.sclY = sclY;
    this.infoHeight = infoHeight;
  }

  HealthBarStyle(){}

  public void load(){
    frame = Core.atlas.find(Sgl.modName + "-health_bar_" + name() + "_frame");
    healthBar = Core.atlas.find(Sgl.modName + "-health_bar_" + name());
    empBar = Core.atlas.find(Sgl.modName + "-emp_bar_" + name());
  }

  public <T extends Teamc & Healthc> float draw(float x, float y, EntityInfoFrag.EntityEntry<T> entry, Team team, float dy, float alpha, float scl) {
    T entity = entry.entity;
    float present = Mathf.clamp(entity.health()/entity.maxHealth());
    float empPresent = entity instanceof Unit u? Mathf.clamp(1 - Sgl.empHealth.healthPresent(u)): 0;

    Draw.color(Color.white, alpha);
    float frameWidth = frame.width*frame.scl()*xscl*sclX*scl;
    float frameHeight = frame.height*frame.scl()*yscl*sclY*scl;

    float healthWidth = healthBar.width*healthBar.scl()*xscl*sclX*scl;
    float healthHeight = healthBar.height*healthBar.scl()*yscl*sclY*scl;

    float empWidth = empBar.width*empBar.scl()*xscl*sclX*scl;
    float empHeight = empBar.height*empBar.scl()*yscl*sclY*scl;

    float hOffX = healthBarOffsetX*Draw.scl*xscl*sclX*scl;
    float hOffY = healthBarOffsetY*Draw.scl*yscl*sclY*scl;
    float eOffX = empBarOffsetX*Draw.scl*xscl*sclX*scl;
    float eOffY = empBarOffsetY*Draw.scl*yscl*sclY*scl;
    float iOffX = infoOffsetX*Draw.scl*xscl*sclX*scl;
    float iOffY = infoOffsetY*Draw.scl*yscl*sclY*scl;

    float origX = x - frameWidth/2;

    Draw.rect(frame, x, y + frameHeight/2, frameWidth, frameHeight);

   // drawBar(healthBar, origX, y, hOffX, hOffY, healthWidth, healthHeight, entry.getVar("over", 0f)/entity.maxHealth(), Pal.lightishGray, alpha);
    drawBar(healthBar, origX, y, hOffX, hOffY, healthWidth, healthHeight, present, entity.team().color, alpha);
    if (entity instanceof Shieldc u) drawBar(healthBar, origX, y, hOffX, hOffY, healthWidth, healthHeight, Mathf.clamp(u.shield()/u.maxHealth()), c.set(entity.team().color).lerp(Tmp.c1.set(Pal.accent).mul(1.6f), 0.3f + Mathf.absin(8, 0.7f)), alpha);

    float emp = 1 - empPresent;
    drawBar(empBar, origX, y, eOffX, eOffY, empWidth, empHeight, empPresent,
        Tmp.c1.set(Pal.remove).lerp(Color.white, emp + Mathf.absin(Time.globalTime%360, 1 + emp*4, 1 - emp)), alpha
    );

    String str =
        entity instanceof Unit u? u.type.localizedName + " " + Mathf.round(Mathf.maxZero(u.health)) + "/" + Mathf.round(u.maxHealth):
        entity instanceof Building b? b.block.localizedName + " " + Mathf.round(Mathf.maxZero(b.health)) + "/" + Mathf.round(b.maxHealth): "";

    if (entity instanceof Shieldc u && u.shield() > 0) str += " " + Iconc.defense + Mathf.round(u.shield());
    GlyphLayout l = GlyphLayout.obtain();
    l.setText(Fonts.def, "A");
    float realH = l.height;
    float infoH = infoHeight*yscl*scl;
    l.free();
    Fonts.def.draw(str, origX + iOffX, y + iOffY, Tmp.c1.set(Color.white).a(alpha), infoH/realH, false, Align.topLeft);

    return frameHeight;
  }

  private void drawBar(TextureRegion barRegion, float origX, float origY, float offX, float offY, float width, float height, float present, Color color, float alpha) {
    v1.set(origX + offX, origY + offY);
    v2.set(origX + offX + width * present, origY + offY + height);

    Core.camera.project(v1);
    Core.camera.project(v2);

    if (ScissorStack.push(scissor.set(v1.x, v1.y, v2.x - v1.x, v2.y - v1.y))){
      Draw.color(color, alpha);
      Draw.rect(barRegion, origX + width /2 + offX, origY + height /2 + offY, width, height);
      ScissorStack.pop();
    }
  }

  public float getWidth(float scl) {
    return frame.width*frame.scl()*xscl*sclX*scl;
  }
}
