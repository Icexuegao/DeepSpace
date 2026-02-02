package singularity.ui.fragments.notification;

import arc.Core;
import arc.Graphics;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Fill;
import arc.input.KeyCode;
import arc.math.Interp;
import arc.math.Mathf;
import arc.scene.Group;
import arc.scene.actions.Actions;
import arc.scene.actions.DelayAction;
import arc.scene.event.Touchable;
import arc.scene.style.BaseDrawable;
import arc.scene.ui.Button;
import arc.scene.ui.Dialog;
import arc.scene.ui.Label;
import arc.scene.ui.layout.Cell;
import arc.scene.ui.layout.Scl;
import arc.scene.ui.layout.Table;
import arc.struct.ObjectMap;
import arc.struct.Seq;
import arc.util.Align;
import arc.util.Scaling;
import arc.util.Tmp;
import arc.util.io.Reads;
import arc.util.io.Writes;
import mindustry.gen.Icon;
import mindustry.gen.Tex;
import mindustry.graphics.Pal;
import mindustry.ui.Styles;
import singularity.Sgl;
import singularity.graphic.SglDraw;
import singularity.graphic.SglDrawConst;
import singularity.ui.SglStyles;
import universecore.util.DataPackable;

import java.text.DateFormat;

public class NotificationFrag {
  protected final Seq<Notification> history = new Seq<>();
  protected final Seq<Notification> notifyQueue = new Seq<>();

  protected final ObjectMap<Notification, NotifyLogBar> logHistory = new ObjectMap<>();
  protected final Seq<NotifyItemBar> showing = new Seq<>();

  int showingDialogs = 0;
  Group notifies;
  Table main;
  Table windowBack;
  Table historyPane;
  Table historyList;

  boolean historyPaneShown;
  boolean anyHistoryUnsaved;

