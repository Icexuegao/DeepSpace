package singularity;

import arc.Core;
import arc.Events;
import arc.files.Fi;
import arc.graphics.g2d.TextureRegion;
import arc.scene.style.Drawable;
import arc.util.Log;
import ice.DeepSpace;
import ice.core.SettingValue;
import mindustry.mod.Mod;
import singularity.contents.LiquidBlocks;
import singularity.contents.SglTechThree;
import universecore.util.OverrideContentList;

import static mindustry.game.EventType.ClientLoadEvent;

public class Singularity extends Mod {

  public Singularity(){
    Log.info("[Singularity] Singularity mod is loading,Thanks for your play");
    //加载模组配置数据
    Sgl.config.load();

    Events.on(ClientLoadEvent.class, e -> {
      if(SettingValue.INSTANCE.get启用调试模式()) Sgl.ui.mainMenu.show();
      //new TestDialog().show();
    });

  }

  @Override
  public void init(){
    //加载全局变量
    Sgl.init();

  }

  @Override
  public void loadContent(){
    new LiquidBlocks().load();
    SglTechThree.INSTANCE.load();
  }

  public static TextureRegion getModAtlas(String name){
    return Core.atlas.find(DeepSpace.INSTANCE.getModName() + "-" + name);
  }

  public static <T extends Drawable> T getModDrawable(String name){
    return Core.atlas.getDrawable(DeepSpace.INSTANCE.getModName() + "-" + name);
  }

  public static Fi getInternalFile(String path){
    return DeepSpace.INSTANCE.getModFile().child(path);
  }

}