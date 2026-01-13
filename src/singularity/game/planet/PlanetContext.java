package singularity.game.planet;

import arc.func.Cons;
import arc.struct.ObjectMap;
import arc.struct.Seq;
import arc.util.Nullable;
import mindustry.Vars;
import mindustry.game.Team;
import mindustry.game.Teams;
import mindustry.type.Planet;
import mindustry.type.Sector;

public class PlanetContext {
  public final Planet planet;

  public final ObjectMap<Sector, Chunk> chunks = new ObjectMap<>();
  public final Seq<ChunkContextIncubator> incubators = new Seq<>();

  @Nullable public Chunk currentChunk;

  private boolean initialized = false;

  public PlanetContext(Planet planet) {
    this.planet = planet;
  }

  public void addIncubator(ChunkContextIncubator incubator) {
    if (initialized) throw new IllegalStateException("Context is already initialized");

    this.incubators.add(incubator);
  }

  public void addIncubator(ChunkContextIncubator... incubators) {
    if (initialized) throw new IllegalStateException("Context is already initialized");

    this.incubators.add(incubators);
  }

  public void addIncubator(Seq<ChunkContextIncubator> incubators) {
    if (initialized) throw new IllegalStateException("Context is already initialized");

    this.incubators.addAll(incubators);
  }

  public Chunk getChunk(Sector sector) {
    return chunks.get(sector);
  }

  public <T extends ChunkContext> T currentContext(Team team, Class<T> type) {
    if (!currentChunk.checkTeamActivity(team)){
      incubateContext(team, currentChunk);
    }

    return currentChunk.getContext(team, type);
  }

  public <T extends ChunkContext> void applyCurrent(Team team, Class<T> chunkType, Cons<T> cons){
    if (!currentChunk.checkTeamActivity(team)){
      incubateContext(team, currentChunk);
    }

    T c = currentContext(team, chunkType);
    if (c == null) throw new IllegalArgumentException("No context of type " + chunkType.getName() + " found");
    cons.get(c);
  }

  public <T extends ChunkContext> T getContext(Sector sector, Team team, Class<T> type) {
    Chunk chunk = chunks.get(sector);

    if (!chunk.checkTeamActivity(team)){
      incubateContext(team, chunk);
    }

    return chunk.getContext(team, type);
  }

  public <T extends ChunkContext> void apply(Sector sector, Team team, Class<T> chunkType, Cons<T> cons){
    Chunk chunk = chunks.get(sector);

    if (!chunk.checkTeamActivity(team)){
      incubateContext(team, chunk);
    }

    T c = getContext(sector, team, chunkType);
    if (c == null) throw new IllegalArgumentException("No context of type " + chunkType.getName() + " found");
    cons.get(c);
  }

  public void initial(){
    for (Sector sector : planet.sectors) {
      Chunk chunk = new Chunk(sector);
      chunks.put(sector, chunk);
    }

    initialized = true;
  }

  public void incubateContext(Team team, Chunk chunk) {
    if (chunk.checkTeamActivity(team)) return;

    for (ChunkContextIncubator incubator : incubators) {
      incubator.begin(team);
      while (incubator.hasNext()) {
        Class<? extends ChunkContext> type = incubator.peekType();

        if (type == null) chunk.addContext(incubator.next());
        else chunk.addContext(type, incubator.next());
      }
    }
  }

  public void enterSector(Sector sector){
    if (sector.planet != planet) throw new IllegalArgumentException("Sector is not a part of this global");

    if (currentChunk != null) currentChunk.chunkUninstall();
    currentChunk = chunks.get(sector);
    currentChunk.chunkInstall();

    for (Teams.TeamData data : Vars.state.teams.active) {
      incubateContext(data.team, currentChunk);
    }
  }

  public void exitSector(){
    if (currentChunk != null) currentChunk.chunkUninstall();
    currentChunk = null;
  }

  public void update(float delta) {
    for (Chunk chunk : chunks.values()) {
      if (chunk.isActive()) {
        chunk.update(delta);
      }
    }
  }
}
