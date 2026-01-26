package singularity.world.distribution;

import arc.func.Boolf2;
import arc.func.Cons2;
import arc.math.geom.Point2;
import arc.struct.ObjectMap;
import arc.struct.Seq;
import mindustry.Vars;
import mindustry.gen.Building;
import mindustry.world.blocks.storage.CoreBlock;
import singularity.Sgl;
import singularity.world.blocks.distribute.TargetConfigure;
import singularity.world.components.distnet.DistMatrixUnitBuildComp;
import universecore.util.colletion.TreeSeq;

@SuppressWarnings({"unchecked", "rawtypes"})
public class MatrixGrid{
  private static final Seq tmp = new Seq();
  public static final float[] DEF_VALUE = {0f};
  public static final Boolf2<Object, TargetConfigure> REQ = (e, c) -> true;

  final public DistMatrixUnitBuildComp owner;
  final ObjectMap<Building, BuildingEntry<?>> all = new ObjectMap<>();
  
  final TreeSeq<BuildingEntry<?>> output = new TreeSeq<>((a, b) -> b.config.priority - a.config.priority);
  final TreeSeq<BuildingEntry<?>> input = new TreeSeq<>((a, b) -> b.config.priority - a.config.priority);
  final TreeSeq<BuildingEntry<?>> acceptor = new TreeSeq<>((a, b) -> b.config.priority - a.config.priority);
  final TreeSeq<BuildingEntry<?>> container = new TreeSeq<>((a, b) -> b.config.priority - a.config.priority){
    @Override
    public void add(BuildingEntry<?> item){
      super.add(item);
      DistSupportContainerTable.Container cont = Sgl.matrixContainers.getContainer(((Building)item.entity).block);
      if(cont == null) return;
      for(ObjectMap.Entry<DistBufferType<?>, Float> entry: cont.capacities){
        containerCapacities.get(entry.key, () -> new float[1])[0] += entry.value;
      }
    }

    @Override
    public boolean remove(BuildingEntry<?> item){
      boolean res = super.remove(item);
      DistSupportContainerTable.Container cont = Sgl.matrixContainers.getContainer(((Building)item.entity).block);
      if(cont == null) return res;
      for(ObjectMap.Entry<DistBufferType<?>, Float> entry: cont.capacities){
        containerCapacities.get(entry.key, () -> new float[1])[0] -= entry.value;
      }

      return res;
    }
  };
  final ObjectMap<DistBufferType<?>, float[]> containerCapacities = new ObjectMap<>();

  final ObjectMap<DistBufferType<?>, float[]> containerUsed = new ObjectMap<>();
  boolean statUsed;
  
  public int priority;

  public MatrixGrid(DistMatrixUnitBuildComp owner){
    this.owner = owner;
  }

  public void update(){
    for(Building bu: all.keys()){
      if(!(bu instanceof CoreBlock.CoreBuild && bu.isAdded()) && bu.tile.build != bu){
        remove(bu);
      }
    }

    for(float[] used: containerUsed.values()){
      used[0] = 0;
    }
    if(statUsed){
      for(BuildingEntry<?> entry: container){
        DistSupportContainerTable.Container cont = Sgl.matrixContainers.getContainer(((Building) entry.entity).block);
        if(cont == null) continue;
        for(DistBufferType<?> key: cont.capacities.keys()){
          containerUsed.get(key, () -> new float[1])[0] += key.containerUsed((Building) entry.entity).floatValue();
        }
      }
    }
  }

  public void eachUsed(Cons2<DistBufferType<?>, Float> cons){
    for(ObjectMap.Entry<DistBufferType<?>, float[]> entry: containerUsed){
      cons.get(entry.key, entry.value[0]);
    }
  }

  public void eachCapacity(Cons2<DistBufferType<?>, Float> cons){
    for(ObjectMap.Entry<DistBufferType<?>, float[]> entry: containerCapacities){
      cons.get(entry.key, entry.value[0]);
    }
  }

  public float contUsed(DistBufferType<?> buff){
    return containerUsed.get(buff, DEF_VALUE)[0];
  }

  public float contCapacity(DistBufferType<?> buff){
    return containerCapacities.get(buff, DEF_VALUE)[0];
  }

  public void startStatContainer(){
    statUsed = true;
  }

  public void endStatContainer(){
    statUsed = false;
  }

  public <T> Seq<BuildingEntry<T>> get(GridChildType type){
    return get(type, REQ, tmp);
  }

  public <T> Seq<BuildingEntry<T>> get(GridChildType type, Boolf2<T, TargetConfigure> req){
    return get(type, req, tmp);
  }

  public <T> Seq<BuildingEntry<T>> get(GridChildType type, Boolf2<T, TargetConfigure> req, Seq<BuildingEntry<T>> temp){
    temp.clear();
    each(type, req, (e, entry) -> temp.add((BuildingEntry<T>) all.get((Building) e)));
    return temp;
  }

  @SuppressWarnings("unchecked")
  public <T> void each(GridChildType type, Boolf2<T, TargetConfigure> req, Cons2<T, TargetConfigure> cons){
    TreeSeq<BuildingEntry<?>> temp = switch(type){
      case output -> output;
      case input -> input;
      case acceptor -> acceptor;
      case container -> container;
    };

    for(BuildingEntry<?> entry: temp){
      if(req.get((T)entry.entity, entry.config)) cons.get((T) entry.entity, entry.config);
    }
  }

  public void addConfig(TargetConfigure c){
    Building t = Vars.world.build(owner.getTile().x + Point2.x(c.offsetPos), owner.getTile().y + Point2.y(c.offsetPos));
    if(t == null || !owner.tileValid(t.tile)) return;
    boolean existed = all.containsKey(t);
    BuildingEntry<?> entry = all.get(t, new BuildingEntry<>(t, c));

    c.eachChildType((type, map) -> {
      TreeSeq<BuildingEntry<?>> temp = switch(type){
        case output -> output;
        case input -> input;
        case acceptor -> acceptor;
        case container -> container;
      };

      if(existed){
        entry.config.priority = priority;
        temp.remove(entry);
      }
      temp.add(entry);
    });
    all.put(t, entry);
  }
  
  public boolean remove(Building building){
    if (building == null) return false;
    BuildingEntry<?> entry = all.remove(building);
    if(entry != null){
      output.remove(entry);
      input.remove(entry);
      acceptor.remove(entry);
      container.remove(entry);
      return true;
    }
    return false;
  }
  
  public void clear(){
    for(Building building : all.keys()){
      remove(building);
    }
  }

  @Override
  public String toString() {
    return all.toString();
  }

  public static class BuildingEntry<T>{
    public final T entity;
    public TargetConfigure config;
    
    public BuildingEntry(T entity, TargetConfigure config){
      this.entity = entity;
      this.config = config;
    }
  }
}
