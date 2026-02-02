package singularity.world.draw;

import arc.struct.Seq;
import mindustry.graphics.MultiPacker;
import mindustry.world.Block;
import mindustry.world.draw.DrawBlock;
import mindustry.world.draw.DrawMulti;

public class DrawMultiSgl extends DrawMulti implements DrawAtlasGenerator {
  public DrawMultiSgl(){
    super();
  }

  public DrawMultiSgl(DrawBlock... drawers){
    super(drawers);
  }

  public DrawMultiSgl(Seq<DrawBlock> drawers){
    super(drawers);
    this.drawers = drawers.toArray(DrawBlock.class);
  }

  @Override
  public void generateAtlas(Block block, MultiPacker packer) {
    for (DrawBlock drawer : drawers) {
      if (drawer instanceof DrawAtlasGenerator gen) gen.generateAtlas(block, packer);
    }
  }

  @Override
  public void postLoad(Block block) {
    for (DrawBlock drawer : drawers) {
      if (drawer instanceof DrawAtlasGenerator gen) gen.postLoad(block);
    }
  }
}
