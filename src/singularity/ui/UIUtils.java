package singularity.ui;

import arc.Core;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Fill;
import arc.graphics.g2d.Lines;
import arc.math.Mathf;
import arc.scene.Element;
import arc.scene.style.BaseDrawable;
import arc.scene.ui.layout.Collapser;
import arc.scene.ui.layout.Scl;
import arc.scene.ui.layout.Table;
import arc.util.Align;
import arc.util.Scaling;
import arc.util.Strings;
import arc.util.Time;
import mindustry.Vars;
import mindustry.content.StatusEffects;
import mindustry.ctype.UnlockableContent;
import mindustry.entities.bullet.BulletType;
import mindustry.gen.Icon;
import mindustry.gen.Tex;
import mindustry.graphics.Pal;
import mindustry.ui.Styles;
import mindustry.world.meta.StatUnit;
import singularity.contents.OtherContents;
import singularity.game.researchs.Inspire;
import singularity.game.researchs.ResearchProject;
import singularity.graphic.SglDraw;
import singularity.graphic.SglDrawConst;
import singularity.world.blocks.turrets.EmpBulletType;
import singularity.world.blocks.turrets.HeatBulletType;
import universecore.ui.elements.BloomGroup;

import static mindustry.Vars.tilesize;

public class UIUtils {
  public static void buildResearchComplete(Table table, ResearchProject project) {
    table.stack(
        new BloomGroup(){{
          setFillParent(true);
          addChild(new Element(){
            { setFillParent(true); }

            @Override
            public void draw() {
              super.draw();
              Draw.color(SglDrawConst.matrixNet, Draw.getColor().a);
              Fill.circle(getX(Align.center), getY(Align.center), getWidth()/2 + Scl.scl(2f));
            }
          });
        }},
        new Table(new BaseDrawable(){
          @Override
          public void draw(float x, float y, float width, float height) {
            float parentAlpha = Draw.getColor().a;
            Draw.color(Pal.darkestGray, parentAlpha);
            Fill.circle(x + width/2f, y + height/2f, width/2f - Scl.scl(7f));

            Draw.color(Color.black, parentAlpha);
            Lines.stroke(Scl.scl(14f));
            Lines.circle(x + width/2f, y + height/2f, width/2f - Scl.scl(7f));

            Draw.color(SglDrawConst.matrixNet, 0.7f*parentAlpha);
            Lines.circle(x + width/2f, y + height/2f, width/2f - Scl.scl(7f));
            Lines.stroke(Scl.scl(4f));
            Draw.color(SglDrawConst.matrixNet, parentAlpha);
            Lines.circle(x + width/2f, y + height/2f, width/2f - Scl.scl(9f));
            SglDraw.dashCircle(x + width/2f, y + height/2f, width/2f - Scl.scl(5f),
                8, 180, Time.time);
          }
        }, img -> {
          img.image(project.icon == null? project.contents.first().uiIcon: project.icon).size(80f);
        }).margin(30f)
    ).pad(40f).padTop(30f).padBottom(30f);
    table.row();
    table.stack(
        new Table(){{
          image().color(SglDrawConst.matrixNet).grow();
          row();
          image().color(SglDrawConst.matrixNetDark).growX().height(4f);
        }},
        new Table(){{
          add(project.localizedName, Styles.outlineLabel).pad(6f).color(Pal.accent);
        }}
    ).growX();
    table.row();
    table.add(project.description).pad(10f).padTop(30f).padBottom(30f).width(320f).growX().wrap().labelAlign(Align.center);
    table.row();
    table.add(Core.bundle.get("infos.unlocked")).fontScale(0.8f).color(Color.lightGray);
    table.row();
    table.table(conts -> {
      int n = 0;
      for (UnlockableContent content : project.contents) {
        conts.button(b -> b.image(content.uiIcon).grow().scaling(Scaling.fit).pad(4f), Styles.cleart, () -> {
          Vars.ui.content.show(content);
        }).size(42f).padLeft(4f);

        if (++n%6 == 0) conts.row();
      }
    }).pad(20).padBottom(10);
    table.row();
    table.table(Tex.buttonTrans, slogan -> {
      slogan.add(project.slogan).pad(4f).grow().wrapLabel(true).color(Color.lightGray).fontScale(0.8f).labelAlign(Align.center);
    }).width(300f).minHeight(80f).fillY().pad(10).padBottom(30).padTop(0);
  }

