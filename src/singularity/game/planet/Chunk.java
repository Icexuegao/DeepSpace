package singularity.game.planet;

import arc.struct.ObjectMap;
import arc.util.Log;
import arc.util.io.Reads;
import arc.util.io.Writes;
import mindustry.game.Team;
import mindustry.type.Sector;
import universecore.util.Empties;

import java.io.*;

public class Chunk {
  public final Sector sector;
  public final ObjectMap<Team, ObjectMap<Class<? extends ChunkContext>, ChunkContext>> contexts = new ObjectMap<>();

  protected ObjectMap<Team, ObjectMap<String, byte[]>> serializedData = new ObjectMap<>();

  public boolean isForeground = false;

  public Chunk(Sector sector) {
    this.sector = sector;
  }

  public boolean checkTeamActivity(Team team) {
    return contexts.containsKey(team);
  }

  public void addContext(ChunkContext context) {
    contexts.get(context.team, ObjectMap::new).put(context.getClass(), context);

    context.init(this);
  }

  public void addContext(Class<? extends ChunkContext> chunkType, ChunkContext context) {
    if (!chunkType.isAssignableFrom(context.getClass())) throw new IllegalArgumentException("Context class mismatch");

    contexts.get(context.team, ObjectMap::new).put(chunkType, context);

    context.init(this);
  }

  @SuppressWarnings("unchecked")
  public <T extends ChunkContext> T getContext(Team team, Class<T> chunkType) {
    ObjectMap<Class<? extends ChunkContext>, ChunkContext> map = contexts.get(team, Empties.nilMapO());
    T res = (T) map.get(chunkType);

    if (res == null) {
      Class<?> resC = null;
      for (ObjectMap.Entry<Class<? extends ChunkContext>, ChunkContext> entry : map) {
        if (chunkType.isAssignableFrom(entry.key)) {
          if (resC == null) {
            res = (T) entry.value;
            resC = entry.key;
          }
          else if (entry.key.isAssignableFrom(resC)){
            res = (T) entry.value;
            resC = entry.key;
          }
        }
      }
    }

    return res;
  }

  public void chunkInstall(){
    isForeground = true;

    for (ObjectMap<Class<? extends ChunkContext>, ChunkContext> map : contexts.values()) {
      for (ChunkContext context : map.values()) {
        context.install();
      }
    }
  }

  public void chunkUninstall(){
    isForeground = false;

    for (ObjectMap<Class<? extends ChunkContext>, ChunkContext> map : contexts.values()) {
      for (ChunkContext context : map.values()) {
        context.uninstall();
      }
    }
  }

  public void update(float delta){
    for (ObjectMap<Class<? extends ChunkContext>, ChunkContext> map : contexts.values()) {
      for (ChunkContext context : map.values()) {
        if (!context.team.active() || !context.active()) continue;

        if (isForeground) context.updateFore(delta);
        else context.updateBack(delta);

        context.update(delta);
      }
    }
  }

  public void saveChunk(Writes writes){
    serializedData.clear();
    for (ObjectMap.Entry<Team, ObjectMap<Class<? extends ChunkContext>, ChunkContext>> entry : contexts) {
      for (ChunkContext context : entry.value.values()) {
        String str = context.getContextName();

        try(ByteArrayOutputStream bu = new ByteArrayOutputStream()) {
          context.save(new Writes(new DataOutputStream(bu)));

          byte[] bytes = bu.toByteArray();
          serializedData.get(entry.key, ObjectMap::new).put(str, bytes);
        } catch (IOException e) {
          throw new RuntimeException(e);
        }
      }
    }

    writes.i(serializedData.size);
    for (ObjectMap.Entry<Team, ObjectMap<String, byte[]>> e : serializedData) {
      writes.i(e.key.id);
      writes.i(e.value.size);
      for (ObjectMap.Entry<String, byte[]> entry : e.value) {
        writes.str(entry.key);
        writes.b(entry.value.length);
        writes.b(entry.value);
      }
    }
  }

  public void loadChunk(Reads reads){
    serializedData.clear();

    int size = reads.i();
    for (int i = 0; i < size; i++) {
      int id = reads.i();
      Team team = Team.get(id);
      int size2 = reads.i();
      for (int j = 0; j < size2; j++) {
        serializedData.get(team, ObjectMap::new).put(reads.str(), reads.b(reads.b()));
      }
    }
  }

  public void loadSerialized(PlanetContext planet){
    for (Team team : serializedData.keys()) {
      if (!contexts.containsKey(team)) planet.incubateContext(team, this);
    }

    for (ObjectMap.Entry<Team, ObjectMap<Class<? extends ChunkContext>, ChunkContext>> entry : contexts) {
      for (ChunkContext context : entry.value.values()) {
        String str = context.getContextName();

        ObjectMap<String, byte[]> teamMap = serializedData.get(entry.key, Empties.nilMapO());
        if (teamMap.containsKey(str)) {
          try(ByteArrayInputStream in = new ByteArrayInputStream(teamMap.get(str))) {
            Reads read = new Reads(new DataInputStream(in));
            context.load(read);
          } catch (RuntimeException | IOException e) {
            Log.err("[Singularity] Failed to load chunk context " + str + " for sector " + sector);
          }
        }
      }
    }
  }

  public boolean isActive() {
    return sector.hasBase() || sector.hasEnemyBase();
  }

  @Override
  public String toString() {
    return "chunk_" + sector.toString();
  }
}
