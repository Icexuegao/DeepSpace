package singularity;

import arc.Core;
import arc.files.Fi;
import arc.graphics.g2d.TextureRegion;
import arc.scene.style.Drawable;
import ice.DeepSpace;
import mindustry.mod.Mod;
import singularity.contents.LiquidBlocks;
import singularity.contents.SglTechThree;

public class Singularity extends Mod {



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