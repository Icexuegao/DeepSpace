package singularity.contents.override;

import mindustry.content.Planets;
import singularity.world.gen.SglSerploGenerator;
import universecore.util.OverrideContentList;

public class OverridePlanets implements OverrideContentList{
  @Override
  public void load(){
    Planets.serpulo.generator = new SglSerploGenerator();
  }
}
