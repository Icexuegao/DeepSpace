package singularity.world.draw;

import mindustry.graphics.MultiPacker;
import mindustry.world.Block;

public interface DrawAtlasGenerator {
  void generateAtlas(Block block, MultiPacker packer);
  void postLoad(Block block);
}
