package singularity.world.draw;

import arc.graphics.Blending;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.TextureRegion;
import arc.math.Mathf;
import arc.util.Time;
import mindustry.gen.Building;
import mindustry.world.Block;
import mindustry.world.draw.DrawPlasma;
import singularity.graphic.SglDrawConst;

import static mindustry.Vars.tilesize;

public class DrawExpandPlasma extends DrawPlasma{
  public float rotationSpeed = 1f;
  public float cycle = 15;
  public float expandSpeed = 0.8f;

  {
    suffix = "_plasma_";
  }
  
  @Override
  public void draw(Building entity){
    Draw.blend(Blending.additive);
    for(int i = 0; i < regions.length; i++){
      float r = entity.block.size*tilesize*(2f/3f) + ((Time.time%cycle)/cycle)*expandSpeed*entity.block.size*tilesize*(2f/3);
      float rd = entity.block.size*tilesize*(2f/3f) + ((Time.time*1.65f%cycle)/cycle)*expandSpeed*entity.block.size*tilesize*(2f/3);
    
      Draw.color(plasma1, plasma2, (float)i / regions.length);
      Draw.alpha((0.3f + Mathf.absin(Time.time, 2f + i * 2f, 0.3f + i * 0.05f)) * entity.warmup());
      Draw.rect(regions[i], entity.x, entity.y, r, r, Time.time * (3 + i * 6f) * rotationSpeed);
      Draw.rect(regions[(i+1)%regions.length], entity.x, entity.y, rd, rd, Time.time * (3 + i * 6f) * rotationSpeed);
    }
    Draw.blend();
    Draw.color();
  }

  @Override
  public TextureRegion[] icons(Block block){
    return SglDrawConst.EMP_REGIONS;
  }
}
