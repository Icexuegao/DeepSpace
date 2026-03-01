package singularity.world.draw;

import arc.Core;
import arc.func.Cons;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.TextureRegion;
import arc.util.Eachable;
import mindustry.entities.units.BuildPlan;
import mindustry.gen.Building;
import mindustry.world.Block;

public class DrawPayloadFactory<E> extends DrawDirSpliceBlock<E>{
  public TextureRegion topRegion, outRegion;

  public Cons<E> drawPayload = e -> {};
  public String suffix = "";

  @Override
  public void load(Block block) {
    String name = block.name;
    int size = block.size;

    topRegion = Core.atlas.find(name + "_top", "factory-top-" + size + suffix);
    outRegion = Core.atlas.find(name + "_out", "factory-out-" + size + suffix);

    for(int i = 0; i < splicers.length; i++){
      splicers[i] = Core.atlas.find(name + "_in", "factory-in-" + size + suffix);
    }
  }

  @Override
  public void drawPlan(Block block, BuildPlan plan, Eachable<BuildPlan> list) {
    Draw.rect(block.region, plan.drawx(), plan.drawy());
    super.drawPlan(block, plan, list);
    Draw.rect(outRegion, plan.drawx(), plan.drawy(), plan.rotation*90);
    Draw.rect(topRegion, plan.drawx(), plan.drawy());
  }

  @SuppressWarnings("unchecked")
  @Override
  public void draw(Building build) {
    Draw.rect(build.block.region, build.x, build.y);
    drawSplice(build.x, build.y, spliceBits.get((E) build));
    Draw.rect(outRegion, build.x, build.y, build.rotdeg());

    drawPayload.get((E) build);

    Draw.rect(topRegion, build.x, build.y);
  }

  @Override
  public TextureRegion[] icons(Block block) {
    return new TextureRegion[]{
        block.region,
        outRegion,
        topRegion
    };
  }
}
