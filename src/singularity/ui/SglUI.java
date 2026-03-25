package singularity.ui;

import arc.Core;
import arc.Events;
import arc.util.Time;
import ice.core.SettingValue;
import mindustry.Vars;
import mindustry.game.EventType;
import mindustry.gen.Healthc;
import mindustry.gen.Teamc;
import mindustry.gen.Unit;
import mindustry.ui.Styles;
import singularity.Sgl;
import singularity.core.SglEventTypes;
import singularity.core.UpdatePool;
import singularity.graphic.Blur;
import singularity.ui.dialogs.*;
import singularity.ui.fragments.DebugInfos;
import singularity.ui.fragments.entityinfo.EntityHealthDisplay;
import singularity.ui.fragments.entityinfo.EntityInfoFrag;
import singularity.ui.fragments.entityinfo.UnitStatusDisplay;
import singularity.ui.fragments.notification.Notification;
import singularity.ui.fragments.notification.NotificationFrag;
import singularity.ui.fragments.override.SglMenuFrag;

@SuppressWarnings("DuplicatedCode")
public class SglUI {
  //ui相关
  public EntityInfoFrag entityInfoFrag;

  /** 主菜单 */
  public MainMenu mainMenu;
  public PublicInfoDialog publicInfo;
  public DocumentDialog document;

  public SupportUsDialog support;

  public DistNetMonitorDialog bufferStat;

  public UnitFactoryCfgDialog unitFactoryCfg;
  public SglTechTreeDialog techTreeDialog;

  public DebugInfos debugInfos;
  public NotificationFrag notificationFrag;

  public static Blur uiBlur = new Blur(Blur.DEf_B);

  static {
    UpdatePool.INSTANCE.receive("syncUIBlurCfg", () -> {
      uiBlur.blurScl = Sgl.config.blurLevel;
      uiBlur.blurSpace = Sgl.config.backBlurLen;

      SglStyles.blurBack.stageBackground = Sgl.config.enableBlur ? SglStyles.BLUR_BACK : Styles.black9;
      Styles.defaultDialog.stageBackground = Sgl.config.enableBlur ? SglStyles.BLUR_BACK : Styles.black9;
    });
  }

  public static final Object[][] grapPreset = {
          {1, false, 0.25f, false, false, 64, false},
          {2, true, 0.5f, false, true, 256, false},
          {2, true, 0.75f, false, true, 512, true},
          {3, true, 1f, true, true, 1024, true},
          {3, true, 1f, true, true, 4096, true},
  };


  public void init() {
    entityInfoFrag = new EntityInfoFrag();
    entityInfoFrag.displayMatcher.put(new EntityHealthDisplay<>(), e -> e instanceof Teamc && e instanceof Healthc);
    entityInfoFrag.displayMatcher.put(new UnitStatusDisplay<>(), e -> e instanceof Unit);

    mainMenu = new MainMenu();
    publicInfo = new PublicInfoDialog();

    support = new SupportUsDialog();
    bufferStat = new DistNetMonitorDialog();
    document = new DocumentDialog();
    unitFactoryCfg = new UnitFactoryCfgDialog();

    debugInfos = new DebugInfos();
    notificationFrag = new NotificationFrag();

    entityInfoFrag.build(Vars.ui.hudGroup);
    notificationFrag.build(Vars.ui.hudGroup);
    Vars.ui.hints.build(Vars.ui.hudGroup);

    mainMenu.build();

    unitFactoryCfg.build();

    debugInfos.build(Vars.ui.hudGroup);

    if (!SettingValue.INSTANCE.get禁用mod主界面背景()) {
      Vars.ui.menufrag = new SglMenuFrag();
      Vars.ui.menufrag.build(Vars.ui.menuGroup);
    }


    configEventListeners();

    //添加设置项入口
    /*Vars.ui.settings.shown(() -> {
      Table table = FieldHandler.getValueDefault(Vars.ui.settings, "menu");
      table.button(
              Core.bundle.get("settings.singularity"),
              SglDrawConst.sglIcon,
              Styles.flatt,
              32,
              () -> Sgl.ui.config.show()
      ).marginLeft(8).row();
    });*/
    //setConfigItems();
  }

  void configEventListeners() {
    Events.on(EventType.WorldLoadEndEvent.class, e -> {
      for (int i = 0; i < 1; i++) {
        int fi = i;
        Time.run(60 + 30*i, () -> notificationFrag.notify(
            new Notification.Warning(
                "notification test " + fi,
                "notification message"
            )
        ));
      }

     /* Time.run(390, () -> {
        Events.fire(new SglEventTypes.ResearchInspiredEvent(
                SglTechThree.test14.getInspire(), SglTechThree.test14
        ));
      });
      Time.run(420, () -> {
        Events.fire(new SglEventTypes.ResearchCompletedEvent(
            SglTechThree.test14
        ));
      });*/
    });

    Events.on(SglEventTypes.ResearchCompletedEvent.class, e -> notificationFrag.notify(
            new Notification.ResearchCompleted(
                    Core.bundle.get("infos.researchCompleted"),
                    Core.bundle.format("infos.researched", e.getResearch().getLocalizedName()),
                    e.getResearch()
            )
    ));

    Events.on(SglEventTypes.ResearchInspiredEvent.class, e -> notificationFrag.notify(
            new Notification.Inspired(
                    Core.bundle.get("infos.inspired"),
                    Core.bundle.format("infos.inspiredBy", e.getResearch().getLocalizedName()),
                    e.getInspire(), e.getResearch()
            )
    ));
  }




}
