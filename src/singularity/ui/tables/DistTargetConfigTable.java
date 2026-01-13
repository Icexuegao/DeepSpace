package singularity.ui.tables;

import arc.Core;
import arc.Graphics;
import arc.func.Cons;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Fill;
import arc.graphics.g2d.Lines;
import arc.input.KeyBind;
import arc.input.KeyCode;
import arc.math.Interp;
import arc.math.Mathf;
import arc.math.geom.Geometry;
import arc.math.geom.Point2;
import arc.scene.Element;
import arc.scene.actions.Actions;
import arc.scene.event.ElementGestureListener;
import arc.scene.event.HandCursorListener;
import arc.scene.event.InputEvent;
import arc.scene.event.Touchable;
import arc.scene.ui.Button;
import arc.scene.ui.Image;
import arc.scene.ui.TextButton;
import arc.scene.ui.Tooltip;
import arc.scene.ui.layout.Scl;
import arc.scene.ui.layout.Table;
import arc.struct.ObjectSet;
import arc.struct.SnapshotSeq;
import arc.util.*;
import mindustry.Vars;
import mindustry.ctype.Content;
import mindustry.ctype.ContentType;
import mindustry.ctype.UnlockableContent;
import mindustry.gen.Icon;
import mindustry.gen.Tex;
import mindustry.graphics.Pal;
import mindustry.input.Binding;
import mindustry.ui.Fonts;
import mindustry.ui.Styles;
import singularity.graphic.SglDrawConst;
import singularity.world.blocks.distribute.TargetConfigure;
import singularity.world.distribution.GridChildType;

