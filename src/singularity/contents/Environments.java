package singularity.contents;

import mindustry.world.Block;
import mindustry.world.blocks.environment.OreBlock;

public class Environments implements ContentList{
  /**黑晶石矿石*/
  public static Block black_crystone_ore,
  /**铀矿石*/
  uranium_ore;

  @Override
  public void load(){
    black_crystone_ore = new OreBlock("black_crystone_ore", SglItems.black_crystone){{
      oreDefault = true;
      oreThreshold = 0.843f;
      oreScale = 24.7247982f;
    }};

    uranium_ore = new OreBlock("uranium_rawore_ore", SglItems.uranium_rawore){{
      oreDefault = true;
      oreThreshold = 0.901f;
      oreScale = 25.380423f;
    }};
  }
}
