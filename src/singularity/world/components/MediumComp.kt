package singularity.world.components;

import arc.Core;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.TextureRegion;
import arc.util.Strings;
import mindustry.graphics.Pal;
import mindustry.ui.Bar;
import mindustry.world.Block;
import singularity.Singularity;


public interface MediumComp{
  TextureRegion[] mediumRegiom = new TextureRegion[1];

  //@Annotations.BindField("mediumCapacity")
  default float mediumCapacity(){
    return 0;
  }

//  @Annotations.BindField("lossRate")
  default float lossRate(){
    return 0;
  }

//  @Annotations.BindField("mediumMoveRate")
  default float mediumMoveRate(){
    return 0;
  }

 // @Annotations.BindField("outputMedium")
  default boolean outputMedium(){
    return false;
  }

  //@Annotations.MethodEntry(entryMethod = "setBars")
  default void setHeatBars(){
    if(this instanceof Block b){
      b.addBar("medium", entity -> {
        MediumBuildComp ent = (MediumBuildComp) entity;
        return new Bar(
            () -> Core.bundle.get("misc.medium") + ":" + Strings.autoFixed(ent.mediumContains(), 2),
            () -> Pal.reactorPurple,
            () -> ent.mediumContains()/mediumCapacity()
        ){
          @Override
          public void draw(){
            super.draw();
            if(mediumRegiom[0] == null) mediumRegiom[0] = Singularity.getModAtlas("medium");
            Draw.rect(mediumRegiom[0], x + height, y + height/2, height, height);
          }
        };
      });
    }
  }
}