  public static void buildResearchInspired(Table table, Inspire inspire, ResearchProject project) {
    float[] prog = {0};

    table.add(inspire.description).pad(10f).padTop(30f).growX().labelAlign(Align.center).wrapLabel(true);
    table.row();
    table.table(Tex.buttonSideRightOver, card -> {
      card.update(() -> {
        prog[0] = Mathf.approachDelta(prog[0], project.inspire.provProgress, 0.005f);
      });
      card.table(SglDrawConst.grayUIAlpha, img -> {
        img.stack(
            new BloomGroup(){{
              setFillParent(true);
              bloomIntensity = 1.8f;
              addChild(new Element(){
                { setFillParent(true); }

                @Override
                public void draw() {
                  super.draw();
                  Draw.color(SglDrawConst.matrixNet, Draw.getColor().a);
                  Fill.circle(getX(Align.center), getY(Align.center), getWidth()/2);
                }
              });
            }},
            new Table(new BaseDrawable(){
              @Override
              public void draw(float x, float y, float width, float height) {
                float parentAlpha = Draw.getColor().a;
                Draw.color(Pal.darkestGray, parentAlpha);
                Fill.circle(x + width/2f, y + height/2f, width/2f - Scl.scl(4f));

                SglDraw.drawCircleProgress(
                    x + width/2, y + height/2, width/2f,
                    Scl.scl(6f), Scl.scl(3f),
                    project.progress() + prog[0],
                    project.inspire.provProgress - prog[0],
                    SglDrawConst.matrixNet, SglDrawConst.matrixNet
                );
              }
            }, i -> {
              i.image(project.icon != null ? project.icon : project.contents.first().uiIcon).size(32).scaling(Scaling.fit);
            })
        ).grow().pad(4f);
      }).width(64f).growY();
      card.table(new BaseDrawable(){
        @Override
        public void draw(float x, float y, float width, float height) {
          float parentAlpha = Draw.getColor().a;
          Draw.color(Pal.darkerGray, 0.7f*parentAlpha);
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
        info.add(project.localizedName).growX().color(Pal.accent).fontScale(1.1f).labelAlign(Align.center).pad(5f);
        info.row();
        info.add(Core.bundle.get("infos.researchInspired")).growX().labelAlign(Align.center).pad(5f);
      }).left().grow().margin(8f);

      card.getChildren().reverse();
    }).width(420).fillY().pad(20f).margin(4f);
  }

  public static void buildAmmo(Table table, BulletType bullet){
    table.left().defaults().padRight(3).left();

    if(bullet.damage > 0){
      if(bullet.continuousDamage() > 0){
        sep(table, Core.bundle.format("bullet.damage", bullet.continuousDamage()) + StatUnit.perSecond.localized());
      }else{
        sep(table, Core.bundle.format("bullet.damage", bullet.damage));
      }
    }

    if (bullet instanceof EmpBulletType emp){
      sep(table, Core.bundle.format("bullet.empDamage", emp.empDamage, emp.empRange > 0? "[lightgray]~ [accent]" + emp.empRange/tilesize + "[lightgray]" + StatUnit.blocks.localized() : ""));
    }

    if (bullet instanceof HeatBulletType heat){
      table.row();
      table.table(t -> {
        t.left().defaults().padRight(3).left();
        t.image(OtherContents.meltdown.uiIcon).size(25).scaling(Scaling.fit);
        t.add(Core.bundle.format("infos.heatAmmo", Strings.autoFixed(heat.meltDownTime/60, 1), Strings.autoFixed(heat.melDamageScl*60, 1), heat.maxExDamage > 0? heat.maxExDamage: Math.max(heat.damage, heat.splashDamage)));
      });
    }

    if(bullet.buildingDamageMultiplier != 1){
      sep(table, Core.bundle.format("bullet.buildingdamage", (int) (bullet.buildingDamageMultiplier*100)));
    }

    if(bullet.rangeChange != 0){
      sep(table, Core.bundle.format("bullet.range", (bullet.rangeChange > 0? "+": "-") + Strings.autoFixed(bullet.rangeChange/tilesize, 1)));
    }

    if(bullet.splashDamage > 0){
      sep(table, Core.bundle.format("bullet.splashdamage", (int) bullet.splashDamage, Strings.fixed(bullet.splashDamageRadius/tilesize, 1)));
    }

    if(bullet.knockback > 0){
      sep(table, Core.bundle.format("bullet.knockback", Strings.autoFixed(bullet.knockback, 2)));
    }

    if(bullet.healPercent > 0f){
      sep(table, Core.bundle.format("bullet.healpercent", Strings.autoFixed(bullet.healPercent, 2)));
    }

    if(bullet.healAmount > 0f){
      sep(table, Core.bundle.format("bullet.healamount", Strings.autoFixed(bullet.healAmount, 2)));
    }

    if(bullet.pierce || bullet.pierceCap != -1){
      sep(table, bullet.pierceCap == -1? "@bullet.infinitepierce": Core.bundle.format("bullet.pierce", bullet.pierceCap));
    }

    if(bullet.incendAmount > 0){
      sep(table, "@bullet.incendiary");
    }

    if(bullet.homingPower > 0.01f){
      sep(table, "@bullet.homing");
    }

    if(bullet.lightning > 0){
      sep(table, Core.bundle.format("bullet.lightning", bullet.lightning, bullet.lightningDamage < 0? bullet.damage: bullet.lightningDamage));
    }

    if(bullet.pierceArmor){
      sep(table, "@bullet.armorpierce");
    }

    if(bullet.status != StatusEffects.none && bullet.status != null){
      sep(table, (bullet.status.minfo.mod == null? bullet.status.emoji(): "") + "[stat]" + bullet.status.localizedName + "[lightgray] ~ " +
          "[stat]" + Strings.autoFixed(bullet.statusDuration/60f, 1) + "[lightgray] " + Core.bundle.get("unit.seconds"));
    }

    if(bullet.intervalBullet != null){
      table.row();

      Table ic = new Table();
      buildAmmo(ic, bullet.intervalBullet);
      Collapser coll = new Collapser(ic, true);
      coll.setDuration(0.1f);

      table.table(it -> {
        it.left().defaults().left();

        it.add(Core.bundle.format("bullet.interval", Strings.autoFixed(bullet.intervalBullets / bullet.bulletInterval * 60, 2)));
        it.button(Icon.downOpen, Styles.emptyi, () -> coll.toggle(false)).update(i -> i.getStyle().imageUp = (!coll.isCollapsed() ? Icon.upOpen : Icon.downOpen)).size(8).padLeft(16f).expandX();
      });
      table.row();
      table.add(coll).padLeft(16);
    }

    if(bullet.fragBullet != null){
      table.row();

      Table ic = new Table();
      buildAmmo(ic, bullet.fragBullet);
      Collapser coll = new Collapser(ic, true);
      coll.setDuration(0.1f);

      table.table(ft -> {
        ft.left().defaults().left();

        ft.add(Core.bundle.format("bullet.frags", bullet.fragBullets));
        ft.button(Icon.downOpen, Styles.emptyi, () -> coll.toggle(false)).update(i -> i.getStyle().imageUp = (!coll.isCollapsed() ? Icon.upOpen : Icon.downOpen)).size(8).padLeft(16f).expandX();
      });
      table.row();
      table.add(coll).padLeft(16);
    }

    table.row();
  }

  private static void sep(Table table, String text){
    table.row();
    table.add(text);
  }
}