  public void build(Group parent) {
    parent.fill(main -> {
      this.main = main;
      main.touchable = Touchable.childrenOnly;
      main.left().table(tab -> {
        tab.left().button(t -> t.image(Icon.rightOpen).size(24f).scaling(Scaling.fit), SglStyles.sideButtonRight, () -> {
          notifyQueue.clear();
          for (NotifyItemBar bar : showing) {
            removeNotificationBar(bar);
          }

          showHistoryPane();
        }).width(26f).height(520f).get();

        tab.table(pane -> {
          pane.left();
          pane.add(
              new Table(top -> {
                Cell<Label> lab = top.left().button("\ue86f " + Core.bundle.get("misc.clear"), Styles.nonet, () -> {
                  notifyQueue.clear();
                  for (NotifyItemBar bar : showing) {
                    removeNotificationBar(bar);
                  }
                }).fill().margin(4f).padLeft(8f).padBottom(-4f).get().getLabelCell();
                lab.reset();
                lab.fill();
              }){
                { color.a = 0; }
                boolean switching = false;

                @Override
                public void updateVisibility() {
                  if (notifyQueue.any() || showing.any()) {
                    if (!visible){
                      visible = true;
                      actions(Actions.fadeIn(0.4f));
                    }
                  }
                  else if (visible && !switching){
                    switching = true;
                    actions(
                        Actions.fadeOut(0.4f),
                        Actions.run(() -> switching = false),
                        Actions.hide()
                    );
                  }
                }
              }
          ).grow();
          pane.row();
          pane.add(notifies = new Group() {
            { touchable = Touchable.childrenOnly; }

            @Override
            public void act(float delta) {
              super.act(delta);

              if (!notifyQueue.isEmpty() && showing.size < 5) {
                addNotificationBar(notifyQueue.remove(0));
              }
            }
          }).width(380f).height(440);
          pane.row();
          pane.add(
              new Table(bottom -> {
                bottom.left().add("", Styles.outlineLabel).color(Pal.accent).fill().update(l -> {
                  l.setText("\uf181 +" + Math.max(notifyQueue.size - (5 - showing.size), 0));
                }).padLeft(8f);
              }){
                { color.a = 0; }
                boolean switching = false;

                @Override
                public void updateVisibility() {
                  if (notifyQueue.size - (5 - showing.size) > 0) {
                    if (!visible){
                      visible = true;
                      actions(Actions.fadeIn(0.4f));
                    }
                  }
                  else if (visible && !switching){
                    switching = true;
                    actions(
                        Actions.fadeOut(0.4f),
                        Actions.run(() -> switching = false),
                        Actions.hide()
                    );
                  }
                }
              }
          ).grow();
        }).growY().fillX();
      }).fill();
    });

    parent.addChild(
        windowBack = new Table(SglDrawConst.darkgrayUIAlpha){
          boolean acting = false;

          {
            setFillParent(true);
          }

          @Override
          public void updateVisibility() {
            if (showingDialogs > 0){
              if (!visible) {
                visible = true;
                actions(Actions.fadeIn(0.4f));
              }
            }
            else if (visible && !acting){
              acting = true;
              actions(
                  Actions.fadeOut(0.4f),
                  Actions.run(() -> acting = false),
                  Actions.hide()
              );
            }
          }
        }
    );

    parent.addChild(historyPane = new Table(Tex.pane).margin(4f).top());
    historyPane.setSize(
        Math.min(Core.graphics.getWidth()*0.75f, Scl.scl(1000f)),
        Math.min(Core.graphics.getHeight()*0.75f, Scl.scl(700f))
    );
    historyPane.setPosition(-historyPane.getWidth()/2, Core.graphics.getHeight()/2f, Align.center);
    historyPane.visible = false;

    historyPane.table(pane -> {
      pane.top().table(top -> {
        top.add().size(32f);
        top.add(Core.bundle.get("infos.notificationHistory"), Styles.outlineLabel).color(Pal.accent).fontScale(1.1f).growX().labelAlign(Align.center);
        top.button(Icon.cancel, Styles.clearNonei, 24f, this::hideHistoryPane).size(32f);
      }).fillY().growX();
      pane.row();
      pane.pane(list -> historyList = list).fillY().growX();
    }).grow();

    historyPane.row();
    historyPane.table(bottom -> {
      bottom.add(Core.bundle.format("infos.logItems", 0), Styles.outlineLabel).fill().fontScale(0.8f).left().color(Pal.accent).pad(4f)
          .update(l -> l.setText(Core.bundle.format("infos.logItems", history.size)));
      bottom.add().growX();
      bottom.button(b -> {
        b.add(Core.bundle.get("infos.saveLatest100")).padLeft(4f).fontScale(0.8f)
            .update(l -> l.setColor(b.isDisabled()? Color.gray: Color.white));
        b.image(Icon.layersSmall).size(26f)
            .update(l -> l.setColor(b.isDisabled()? Color.gray: Color.white));
      }, Styles.clearNonei, () -> {
        history.removeRange(100, history.size - 1);
        rebuildHistory();
        anyHistoryUnsaved = true;
      }).fill().pad(4f).margin(4f).disabled(b -> history.size <= 100);
      bottom.button(b -> {
        b.add(Core.bundle.get("misc.clear")).padLeft(4f).fontScale(0.8f);
        b.image(Icon.trashSmall).size(26f);
      }, Styles.clearNonei, () -> {
        history.clear();
        rebuildHistory();
        anyHistoryUnsaved = true;
      }).fill().pad(4f).margin(4f);
    }).growX().fillY();

    historyPane.update(() -> {
      historyList.forEach(item -> {
        if (item instanceof NotifyLogBar i && historyList.getCullingArea() != null
            && historyList.getCullingArea().overlaps(i.x, i.y, i.getWidth(), i.getHeight())){
          if (!i.notification.readed) {
            i.notification.readed = true;
            anyHistoryUnsaved = true;
          }
        }
      });
    });
  }

  public void notify(Notification notification) {
    history.insert(0, notification);
    if (history.size > Sgl.config.maxNotifyHistories) history.removeRange(Sgl.config.maxNotifyHistories, history.size - 1);
    if (!historyPaneShown) notifyQueue.add(notification);

    rebuildHistory();

    anyHistoryUnsaved = true;
  }

  private void rebuildHistory() {
    historyList.clearChildren();
    for (Notification notification : history) {
      historyList.add(logHistory.get(notification, () -> new NotifyLogBar(notification))).growX().height(80f).pad(4f);
      historyList.row();
    }
  }

