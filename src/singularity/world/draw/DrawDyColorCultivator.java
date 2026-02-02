package singularity.world.draw;

import arc.Core;
import arc.func.Floatf;
import arc.func.Func;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Lines;
import arc.graphics.g2d.TextureRegion;
import arc.util.Time;
import mindustry.gen.Building;
import mindustry.graphics.Drawf;
import mindustry.world.Block;
import mindustry.world.draw.DrawBlock;
import singularity.graphic.SglDrawConst;

public class DrawDyColorCultivator<T extends Building> extends DrawBlock{
  public Func<T, Color> plantColor = e -> Color.valueOf("5541b1");
  public Func<T, Color> plantColorLight = e -> Color.valueOf("7457ce");
  public Func<T, Color> bottomColor = e -> Color.valueOf("474747");

  public Floatf<T> alpha = Building::warmup;

  public int bubbles = 12, sides = 8;
  public float strokeMin = 0.2f, spread = 3f, timeScl = 70f;
  public float recurrence = 6f, radius = 3f;

  public TextureRegion middle;

  @SuppressWarnings("unchecked")
  @Override
  public void draw(Building build){

    T entity = (T) build;
    Drawf.liquid(middle, build.x, build.y, build.warmup(), plantColor.get(entity));

    Draw.color(bottomColor.get(entity), plantColorLight.get(entity), alpha.get(entity));

    rand.setSeed(build.pos());
    for(int i = 0; i < bubbles; i++){
      float x = rand.range(spread), y = rand.range(spread);
      float life = 1f - ((Time.time / timeScl + rand.random(recurrence)) % recurrence);

      if(life > 0){
        Lines.stroke(build.warmup() * (life + strokeMin));
        Lines.poly(build.x + x, build.y + y, sides, (1f - life) * radius);
      }
    }

    Draw.color();
  }

  @Override
  public void load(Block block){
    middle = Core.atlas.find(block.name + "_middle");
  }

  @Override
  public TextureRegion[] icons(Block block){
    return SglDrawConst.EMP_REGIONS;
  }
}
