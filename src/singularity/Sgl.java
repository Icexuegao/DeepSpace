package singularity;

import arc.Core;
import arc.Events;
import arc.Settings;
import arc.files.Fi;
import arc.util.Log;
import ice.DeepSpace;
import ice.library.IFiles;
import singularity.graphic.ScreenSampler;
import mindustry.Vars;
import mindustry.game.EventType;
import mindustry.game.Team;
import singularity.core.ModConfig;
import singularity.core.ModsInteropAPI;
import singularity.game.researchs.ResearchManager;
import singularity.graphic.MathRenderer;
import singularity.graphic.PostAtlasGenerator;
import singularity.graphic.SglDrawConst;
import singularity.graphic.SglShaders;
import singularity.ui.SglStyles;
import singularity.ui.SglUI;
import singularity.world.blocks.BytePackAssign;
import singularity.world.distribution.DistSupportContainerTable;
import singularity.world.unit.EMPHealthManager;
import universecore.util.mods.ModGetter;
import universecore.util.mods.ModInfo;

public class Sgl {
  public static final String NL = System.lineSeparator();



  /** 本模组的文件位置 */
  public static final ModInfo mod = IFiles.INSTANCE.getModWithClass();
  /** 此模组的压缩包对象 */
  public static final Fi modFile = mod.getFile();

/** 通知标签历史 *//*

  public static final Fi notificationHistory = dataDirectory.child("notifyHistory.bin");
  */
/** 通知标签历史备份 *//*

  public static final Fi notificationHistoryBackup = dataDirectory.child("notifyHistory.bin");
*/

  //URIs
  public static final String modDevelopGroup = "https://jq.qq.com/?_wv=1027&k=vjybgqDG";
  public static final String githubUserAvatars = "https://github.com/";
  public static final String githubProject = "https://github.com/EB-wilson/Singularity";
  public static final String discord = "";
  public static final String githubRawMaster = "https://raw.githubusercontent.com/EB-wilson/Singularity/master/";
  public static final String githubProjReleaseApi = "https://api.github.com/repos/EB-wilson/Singularity/releases/latest";
  public static final String publicInfo = githubRawMaster + "publicInfo/";

  public static final Team none = Team.get(255);

  /** 模组配置存储器 */
  public static ModConfig config = new ModConfig();

  /** ui类存放对象 */
  public static SglUI ui;

  public static DistSupportContainerTable matrixContainers;

  public static EMPHealthManager empHealth;

  public static ResearchManager researches = new ResearchManager();
  public static ModsInteropAPI interopAPI = new ModsInteropAPI();

  public static void init() {
    //注册所有打包数据类型id
    BytePackAssign.INSTANCE.assignAll();
    researches.load();
    researches.init();

    matrixContainers = new DistSupportContainerTable();
    empHealth = new EMPHealthManager();

    matrixContainers.setDefaultSupports();

    interopAPI.init();
    empHealth.init();
    // researches.init();



    if (!Core.app.isHeadless()) {
      generatePostAtlas();
      //设置屏幕采样器
      ScreenSampler.INSTANCE.setup();
      //载入着色器
      SglShaders.load();
      //载入数学着色器
      MathRenderer.load();
      //加载绘制资源
      SglDrawConst.load();
      //载入风格
      SglStyles.load();

      ui = new SglUI();
      ui.init();

      //configNotificationIO();

      //  int count = SglHint.all.size;
      // if (Sgl.config.loadInfo) Log.info("[Singularity][INFO] loading sgl hints, hints count: " + count);
    }

    Events.on(EventType.ClientLoadEvent.class, e -> interopAPI.updateModels());

    /*for (Block block : Vars.content.blocks()) {
      if (block.minfo.mod != null && block.minfo.mod.name.equals(modName) && !(block instanceof SglTurret)){
        PixmapRegion image = Core.atlas.getPixmap(block.region);
        block.squareSprite = image.getA(0, 0) > 0.5f;
      }
    }*/
  }

  private static boolean saving;

/*  private static void configNotificationIO() {
    if (notificationHistory.exists() || notificationHistoryBackup.exists()) {
      try (Reads reads = notificationHistory.reads()) {
        ui.notificationFrag.loadHistory(reads);
      } catch (RuntimeException ignored) {
        if (notificationHistory.exists() && notificationHistoryBackup.exists())
          Log.err("[Singularity] history notification load failed, trying load backup");

        try (Reads reads = notificationHistoryBackup.reads()) {
          ui.notificationFrag.loadHistory(reads);
        } catch (RuntimeException e) {
          Log.err("[Singularity] history notification load failed!", e);
        }
      }
    }

    UpdatePool.receive("notificationAutoSave", () -> {
      if (ui.notificationFrag.shouldSave() && notificationHistory.exists() && !saving) {
        executor.submit(() -> {
          saving = true;
          notificationHistory.copyTo(notificationHistoryBackup);

          try (Writes writes = notificationHistory.writes(false)) {
            ui.notificationFrag.saveHistory(writes);
          }
          saving = false;
        });
      }
    });
  }*/

  private static void generatePostAtlas() {
    Log.info("[Singularity] load post generated atlas");
    Vars.content.each(c -> {
      if (c instanceof PostAtlasGenerator gen) {
        gen.postLoad();
      }
    });
  }
}
