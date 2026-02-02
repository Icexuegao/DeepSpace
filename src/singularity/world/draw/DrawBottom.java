package singularity.world.draw;

import arc.Core;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.TextureRegion;
import arc.util.Eachable;
import mindustry.entities.units.BuildPlan;
import mindustry.gen.Building;
import mindustry.graphics.Layer;
import mindustry.world.Block;
import mindustry.world.draw.DrawBlock;
import singularity.Singularity;

public class DrawBottom extends DrawBlock{
  public TextureRegion bottom;

  @Override
  public void load(Block block){
    bottom = Core.atlas.find(block.name + "_bottom", Singularity.getModAtlas("bottom_" + block.size));
  }

  @Override
  public void draw(Building build){
    float z = Draw.z();
    Draw.z(Layer.blockUnder);
    Draw.rect(bottom, build.x, build.y);
    Draw.z(z);
  }

  @Override
  public void drawPlan(Block block, BuildPlan plan, Eachable<BuildPlan> list) {
    Draw.rect(bottom, plan.drawx(), plan.drawy());
  }

  @Override
  public TextureRegion[] icons(Block block){
    return new TextureRegion[]{bottom};
  }
}