  protected void showHistoryPane() {
    historyPaneShown = true;
    showingDialogs++;
    historyPane.visible = true;
    historyPane.actions(
        Actions.moveToAligned(-historyPane.getWidth()/2, Core.graphics.getHeight()/2f, Align.center),
        Actions.moveToAligned(
            Core.graphics.getWidth()/2f, Core.graphics.getHeight()/2f, Align.center,
            0.5f, Interp.pow3Out
        )
    );
    main.actions(
        Actions.fadeOut(0.5f),
        Actions.hide()
    );
  }

  protected void hideHistoryPane() {
    historyPaneShown = false;
    showingDialogs--;
    historyPane.actions(
        Actions.moveToAligned(Core.graphics.getWidth()/2f, Core.graphics.getHeight()/2f, Align.center),
        Actions.moveToAligned(
            -historyPane.getWidth()/2, Core.graphics.getHeight()/2f, Align.center,
            0.5f, Interp.pow3In
        ),
        Actions.hide()
    );
    main.visible = true;
    main.actions(
        Actions.fadeIn(0.5f)
    );
  }

  protected void addNotificationBar(Notification notification) {
    NotifyItemBar bar = new NotifyItemBar(notification);
    bar.index = showing.size;

    notifies.addChild(bar);
    bar.color.a = 0f;
    bar.setSize(Scl.scl(380f), Scl.scl(80f));
    bar.x = -bar.getWidth();
    bar.y = showing.isEmpty()? notifies.getHeight() - bar.getHeight(): showing.peek().getY(Align.bottom) - Scl.scl(10f) - bar.getHeight();

    showing.add(bar);
    if (notification.duration < 0){
      bar.actions(
          Actions.parallel(
              Actions.fadeIn(0.3f),
              Actions.moveBy(bar.getWidth(), 0, 0.3f, Interp.pow3Out)
          )
      );
    }
    else {
      bar.actions(
          Actions.parallel(
              Actions.fadeIn(0.3f),
              Actions.moveBy(bar.getWidth(), 0, 0.3f, Interp.pow3Out)
          ),
          bar.act = Actions.delay(notification.duration),
          Actions.run(() -> removeNotificationBar(bar))
      );
    }

    if (notification.activeWindow && notification.buildWindow){
      showWindow(notification);
    }

    if (bar.act != null) bar.act.setPool(null);
  }

  protected void showWindow(Notification notification) {
    showingDialogs++;
    new Dialog("", SglStyles.transparentBack){
      final int lay = showingDialogs;
      {
        titleTable.clear();

        cont.table(Tex.pane, pane -> {
          pane.table(topBar -> {
            topBar.top().defaults().top();
            topBar.add().size(32f);
            topBar.add(notification.title).growX().padTop(12f).labelAlign(Align.center).fontScale(1.2f)
                .update(l -> l.setColor(notification.getTitleColor()));
            topBar.button(Icon.cancel, Styles.clearNonei, 24, () -> {
              hide(null);
              showingDialogs--;
            }).size(32f);
          }).growX().fillX();
          pane.row();
          pane.table(notification::buildWindow).fillY().growX();
        }).fill().margin(4f);
      }

      @Override
      public void updateVisibility() {
        visible = lay == showingDialogs;
      }
    }.show(Core.scene, null ).centerWindow();
  }

  protected void removeNotificationBar(NotifyItemBar bar) {
    boolean cont = showing.contains(bar);

    if (cont){
      bar.clearActions();
      bar.actions(
          Actions.parallel(
              Actions.fadeOut(0.3f),
              Actions.moveBy(-bar.getWidth(), 0, 0.3f, Interp.pow3In)
          ),
          Actions.run(() -> {
            showing.remove(bar);
            for (int i = bar.index; i < showing.size; i++){
              showing.get(i).index = i;
            }
            bar.remove();
          })
      );
    }
  }

  public boolean shouldSave(){
    return anyHistoryUnsaved;
  }

  public void saveHistory(Writes write){
    write.i(history.size);
    for (Notification n : history) {
      byte[] bytes = n.pack();
      write.i(bytes.length);
      write.b(bytes);
    }
    anyHistoryUnsaved = false;
  }

