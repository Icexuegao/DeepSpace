package singularity.world.draw;

import arc.struct.Seq;
import mindustry.graphics.MultiPacker;
import mindustry.world.Block;
import mindustry.world.draw.DrawBlock;
import mindustry.world.draw.DrawMulti;

public class DrawMultiSgl extends DrawMulti   {
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



}
