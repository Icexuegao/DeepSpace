package singularity;

import arc.Core;
import arc.Events;
import arc.files.Fi;
import arc.graphics.g2d.TextureRegion;
import arc.scene.style.Drawable;
import arc.struct.ObjectMap;
import arc.util.Log;
import ice.core.SettingValue;
import mindustry.mod.Mod;
import singularity.contents.*;
import singularity.contents.override.OverrideTechThree;
import singularity.type.SglCategory;
import singularity.type.SglContentType;
import singularity.world.meta.SglAttribute;
import universecore.util.OverrideContentList;

import java.util.Locale;

import static mindustry.game.EventType.ClientLoadEvent;

public class Singularity extends Mod {
  private static final ContentList[] modContents = new ContentList[]{new OtherContents(),//其他内容
          new LiquidBlocks(),//物流方块
          new SglUnits(),//单位相关内容（单位、工厂）
          new SglPlanets(),//星球
          new SglTechThree(),//科技树
  };

  private static final OverrideContentList[] overrideContents = new OverrideContentList[]{new OverrideTechThree(),};

  public Singularity() {
    Log.info("[Singularity] Singularity mod is loading,Thanks for your play");
    //加载模组配置数据
    Sgl.config.load();

    Events.on(ClientLoadEvent.class, e -> {
      if (SettingValue.INSTANCE.get启用调试模式()) Sgl.ui.mainMenu.show();
      // new TestDialog().show();
    });

  }

  @Override
  public void init() {
    //加载全局变量
    Sgl.init();
    //Sgl.classes.finishGenerate();
    if (Sgl.config.loadInfo) Log.info("[Singularity] mod initialize finished");
  }

  @Override
  public void loadContent() {
    //加载属性类型
    SglAttribute.load();
    //加载方块类型
    SglCategory.load();
    //载入所有新内容类型
    SglContentType.load();

    for (ContentList list : Singularity.modContents) {
      list.load();
    }

    if (Sgl.config.modReciprocalContent) {
      for (OverrideContentList override : Singularity.overrideContents) {
        override.load();
      }
    }

  }

  public static TextureRegion getModAtlas(String name) {
    return Core.atlas.find(Sgl.modName + "-" + name);
  }

  public static TextureRegion getModAtlas(String name, TextureRegion def) {
    return Core.atlas.find(Sgl.modName + "-" + name, def);
  }

  public static <T extends Drawable> T getModDrawable(String name) {
    return Core.atlas.getDrawable(Sgl.modName + "-" + name);
  }

  public static Fi getInternalFile(String path) {
    return Sgl.modFile.child(path);
  }

  public static Fi getDocumentFile(String name) {
    return getInternalFile("documents").child(Core.bundle.getLocale().toString()).child(name);
  }

  public static Fi getDocumentFile(Locale locale, String name) {
    Fi docs = getInternalFile("documents").child(locale.toString());
    return docs.exists() ? docs.child(name) : getInternalFile("documents").child("zh_CN");
  }

  private static final ObjectMap<Fi, String> docCache = new ObjectMap<>();

  public static String getDocument(String name) {
    return getDocument(name, true);
  }

  public static String getDocument(String name, boolean cache) {
    Fi fi = getDocumentFile(name);
    return cache ? docCache.get(fi, fi::readString) : fi.readString();
  }

  public static String getDocument(Locale locale, String name) {
    return getDocumentFile(locale, name).readString();
  }
}