  public void loadHistory(Reads read){
    history.clear();
    int n = read.i();
    for (int i = 0; i < n; i++) {
      history.add(DataPackable.<Notification>readObject(read.b(read.i())));
    }
  }

  protected class NotifyItemBar extends Table {
    public final Notification notification;
    public DelayAction act;
    public int index;

    boolean hovered;
    boolean hovering;
    float progress = 0;
    float lerp = 0;

    private void colorLerp(float start, float end) {
      float prog = Mathf.clamp((Interp.pow2.apply(lerp) - start)/(end - start));
      Draw.color(
          Tmp.c1.set(Pal.darkestGray).lerp(Pal.accent, prog),
          Mathf.lerp(0.3f, 0.7f, 1 - prog)*parentAlpha
      );
    }

    public NotifyItemBar(Notification noti) {
      super(Tex.paneLeft);
      margin(0);
      marginLeft(4f);
      this.notification = noti;

      touchable = Touchable.enabled;

      table(new BaseDrawable(){
        @Override
        public void draw(float x, float y, float width, float height) {
          colorLerp(0, 0.4f);
          Fill.rect(x + width/2f, y + height/2f, width, height);
        }
      }, img -> {
        img.table(new BaseDrawable(){
          @Override
          public void draw(float x, float y, float width, float height) {
            if (act == null) return;

            Draw.color(Pal.darkestGray, parentAlpha);
            Fill.circle(x + width/2f, y + height/2f, width/2f - Scl.scl(4f));

            Color col = noti.getIconColor();
            SglDraw.drawCircleProgress(
                x + width/2f, y + height/2f, width/2f,
                Scl.scl(6f), Scl.scl(3f), Interp.pow2Out.apply(progress),
                col, Tmp.c1.set(col).lerp(Color.black, 0.4f)
            );
          }
        }, i -> {
          i.image(notification.getIcon()).size(32f).scaling(Scaling.fit).pad(10f)
              .update(im -> {
                im.setColor(noti.getIconColor());
                im.setDrawable(notification.getIcon());
              });
        }).fill().pad(0);
      }).fillX().growY().margin(4f);

      table(new BaseDrawable(){
        @Override
        public void draw(float x, float y, float width, float height) {
          colorLerp(0, 0.4f);
          Fill.tri(x, y, x, y + height, x + width/3f, y);

          colorLerp(0.2f, 0.6f);
          Fill.quad(
              x + width/3f + Scl.scl(45f), y,
              x + Scl.scl(45f), y + height,
              x + Scl.scl(95f), y + height,
              x + width/3f + Scl.scl(95f), y
          );
          colorLerp(0.4f, 0.8f);
          Fill.quad(
              x + width/3f + Scl.scl(130f), y,
              x + Scl.scl(130f), y + height,
              x + Scl.scl(160f), y + height,
              x + width/3f + Scl.scl(160f), y
          );
          colorLerp(0.6f, 1f);
          Fill.quad(
              x + width/3f + Scl.scl(190f), y,
              x + Scl.scl(190f), y + height,
              x + Scl.scl(200f), y + height,
              x + width/3f + Scl.scl(200f), y
          );
        }
      }, inf -> {
        inf.add(notification.title).growX().labelAlign(Align.left).fontScale(1.1f)
            .update(l -> l.setColor(noti.getTitleColor()));
        inf.row();
        inf.add(notification.information).growX().wrap().labelAlign(Align.left)
            .update(l -> l.setColor(noti.getInformationColor()));
      }).pad(0).grow().marginLeft(4f);

      button(Icon.leftOpenSmall, Styles.clearNonei, 22f, () -> {
        removeNotificationBar(this);
        notification.readed = true;
      }).margin(4).growY().get().addListener(event -> {
        event.stop();
        return false;
      });

      clicked(KeyCode.mouseLeft, () -> {
        if (notification.buildWindow) showWindow(notification);
        noti.activity();
      });

      hovered(() -> {
        hovered = true;
        hovering = true;

        Core.graphics.cursor(Graphics.Cursor.SystemCursor.hand);
      });
      exited(() -> {
        hovering = false;

        Core.graphics.restoreCursor();
      });
    }

