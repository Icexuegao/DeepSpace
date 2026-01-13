package singularity.world.distribution.buffers;

import arc.graphics.Color;
import arc.graphics.g2d.TextureRegion;
import arc.math.Mathf;
import arc.math.WindowedMean;
import arc.struct.IntMap;
import arc.util.Interval;
import arc.util.Nullable;
import mindustry.world.modules.BlockModule;
import singularity.world.distribution.DistBufferType;
import singularity.world.distribution.DistributeNetwork;

import java.util.Iterator;

public abstract class BaseBuffer<C, CType, T extends BaseBuffer.Packet<C, CType>> implements Iterable<T>{
  public int capacity;
  protected int used;
  
  protected Interval timer = new Interval();
  protected WindowedMean putMean = new WindowedMean(6), readMean = new WindowedMean(6);
  protected int putBufferCaching, readBufferCaching;
  protected float putRate = -1, readRate = -1;
  protected int maxUsed = -1;
  
  protected IntMap<T> memory = new IntMap<>();

  private boolean calculateDelta, calculateDetail;
  
  public int space(){
    return capacity - used;
  }
  
  public Number remainingCapacity(){
    return space()/bufferType().unit();
  }
  
  public Number usedCapacity(){
    return used/bufferType().unit();
  }
  
  public void update(){
    if(calculateDelta){
      if(timer.get(10)){
        putMean.add(putBufferCaching);
        readMean.add(readBufferCaching);

        maxUsed = Math.max(maxUsed, used);

        putBufferCaching = readBufferCaching = 0;

        if(putMean.hasEnoughData()){
          putRate = putMean.mean()/10f;
        }

        if(readMean.hasEnoughData()){
          readRate = readMean.mean()/10f;
        }

        if(calculateDetail) for(IntMap.Entry<T> entry: memory){
          entry.value.calculateDelta();
        }
      }
    }
    else {
      putBufferCaching = 0;
      readBufferCaching = 0;
      maxUsed = -1;
      putRate = -1;
      readRate = -1;
      putMean.clear();
      readMean.clear();
      for(IntMap.Entry<T> entry: memory){
        if(entry.value.occupation() <= 0) memory.remove(entry.key);
        entry.value.clearMean();
      }
    }

    if(!calculateDelta || !calculateDetail){
      for(IntMap.Entry<T> entry: memory){
        entry.value.readMean.clear();
        entry.value.putMean.clear();
        entry.value.readRate = 0;
        entry.value.readCaching = 0;
        entry.value.putCaching = 0;
        entry.value.putRate = 0;
      }
    }
  }

  public void startCalculate(boolean detail){
    calculateDelta = true;
    calculateDetail = detail;
  }

  public void endCalculate(){
    calculateDelta = false;
    calculateDetail = false;
  }

  public float putRate(){
    return putRate;
  }

  public float readRate(){
    return readRate;
  }

  @SuppressWarnings("unchecked")
  public void put(T packet){
    int useByte = packet.occupation();
    used += useByte;
    putBufferCaching += useByte;
    
    T target;
    if((target = memory.get(packet.id())) != null){
      target.merge(packet);
    }
    else memory.put(packet.id(), (T) packet.copy());
  }
  
  public void set(T packet){
    int put;
    Packet<?, ?> existed = memory.get(packet.id());
    if(existed == null){
      put = packet.occupation();
    }
    else{
      put = packet.occupation() - existed.occupation();
    }

    if(put > 0){
      putBufferCaching += put;
    }
    else readBufferCaching += put;
    used += put;
    memory.put(packet.id(), packet);
  }
  
  @SuppressWarnings("unchecked")
  @Nullable
  public <Type extends T> Type get(int id){
    return (Type)memory.get(id);
  }
  
  public void remove(T packet){
    T p = memory.get(packet.id());
    if(p != null){
      float del = Math.min(p.occupation(), packet.occupation());
      used -= del;
      readBufferCaching += del;
      p.remove(packet);
    }
  }
  
  public void remove(int id){
    T packet = memory.remove(id);
    if(packet != null){
      used -= packet.occupation();
      readBufferCaching += packet.occupation();
      packet.setZero();
    }
  }

  public void deReadFlow(T packet){
    readBufferCaching = (int) Mathf.maxZero(readBufferCaching - packet.occupation());
    T p = get(packet.id());
    if(p == null) return;
    p.deRead(packet);
  }

  public void dePutFlow(T packet){
    putBufferCaching = (int) Mathf.maxZero(putBufferCaching - packet.occupation());
    T p = get(packet.id());
    if(p == null) return;
    p.dePut(packet);
  }

  public abstract DistBufferType<?> bufferType();

  public abstract void deReadFlow(CType ct, Number amount);

  public abstract void dePutFlow(CType ct, Number amount);

  public abstract void bufferContAssign(DistributeNetwork network);

  public abstract void bufferContAssign(DistributeNetwork network, CType ct);

  public abstract Number bufferContAssign(DistributeNetwork network, CType ct, Number amount);

  public abstract Number bufferContAssign(DistributeNetwork network, CType ct, Number amount, boolean deFlow);

  public abstract BlockModule generateBindModule();

  public abstract String localization();

  public abstract Color displayColor();

  @Override
  public Iterator<T> iterator(){
    return memory.values().iterator();
  }

  public Number maxCapacity() {
    return capacity/bufferType().unit();
  }

  public static abstract class Packet<Obj, Type>{
    public Obj obj;
    WindowedMean putMean = new WindowedMean(6), readMean = new WindowedMean(6);
    int putCaching, readCaching;
    float putRate = -1;
    float readRate = -1;
    
    public abstract int id();
    
    public abstract Type get();

    public abstract Color color();

    public abstract String localization();

    public abstract TextureRegion icon();
    
    public abstract int occupation();
    
    public abstract Number amount();

    protected abstract void setZero();
    
    protected abstract void merge(Packet<Obj, Type> other);
    
    protected abstract void remove(Packet<Obj, Type> other);

    public boolean isEmpty(){
      return occupation() <= 0;
    }

    public void calculateDelta(){
      putMean.add(putCaching);
      putCaching = 0;
      if(putMean.hasEnoughData()) putRate = putMean.mean()/10;

      readMean.add(readCaching);
      readCaching = 0;
      if(readMean.hasEnoughData()) readRate = readMean.mean()/10;
    }

    public float putRate(){
      return putRate;
    }

    public float readRate(){
      return readRate;
    }

    public void clearMean(){
      putCaching = 0;
      readCaching = 0;
      putMean.clear();
      readMean.clear();
    }

    public void deRead(Packet<Obj, Type> other){
      readCaching = (int) Mathf.maxZero(readCaching - other.occupation());
    }

    public void dePut(Packet<Obj, Type> other){
      putCaching = (int) Mathf.maxZero(putCaching - other.occupation());
    }

    public abstract Packet<Obj, Type> copy();
  }
}
