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

public class DrawAntiSpliceBlock<E> extends DrawBlock{
  private final static String[] splices = {"right", "right_top", "top", "left_top", "left", "left_bot", "bot", "right_bot"};

  public TextureRegion[] regions = new TextureRegion[8];
  public TextureRegion[] inner = new TextureRegion[4];
  public Boolf2<BuildPlan, BuildPlan> planSplicer = (plan, other) -> false;
  public Intf<E> splicer = e -> 0;

  public TextureRegion icon;

  public float layerOffset = 0.0001f;
  public boolean layerRec = true;

  public boolean interConner;

  @Override
  public void load(Block block) {
    icon = Core.atlas.find(block.name + "_icon");

    for (int i = 0; i < regions.length; i++) {
      regions[i] = Core.atlas.find(block.name + "_" + splices[i]);
    }
    for (int i = 0; i < inner.length; i++) {
      inner[i] = Core.atlas.find(block.name + "_" + splices[i*2 + 1] + "_inner");
    }
  }

  @Override
  public TextureRegion[] icons(Block block) {
    return new TextureRegion[]{icon};
  }

  protected void drawSplice(float x, float y, int bits) {
    for (int dir = 0; dir < 8; dir++) {
      if (dir%2 == 0){
        if ((bits & (1 << dir)) == 0) Draw.rect(regions[dir], x, y);
      }
    }
    for (int dir = 0; dir < 8; dir++) {
      if ((dir + 1)%2 == 0){
        int dirBit = 1 << (dir + 1)%8 | 1 << (dir - 1);
        if ((bits & dirBit) == 0) Draw.rect(regions[dir], x, y);
        else if ((bits & dirBit) == dirBit && (interConner || (bits & (1 << dir)) == 0)) Draw.rect(inner[dir/2], x, y);
      }
    }
  }

  @SuppressWarnings("unchecked")
  @Override
  public void draw(Building build) {
    float z = Draw.z();
    Draw.z(z + layerOffset);
    drawSplice(build.x, build.y, splicer.get((E) build));
    if (layerRec) Draw.z(z);
  }

  @Override
  public void drawPlan(Block block, BuildPlan plan, Eachable<BuildPlan> list){
    int data = 0;
    Block planBlock = plan.block;

    t: for(int i=0; i<8; i++){
      Block other = null;
      for(Point2 p: DirEdges.get8(plan.block.size, i)){
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
            continue t;
          }
        }
        else if(other != planBlock || !planSplicer.get(plan, target[0])){
          continue t;
        }
      }
      data |= 1 << i;
    }

    drawSplice(plan.drawx(), plan.drawy(), data);
  }
}
