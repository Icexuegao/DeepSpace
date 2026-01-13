package singularity.world.draw;

import arc.Core;
import arc.func.Floatf;
import arc.func.Intf;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.TextureRegion;
import mindustry.gen.Building;
import mindustry.world.Block;
import mindustry.world.draw.DrawBlock;

public class DrawFrame<E extends Building> extends DrawBlock{
  public int frames = 3;

  public Intf<E> cursor = e -> (int)((e.totalProgress() / 5) % frames);
  public Floatf<E> alpha = e -> 1;
  public Floatf<E> rotation = e -> 0;

  public TextureRegion[] regions;

  @SuppressWarnings("unchecked")
  @Override
  public void draw(Building build){
    Draw.alpha(alpha.get((E) build));
    Draw.rect(regions[cursor.get((E) build)], build.x, build.y, rotation.get((E) build));
    Draw.color();
  }

  @Override
  public TextureRegion[] icons(Block block){
    return new TextureRegion[]{regions[0]};
  }

  @Override
  public void load(Block block){
    regions = new TextureRegion[frames];
    for(int i = 0; i < frames; i++){
      regions[i] = Core.atlas.find(block.name + "_frame" + i);
    }
  }
}
