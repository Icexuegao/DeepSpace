package singularity;

import arc.Core;
import arc.Events;
import arc.Settings;
import arc.files.Fi;
import arc.util.Log;
import arc.util.Strings;
import arc.util.Threads;
import arc.util.io.Reads;
import arc.util.io.Writes;
import mindustry.Vars;
import mindustry.game.EventType;
import mindustry.game.Team;
import singularity.core.ModConfig;
import singularity.core.ModsInteropAPI;
import singularity.core.UpdatePool;
import singularity.game.SingularityGameLogic;
import singularity.game.researchs.ResearchManager;
import singularity.graphic.*;
import singularity.ui.SglStyles;
import singularity.ui.SglUI;
import singularity.world.blocks.BytePackAssign;
import singularity.world.distribution.DistSupportContainerTable;
import singularity.world.unit.EMPHealthManager;
import universecore.util.mods.ModGetter;
import universecore.util.mods.ModInfo;

import java.util.concurrent.ExecutorService;

import static arc.Core.settings;

public class Sgl {
    public static final String NL = System.lineSeparator();

    /** 此mod内部名称 */
    public static final String modName = "ice";
    /** 模组文件夹位置 */
    public static final Fi modDirectory = settings.getDataDirectory().child("mods");
    /** 本模组的文件位置 */
    public static final ModInfo mod = ModGetter.INSTANCE.getModWithName(modName);
    /** 此模组的压缩包对象 */
    public static final Fi modFile = mod.getFile();

    /** 模组内配置文件存放位置 */
    public static final Fi internalConfigDir = modFile.child("config");
    /** 模组数据文件夹 */
    public static final Fi dataDirectory = modDirectory.child("data").child(modName);
    /** 行星上下文目录 */
    public static final Fi planetDataDirectory = dataDirectory.child("planet_contexts");
    /** 模组配置文件夹 */
    public static final Fi configDirectory = modDirectory.child("config").child(modName);
    /** 模组的mod_config.hjson配置文件 */
    public static final Fi configFile = configDirectory.child("mod_config.hjson");
    /** 模组持久全局变量存储文件 */
    public static final Fi globalVars = dataDirectory.child("global_vars.bin");
    /** 模组持久全局变量备份文件 */
    public static final Fi globalVarsBackup = dataDirectory.child("global_vars.bin.bak");
    /** 通知标签历史 */
    public static final Fi notificationHistory = dataDirectory.child("notifyHistory.bin");
    /** 通知标签历史备份 */
    public static final Fi notificationHistoryBackup = dataDirectory.child("notifyHistory.bin");

    //URIs
    public static final String modDevelopGroup = "https://jq.qq.com/?_wv=1027&k=vjybgqDG";
    public static final String githubUserAvatars = "https://avatars.githubusercontent.com/";
    public static final String githubProject = "https://github.com/EB-wilson/Singularity";
    public static final String discord = "";
    public static final String githubRawMaster = "https://raw.githubusercontent.com/EB-wilson/Singularity/master/";
    public static final String githubProjReleaseApi = "https://api.github.com/repos/EB-wilson/Singularity/releases/latest";
    public static final String publicInfo = githubRawMaster + "publicInfo/";

    public static final Team none = Team.get(255);

    /** Singularity平行逻辑存储对象 */
    public static SingularityGameLogic logic = new SingularityGameLogic();
    /** 模组配置存储器 */
    public static ModConfig config = new ModConfig();
    /** 持久保存的全局变量集 */
    public static Settings globals;
    /** ui类存放对象 */
    public static SglUI ui;
    /** 贡献者列表 */
    public static Contributors contributors;

    public static DistSupportContainerTable matrixContainers;

    public static EMPHealthManager empHealth;

    public static ResearchManager researches = new ResearchManager();
    public static ModsInteropAPI interopAPI = new ModsInteropAPI();

    public static ExecutorService executor = Threads.unboundedExecutor("SGL_EXEC", 1);

    public static void init() {
        //注册所有打包数据类型id
        BytePackAssign.assignAll();

        globals = new Settings() {
            {
                setAutosave(true);
                setDataDirectory(Sgl.dataDirectory);
            }

            @Override
            public Fi getSettingsFile() {
                return globalVars;
            }

            @Override
            public Fi getBackupFolder() {
                return Sgl.dataDirectory.child("global_backups");
            }

            @Override
            public Fi getBackupSettingsFile() {
                return globalVarsBackup;
            }

            @Override
            public synchronized void load() {
                try {
                    loadValues();
                } catch (Throwable error) {
                    Log.err("Error in load: " + Strings.getStackTrace(error));
                    if (errorHandler != null) {
                        if (!hasErrored) errorHandler.get(error);
                    } else {
                        throw error;
                    }
                    hasErrored = true;
                }
                loaded = true;
            }

            @Override
            public synchronized void forceSave() {
                if (!loaded) return;
                try {
                    saveValues();
                } catch (Throwable error) {
                    Log.err("Error in forceSave to " + getSettingsFile() + ":\n" + Strings.getStackTrace(error));
                    if (errorHandler != null) {
                        if (!hasErrored) errorHandler.get(error);
                    } else {
                        throw error;
                    }
                    hasErrored = true;
                }
                modified = false;
            }
        };

        globals.load();

        contributors = new Contributors();
        matrixContainers = new DistSupportContainerTable();
        empHealth = new EMPHealthManager();

        matrixContainers.setDefaultSupports();

        logic.init();
        interopAPI.init();
        empHealth.init();
        // researches.init();

        UpdatePool.receive("autosaveGlobal", globals::autosave);
        UpdatePool.receive("sglLogicUpdate", logic::update);

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

            configNotificationIO();

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

    private static void configNotificationIO() {
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
    }

    private static void generatePostAtlas() {
        Log.info("[Singularity] load post generated atlas");
        Vars.content.each(c -> {
            if (c instanceof PostAtlasGenerator gen) {
                gen.postLoad();
            }
        });
    }
}
