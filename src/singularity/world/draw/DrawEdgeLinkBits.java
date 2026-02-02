package singularity.world.draw;

import arc.Core;
import arc.func.Func;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.TextureRegion;
import arc.math.geom.Point2;
import arc.util.Eachable;
import mindustry.entities.units.BuildPlan;
import mindustry.gen.Building;
import mindustry.world.Block;
import mindustry.world.draw.DrawBlock;
import universecore.world.DirEdges;

import static mindustry.Vars.tilesize;

public class DrawEdgeLinkBits<T> extends DrawBlock{
  public static final byte[] EMP = new byte[]{0, 0, 0, 0};
  public Func<T, byte[]> compLinked = e -> EMP;

  public float layer = -1;

  public TextureRegion linker;
  public String suffix = "_linker";

  @Override
  public void load(Block block){
    super.load(block);
    linker = Core.atlas.find(block.name + suffix);
  }

  @Override
  public void drawPlan(Block block, BuildPlan plan, Eachable<BuildPlan> list){

  }

  @SuppressWarnings("unchecked")
  @Override
  public void draw(Building build){
    float z = Draw.z();
    if (layer > 0) Draw.z(layer);
    for(int dir = 0; dir < 4; dir++){
      Point2[] arr = DirEdges.get(build.block.size, dir);
      byte[] linkBits = this.compLinked.get((T) build);
      for(int i = 0; i < arr.length; i++){
        if((linkBits[dir] & 1 << i) == 0) continue;
        float dx = 0, dy = 0;

        Draw.scl(1, dir == 1 || dir == 2? -1: 1);
        switch(dir){
          case 0 -> dx = -1;
          case 1 -> dy = -1;
          case 2 -> dx = 1;
          case 3 -> dy = 1;
        }
        Draw.rect(linker, (build.tileX() + arr[i].x + dx)*tilesize, (build.tileY() + arr[i].y + dy)*tilesize, 90*dir);
      }
    }

    Draw.z(z);
  }
}