    @Override
    public void act(float delta) {
      lerp = Mathf.lerpDelta(lerp, hovering ? 1 : 0, 0.04f);

      if (act != null && (hovered || showingDialogs > 0)){
        act.restart();
      }

      if (act != null) {
        progress = Mathf.lerpDelta(progress, act.getTime()/act.getDuration(), 0.1f);
      }

      super.act(delta);

      if (index - 1 < showing.size && index > 0){
        NotifyItemBar last = showing.get(index - 1);
        y = Mathf.lerpDelta(y, last.getY(Align.bottom) - Scl.scl(10f) - height, 0.2f);
      }
      else {
        y = Mathf.lerpDelta(y, notifies.getHeight() - height, 0.2f);
      }
    }
  }

  protected class NotifyLogBar extends Button {
    public final Notification notification;

    public float lerp;

    private void colorLerp() {
      float prog = Interp.pow2.apply(lerp);
      Draw.color(
          Tmp.c1.set(Pal.darkestGray).lerp(Pal.accent, prog),
          Mathf.lerp(0.3f, 0.7f, 1 - prog)*parentAlpha
      );
    }

    public NotifyLogBar(Notification noti) {
      super(SglStyles.underline);
      margin(0f);
      marginBottom(4f);
      marginTop(4f);
      this.notification = noti;
      this.lerp = noti.readed? 0: 1;

      touchable = Touchable.enabled;

      table(new BaseDrawable(){
        @Override
        public void draw(float x, float y, float width, float height) {
          colorLerp();
          Fill.rect(x + width/2f, y + height/2f, width, height);
        }
      }, img -> {
        img.table(i -> {
          i.image(notification.getIcon()).size(32f).scaling(Scaling.fit).pad(10f)
              .update(im -> {
                im.setColor(noti.getIconColor());
                im.setDrawable(notification.getIcon());
              });
        }).fill().pad(0);
      }).fillX().growY().margin(4f);

      table(new BaseDrawable(){
        @Override
        public void draw(float x, float y, float width, float height) {
          colorLerp();
          float c1 = Draw.getColor().toFloatBits();
          float c2 = Tmp.c1.set(Draw.getColor()).a(0).toFloatBits();
          float prog = Interp.pow2.apply(lerp);
          Fill.quad(
              x, y, c1,
              x, y + height, c1,
              x + width*prog, y + height, c2,
              x + width*prog, y, c2
          );
        }
      }, inf -> {
        inf.table(i -> {
          i.add(notification.title).growX().labelAlign(Align.left).fontScale(1.1f)
              .update(lab -> lab.setColor(noti.getTitleColor()));
          i.row();
          i.add(notification.information).growX().wrap().labelAlign(Align.left)
              .update(lab -> lab.setColor(noti.getInformationColor()));
        }).growX();
        inf.table(dat -> {
          dat.add(DateFormat.getDateInstance(DateFormat.DEFAULT, Core.bundle.getLocale()).format(notification.date))
              .growX().labelAlign(Align.right).color(Color.lightGray);
          dat.row();
          dat.add(DateFormat.getTimeInstance(DateFormat.DEFAULT, Core.bundle.getLocale()).format(notification.date))
              .growX().labelAlign(Align.right).color(Color.lightGray);
        }).fillX();
      }).pad(0).grow().margin(4f);

      button(Icon.leftOpenSmall, Styles.clearNonei, 22f, () -> {

      }).margin(4).growY().get().addListener(event -> {
        event.stop();
        return false;
      });

      clicked(KeyCode.mouseLeft, () -> {
        if (noti.buildWindow) {
          showWindow(notification);
        }
        noti.activity();
      });

      hovered(() -> {
        Core.graphics.cursor(Graphics.Cursor.SystemCursor.hand);
      });
      exited(() -> {
        Core.graphics.restoreCursor();
      });
    }

    @Override
    public void act(float delta) {
      super.act(delta);

      if (notification.readed){
        lerp = Mathf.approachDelta(lerp, 0, 0.005f);
      }
    }
  }
}
