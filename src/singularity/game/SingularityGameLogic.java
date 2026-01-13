package singularity.game;

import arc.Core;
import arc.Events;
import arc.files.Fi;
import arc.struct.ObjectMap;
import arc.util.Time;
import arc.util.io.Reads;
import arc.util.io.Writes;
import mindustry.Vars;
import mindustry.game.EventType;
import mindustry.type.Planet;
import mindustry.type.Sector;
import singularity.Sgl;
import singularity.game.planet.Chunk;
import singularity.game.planet.PlanetContext;

public class SingularityGameLogic {
  private final ObjectMap<Planet, PlanetContext> planets = new ObjectMap<>();

  public boolean inSingularity = true;
  public boolean paused = false;

  public PlanetContext currentPlanet;

  protected Chunk currentChunk;

  public PlanetContext makePlanetContext(Planet planet){
    PlanetContext context = new PlanetContext(planet);
    planets.put(planet, context);
    return context;
  }

  public PlanetContext getPlanetContext(Planet planet){
    return planets.get(planet);
  }

  public void assignListener(){
    Events.on(EventType.WorldLoadEvent.class, e -> {
      Sector sector = Vars.state.rules.sector;

      if (sector == null) return;

      Planet planet = sector.planet;
      if (inSingularity && planets.containsKey(planet)) {
        Core.app.post(() -> {
          PlanetContext context = getPlanetContext(planet);
          context.enterSector(sector);

          currentPlanet = context;
          currentChunk = context.getChunk(sector);
          currentChunk.loadSerialized(context);
        });
      }
    });

    Events.on(EventType.ResetEvent.class, e -> {
      if (currentChunk == null || !inSingularity) return;

      try(Writes data = Sgl.planetDataDirectory.child(currentChunk + ".bin").writes()){
        currentChunk.saveChunk(data);
      }

      getPlanetContext(currentChunk.sector.planet).exitSector();
      currentPlanet = null;
      currentChunk = null;
    });
  }

  public void init(){
    planets.values().forEach(PlanetContext::initial);

    planets.values().forEach(c -> {
      for (Chunk chunk : c.chunks.values()) {
        Fi fi = Sgl.planetDataDirectory.child(chunk + ".bin");
        if (!fi.exists()) continue;

        try(Reads data = fi.reads()){
          chunk.loadChunk(data);
          chunk.loadSerialized(c);
        }
      }
    });

    assignListener();
  }

  public void update(){
    if(!paused && inSingularity) {
      planets.values().forEach(e -> e.update(Time.delta));
    }
  }
}
