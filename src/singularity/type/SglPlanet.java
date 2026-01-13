package singularity.type;

import arc.struct.Seq;
import mindustry.type.Planet;
import singularity.Sgl;
import singularity.game.planet.ChunkContextIncubator;
import singularity.game.planet.PlanetContext;

public class SglPlanet extends Planet {
  public PlanetContext context;

  public Seq<ChunkContextIncubator> incubators = new Seq<>();

  public SglPlanet(String name, Planet parent, float radius) {
    super(name, parent, radius);
  }

  public SglPlanet(String name, Planet parent, float radius, int sectorSize) {
    super(name, parent, radius, sectorSize);
  }

  @Override
  public void init() {
    super.init();

    context = Sgl.logic.makePlanetContext(this);
    context.addIncubator(incubators);
  }

  public void addIncubators(ChunkContextIncubator... incubator) {
    this.incubators.addAll(incubator);
  }
}