public class DistTargetConfigTable extends Table{
  private static final ObjectSet<Character> numbers = ObjectSet.with('0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '-');
 
  TargetConfigure config = new TargetConfigure();
  ContentType currType;
  GridChildType currIOType;
  UnlockableContent current;
  byte[] currDireBit;

  Button enter;
  
  Runnable rebuildItems;
  
  public DistTargetConfigTable(int positionOffset, TargetConfigure defaultCfg, GridChildType[] IOTypes,
                               ContentType[] types, boolean directionConfig, Cons<TargetConfigure> cons, Runnable close){
    if(defaultCfg != null){
      config.read(defaultCfg.pack());
    }
    else{
      config.offsetPos = positionOffset;
    }

    currIOType = IOTypes[0];
    
    class Flip extends Element{
      int holdCount;

      float deltaX, deltaY;
      float alpha, texAlpha;
      float pressTime;
      boolean pressing, valid = true, hovering, hold;

      static final KeyCode[] code = {
          ((KeyBind.Axis) Binding.moveX.defaultValue).max,
          ((KeyBind.Axis) Binding.moveY.defaultValue).max,
          ((KeyBind.Axis) Binding.moveX.defaultValue).min,
          ((KeyBind.Axis) Binding.moveY.defaultValue).min,
      };
  
      public Flip(){
        touchable(() -> currDireBit != null? Touchable.enabled: Touchable.disabled);
        update(() -> {
          if (current == null) hold = false;

          if (hold){
            holdCount = 2;

            if (Core.input.axisTap(Binding.moveX) > 0) setDireBit((byte) 1);
            if (Core.input.axisTap(Binding.moveY) > 0) setDireBit((byte) 2);
            if (Core.input.axisTap(Binding.moveX) < 0) setDireBit((byte) 4);
            if (Core.input.axisTap(Binding.moveY) < 0) setDireBit((byte) 8);

            if (Core.input.keyTap(Binding.blockSelectUp)){
              currIOType = IOTypes[Mathf.mod(Structs.indexOf(IOTypes, currIOType) - 1, IOTypes.length)];
              currDireBit = config.getDirectBit(currIOType, current);
            }
            else if (Core.input.keyTap(Binding.blockSelectDown)){
              currIOType = IOTypes[Mathf.mod(Structs.indexOf(IOTypes, currIOType) + 1, IOTypes.length)];
              currDireBit = config.getDirectBit(currIOType, current);
            }

            if (Core.input.keyTap(Binding.menu)){
              Vars.ui.paused.hide(null);
              hold = false;
            }
          }

          texAlpha = Mathf.lerpDelta(texAlpha, hold? 1: 0, 0.05f);
          alpha = Mathf.lerpDelta(alpha, pressing || hovering || hold? 1: 0, 0.045f);

          if(!pressing || !valid){
            deltaX = Mathf.lerpDelta(deltaX, 0, 0.05f);
            deltaY = Mathf.lerpDelta(deltaY, 0, 0.05f);
          }
        });

        if(Core.app.isDesktop() || Core.settings.getBool("keyboard")) {
          Vars.control.input.addLock(() -> holdCount-- > 0);

          hovered(() -> {
            hovering = true;
            Core.graphics.cursor(Graphics.Cursor.SystemCursor.hand);
          });

          exited(() -> {
            hovering = false;
            Core.graphics.restoreCursor();
          });
        }

        addCaptureListener(new ElementGestureListener(){
          @Override
          public void touchDown(InputEvent event, float x, float y, int pointer, KeyCode button){
            super.touchDown(event, x, y, pointer, button);
            pressing = true;
            pressTime = Time.globalTime;
            valid = true;
          }

          @Override
          public void touchUp(InputEvent event, float x, float y, int pointer, KeyCode button){
            super.touchUp(event, x, y, pointer, button);
            if(Time.globalTime - pressTime <= 20) hold = !hold && valid;
            pressing = false;
            valid = true;
          }

          @Override
          public void pan(InputEvent event, float x, float y, float dx, float dy){
            if(valid){
              deltaX += dx;
              deltaY += dy;
              if(deltaX > width){
                setDireBit((byte) 1);
              }
              else if(deltaX < -width){
                setDireBit((byte) 4);
              }
              else if(deltaY > width){
                setDireBit((byte) 2);
              }
              else if(deltaY < -width){
                setDireBit((byte) 8);
              }
            }
            super.pan(event, x, y, deltaX, deltaY);
          }
        });
      }
  
      @Override
      public void draw(){
        validate();

        Draw.color(currDireBit == null? Pal.gray: Color.lightGray);
        Lines.stroke(4.5f);
        Lines.square(this.x + deltaX + width/2f, this.y + deltaY + height/2f,  width/2 + 4.5f/2f*Mathf.sqrt2, 45);
    
        if(currDireBit != null){
          byte bit = 1;
          for(int i = 0; i < 4; i++){
            int dx = Geometry.d4x(i);
            int dy = Geometry.d4y(i);

            Draw.color(Pal.gray);
            Fill.square(x + deltaX + width/2f + dx*width/4*(1 + alpha), y + deltaY + height/2f + dy*height/4*(1 + alpha), width/4/Mathf.sqrt2, 45);
            Draw.color((currDireBit[0] & bit) != 0? Pal.accent: Pal.darkerGray);
            Fill.square(x + deltaX + width/2f + dx*width/4*(1 + alpha), y + deltaY + height/2f + dy*height/4*(1 + alpha),  width/4/Mathf.sqrt2 - 4, 45);

            if(Core.app.isDesktop() || Core.settings.getBool("keyboard")) {
              Fonts.outline.draw(code[i].toString(),
                  x + deltaX + width/2f + dx*width/4*(1 + alpha), y + deltaY + height/2f + dy*height/4*(1 + alpha),
                  Tmp.c1.set((currDireBit[0] & bit) != 0? Pal.accent: Color.white).a(0.7f*texAlpha),
                  1, true, Align.center
              );
            }

            bit *= 2;
          }
        }
      }
  
      private void setDireBit(byte bit){
        if(currDireBit != null){
          currDireBit[0] ^= bit;
        }
        updateCfg();
        valid = false;
      }
    }
    
    table(topBar -> {
      topBar.image(Icon.settings).size(40).left().padLeft(6);
      topBar.add(Core.bundle.get("fragments.configs.nodeConfig")).left().padLeft(4);

      if (!directionConfig) {
        topBar.button(
            t -> t.add("").update(l -> l.setText(Core.bundle.format("misc.mode", currIOType.locale()))),
            Styles.cleart,
            () -> {
              currIOType = IOTypes[Mathf.mod(Structs.indexOf(IOTypes, currIOType) + 1, IOTypes.length)];
              rebuildItems.run();
            }
        ).width(85).padLeft(4).padRight(4).grow().touchable(IOTypes.length > 1 ? Touchable.enabled : Touchable.disabled);
      }
      else topBar.add().grow();

      topBar.add(Core.bundle.get("misc.priority")).right().padRight(4);
      topBar.field(Integer.toString(config.priority),
          (f, c) -> numbers.contains(c),
          str ->{
            try {
              config.priority = str.isEmpty() ? 0 : Integer.parseInt(str);
            }
            catch (NumberFormatException ignored){
              config.priority = 0;
            }
          }).right().width(75).padRight(4);
    }).fillY().growX();

    row();
    image().color(Pal.gray).growX().height(4).colspan(2).pad(0).margin(0);
    row();
    table(main -> {
      main.pane(items -> {
        items.defaults().size(45);

        rebuildItems = () -> {
          items.clearChildren();

          if (currType == null) currType = types[0];

          int count = 0;
          for (Content content : Vars.content.getBy(currType)) {
            if (content instanceof UnlockableContent item && item.unlockedNow()){
              Image button = new Image(item.uiIcon){
                boolean hovering;

                {
                  hovered(() -> hovering = true);
                  exited(() -> hovering = false);

                  addListener(new HandCursorListener());

                  addListener(new Tooltip(t -> t.table(Tex.paneLeft).get().add(item.localizedName)));
                }

                @Override
                public void draw() {
                  float ox = this.x + width/2;
                  float oy = this.y + height/2;

                  if (hovering){
                    Draw.color(Color.gray);
                    Draw.alpha(0.7f);

                    Draw.rect(Core.atlas.white(), ox, oy, width + 8, height + 8);
                  }

                  super.draw();

                  byte[] out = config.getDirectBit(GridChildType.output, item);
                  byte[] in = config.getDirectBit(GridChildType.input, item);
                  byte[] acc = config.getDirectBit(GridChildType.acceptor, item);
                  byte[] cont = config.getDirectBit(GridChildType.container, item);
                  for (int i = 0; i < 4; i++) {
                    if (current == item) {
                      Draw.color(Pal.accent);
                      Draw.alpha(parentAlpha * color.a);
                      Lines.stroke(4);
                      Lines.square(ox, oy, width*0.6f, 45);
                    }

                    if (dirValid(out, i) && (dirValid(in, i) || dirValid(acc, i))) Draw.color(Pal.reactorPurple);
                    else if (dirValid(cont, i)) Draw.color(Pal.accent);
                    else if (dirValid(out, i)) Draw.color(Pal.lightOrange);
                    else if (dirValid(in, i)) Draw.color(Pal.heal);
                    else if (dirValid(acc, i)) Draw.color(Pal.logicControl);
                    else continue;

                    Draw.alpha(parentAlpha * color.a);
                    Point2 po1 = Geometry.d8(i*2 - 1);
                    Point2 po2 = Geometry.d8((i + 1)*2 - 1);
                    Fill.quad(
                        ox + width*po1.x/2 + Scl.scl(po1.x*4), oy + height*po1.y/2 + Scl.scl(po1.y*4),
                        ox + width*po1.x/2, oy + height*po1.y/2,
                        ox + width*po2.x/2, oy + height*po2.y/2,
                        ox + width*po2.x/2 + Scl.scl(po2.x*4), oy + height*po2.y/2 + Scl.scl(po2.y*4)
                    );
                  }
                }
              };
              button.clicked(() -> {
                if (directionConfig){
                  current = current == item? null: item;
                  currDireBit = current == null? null: config.getDirectBit(currIOType, current);
                }
                else {
                  currDireBit = new byte[]{(byte) (config.get(currIOType, item)? 0: 15)};
                  current = item;
                  updateCfg();
                  current = null;
                }
              });

              items.table(t -> t.add(button).size(32).scaling(Scaling.fit));

              if (count++ != 0 && count%5 == 0) items.row();
            }
          }
        };

        rebuildItems.run();
      }).height(180).fillX();
      
      main.image().color(Pal.gray).growY().width(4).colspan(2).padLeft(3).padRight(3).margin(0);
      
      main.table(sideBar -> {
        sideBar.pane(typesTable -> {
          for(ContentType type : types){
            typesTable.button(t -> t.add(Core.bundle.get("content." + type.name() + ".name")), Styles.underlineb, () -> {
              currType = type;
              rebuildItems.run();
            }).growX().height(35).update(b -> b.setChecked(currType == type))
                .touchable(() -> currType == type? Touchable.disabled: Touchable.enabled);
            typesTable.row();
          }
        }).size(120, 80);
        sideBar.row();
        enter = sideBar.button(Core.bundle.get("misc.sure"), Icon.ok, Styles.cleart, () -> {
          cons.get(config);
          close.run();
        }).size(120, 40).update(t -> {
          if(Core.input.keyTap(KeyCode.enter)) t.fireClick();
        }).get();
        sideBar.row();
        sideBar.button(Core.bundle.get("misc.reset"), Icon.cancel, Styles.cleart, () -> {
          config.clear();
          cons.get(config);
          rebuildItems.run();
        }).size(120, 40).update(t -> {
          if(Core.input.keyTap(KeyCode.del)) t.fireClick();
        });
      }).fillX();
    });
    if(directionConfig){
      row();
      image().color(Pal.gray).growX().height(4).colspan(2).padTop(3).padBottom(3).margin(0);
      row();
      table(dirCfg -> {
        dirCfg.table(SglDrawConst.padGrayUIAlpha, infos -> {
          infos.top().defaults().top().left().growX();
          infos.add("").update(l -> l.setText(current == null? Core.bundle.get("infos.selectAItem"): Core.bundle.get("infos.flipCfg")));
          infos.row();
          infos.add().growY();
          infos.row();
          infos.table(buttons -> {
            buttons.left().defaults().left().size(40);
            buttons.button(SglDrawConst.matrixIcon, Styles.clearNonei, 24, () ->  {
              currDireBit = new byte[]{15};
              updateCfg();
            }).disabled(b -> current == null).get().addListener(new Tooltip(t -> t.table(Tex.paneLeft).get().add(Core.bundle.get("misc.allDir"))));
            buttons.button(Icon.cancel, Styles.clearNonei, 24, () ->  {
              currDireBit = new byte[]{0};
              updateCfg();
            }).disabled(b -> current == null || currDireBit[0] <= 0).get().addListener(new Tooltip(t -> t.table(Tex.paneLeft).get().add(Core.bundle.get("misc.clearDir"))));
            buttons.button(Icon.trash, Styles.clearNonei, 24, () ->  {
              GridChildType orig = currIOType;
              for (GridChildType type : IOTypes) {
                currDireBit = new byte[]{0};
                currIOType = type;
                updateCfg();
              }
              currIOType = orig;
            }).disabled(b -> current == null).get().addListener(new Tooltip(t -> t.table(Tex.paneLeft).get().add(Core.bundle.get("misc.clearAllDir"))));
          });
        }).growY().width(150);
        dirCfg.table(SglDrawConst.padGrayUIAlpha, fliper -> {
          fliper.add(new Flip()).size(75);
        });
        dirCfg.table(IOty -> {
          IOty.top().defaults().top();

          TextButton[] currSelecting = new TextButton[]{null, null};

          Table bot = new Table(SglDrawConst.grayUIAlpha){{
            update(() -> {
              if (currSelecting[0] != currSelecting[1]){
                currSelecting[1] = currSelecting[0];

                clearActions();
                actions(Actions.parallel(
                    Actions.sizeTo(currSelecting[0].getWidth(), currSelecting[0].getHeight(), 0.3f),
                    Actions.moveToAligned(currSelecting[0].x, currSelecting[0].y, Align.bottomLeft, 0.3f, Interp.pow2Out)
                ));
              }
            });

            left().add(">", Styles.outlineLabel).left().padLeft(8);
          }};
          IOty.addChild(bot);
          for (GridChildType type : IOTypes) {
            TextButton button = new TextButton(type.locale(), new TextButton.TextButtonStyle(Styles.nonet){{
              fontColor = Color.white;
              checkedFontColor = Pal.accent;
            }}){{
              clicked(() -> {
                currIOType = type;
                currDireBit = config.getDirectBit(currIOType, current);
              });

              update(() -> {
                setChecked(currIOType == type);
                if (currIOType == type) currSelecting[0] = this;
              });

              setDisabled(() -> current == null);
            }};

            IOty.add(button).left().growX().height(30);
            IOty.row();
          }
        }).grow().update(e -> {
          if(Core.input.keyTap(KeyCode.enter)) enter.fireClick();
        });
       SnapshotSeq<Element> seq = dirCfg.getChildren();
       seq.insert(seq.size - 2, seq.pop());
      }).growX();
    }
  }

  private void updateCfg() {
    if (currDireBit[0] > 0) config.set(currIOType, current, currDireBit);
    else config.remove(currIOType, current);
  }

  boolean dirValid(byte[] bits, int dir){
    return bits[0] > 0 && (bits[0] & (1 << dir)) != 0;
  }
}
