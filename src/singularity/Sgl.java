package singularity;

import arc.Core;
import arc.Events;
import singularity.graphic.ScreenSampler;
import mindustry.game.EventType;
import singularity.core.ModConfig;
import singularity.core.ModsInteropAPI;
import singularity.game.researchs.ResearchManager;
import singularity.graphic.MathRenderer;
import singularity.graphic.SglDrawConst;
import singularity.graphic.SglShaders;
import singularity.ui.SglStyles;
import singularity.ui.SglUI;
import singularity.world.blocks.BytePackAssign;
import singularity.world.distribution.DistSupportContainerTable;
import singularity.world.unit.EMPHealthManager;

public class Sgl {
  public static final String NL = System.lineSeparator();

  //URIs
  public static final String modDevelopGroup = "https://jq.qq.com/?_wv=1027&k=vjybgqDG";
  public static final String githubUserAvatars = "https://github.com/";
  public static final String githubProject = "https://github.com/EB-wilson/Singularity";
  public static final String discord = "";
  public static final String githubRawMaster = "https://raw.githubusercontent.com/EB-wilson/Singularity/master/";
  public static final String githubProjReleaseApi = "https://api.github.com/repos/EB-wilson/Singularity/releases/latest";
  public static final String publicInfo = githubRawMaster + "publicInfo/";

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

  }



}
