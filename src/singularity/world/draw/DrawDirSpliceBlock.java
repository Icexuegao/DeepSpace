package singularity.world.draw;

import arc.Core;
import arc.func.Boolf2;
import arc.func.Intf;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.TextureRegion;
import arc.math.geom.Point2;
import arc.util.Eachable;
import mindustry.entities.units.BuildPlan;
import mindustry.gen.Building;
import mindustry.world.Block;
import mindustry.world.draw.DrawBlock;
import universecore.world.DirEdges;

public class DrawDirSpliceBlock<E> extends DrawBlock {
  public TextureRegion[] splicers = new TextureRegion[4];
  public Intf<E> spliceBits = e -> 0;
  public Boolf2<BuildPlan, BuildPlan> planSplicer = (plan, other) -> false;

  public float layerOffset = 0.0001f;
  public boolean layerRec = true;

  public boolean simpleSpliceRegion = false;
  public String suffix = "_splice";

  @Override
  public void load(Block block){
    if(simpleSpliceRegion){
      for(int i = 0; i < 4; i++){
        splicers[i] = Core.atlas.find(block.name + suffix);
      }
    }
    else{
      for(int i = 0; i < splicers.length; i++){
        splicers[i] = Core.atlas.find(block.name + suffix + "_" + i);
      }
    }
  }

  protected void drawSplice(float x, float y, int dirBits){
    int move = 0;

    while(1 << move <= dirBits){
      int bit = 1 << move;
      if((dirBits & bit) != 0){

        Draw.rect(splicers[move], x, y, move*90);
      }
      move++;
    }
  }

  @SuppressWarnings("unchecked")
  @Override
  public void draw(Building build){
    float z = Draw.z();
    Draw.z(z + layerOffset);

    drawSplice(build.x, build.y, spliceBits.get((E) build));
    if (layerRec){
      Draw.z(z);
    }
    else Draw.z(z + 0.0001f);
  }

  @Override
  public void drawPlan(Block block, BuildPlan plan, Eachable<BuildPlan> list){
    int bits = 0;
    Block planBlock = plan.block;

    t: for(int i=0; i<4; i++){
      Block other = null;
      for(Point2 p: DirEdges.get(plan.block.size, i)){
        int x = plan.x + p.x;
        int y = plan.y + p.y;
        BuildPlan[] target = {null};

        list.each(pl -> {
          if(target[0] != null) return;
          if(pl.x == x && pl.y == y){
            target[0] = pl;
          }
        });

        if(target[0] == null) continue t;

        if(other == null){
          if(planSplicer.get(plan, target[0])){
            other = target[0].block;
          }
          else{
            bits &= ~(0b0001 << i);
            continue t;
          }
        }
        else if(other != planBlock || !planSplicer.get(plan, target[0])){
          bits &= ~(0b0001 << i);
          continue t;
        }
      }
      bits |= 0b0001 << i;
    }

    drawSplice(plan.drawx(), plan.drawy(), bits);
  }
}
