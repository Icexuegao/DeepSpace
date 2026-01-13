package singularity.core;

import arc.struct.ObjectMap;
import mindustry.world.Tile;
import mindustry.world.Tiles;

public class UpdateTiles{
  protected final ObjectMap<Tile, Updatable> updaters = new ObjectMap<>();
  
  public void update(){
    for(ObjectMap.Entry<Tile, Updatable> updater: updaters){
      updater.value.update(updater.key);
    }
  }
  
  public void add(Tile tile, Updatable updater){
    if(tile == null) return;
    updaters.put(tile, updater);
  }
  
  public void clear(Tile tile){
    updaters.remove(tile);
  }
  
  public void clear(){
    updaters.clear();
  }
  
  public void loadAll(Tiles all){
    updaters.clear();
    all.eachTile(t -> {
      if(t.floor() instanceof Updatable) add(t, (Updatable) t.floor());
      if(t.overlay() instanceof Updatable) add(t, (Updatable) t.overlay());
    });
  }
  
  public interface Updatable{
    void update(Tile tile);
  }
}
