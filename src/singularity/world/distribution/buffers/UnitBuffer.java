package singularity.world.distribution.buffers;

import arc.Core;
import arc.graphics.Color;
import arc.graphics.g2d.TextureRegion;
import arc.struct.OrderedSet;
import arc.struct.Seq;
import mindustry.ctype.UnlockableContent;
import mindustry.graphics.Pal;
import mindustry.type.PayloadStack;
import mindustry.type.UnitType;
import mindustry.world.blocks.payloads.Payload;
import mindustry.world.blocks.payloads.UnitPayload;
import singularity.world.distribution.DistBufferType;
import singularity.world.distribution.DistributeNetwork;
import singularity.world.modules.PayloadModule;

public class UnitBuffer extends BaseBuffer<PayloadStack, UnitPayload, UnitBuffer.UnitPacket>{
  private final UnitPacket tmp = new UnitPacket();

  @Override
  public DistBufferType<?> bufferType(){
    return DistBufferType.unitBuffer;
  }

  public void put(UnitPayload unit){
    tmp.payloads.clear();
    tmp.payloads.add(unit);
    tmp.obj.item = unit.content();
    tmp.obj.amount = 1;
    put(tmp);
  }

  public void remove(UnitPayload unit){
    tmp.payloads.clear();
    tmp.payloads.add(unit);
    tmp.obj.item = unit.content();
    tmp.obj.amount = 1;
    remove(tmp);
  }

  public int getAmount(UnitType type){
    UnitPacket packet = get(type.id);
    return packet == null? 0: packet.obj.amount;
  }

  @Override
  public void deReadFlow(UnitPayload ct, Number amount){
    tmp.payloads.clear();
    tmp.payloads.add(ct);
    tmp.obj.item = ct.content();
    tmp.obj.amount = amount.intValue();

    deReadFlow(tmp);
  }

  @Override
  public void dePutFlow(UnitPayload ct, Number amount){
    tmp.payloads.clear();
    tmp.payloads.add(ct);
    tmp.obj.item = ct.content();
    tmp.obj.amount = amount.intValue();

    dePutFlow(tmp);
  }

  public UnitPayload take(){
    if(memory.values().hasNext()){
      UnitPacket p = memory.values().next();
      if(!p.isEmpty()){
        return p.take();
      }
    }
    return null;
  }

  public UnitPayload peek(){
    if(memory.values().hasNext()){
      UnitPacket p = memory.values().next();
      if(!p.isEmpty()){
        return p.get();
      }
    }
    return null;
  }

  public UnitPacket peekPacket(){
    if(memory.values().hasNext()){
      return memory.values().next();
    }
    return null;
  }

  @Override
  public Integer remainingCapacity(){
    return (Integer) super.remainingCapacity();
  }

  //no container usable, only buffer
  @Override
  public void bufferContAssign(DistributeNetwork network){}
  @Override
  public void bufferContAssign(DistributeNetwork network, UnitPayload ct){}
  @Override
  public Integer bufferContAssign(DistributeNetwork network, UnitPayload ct, Number amount){
    return 0;
  }
  @Override
  public Integer bufferContAssign(DistributeNetwork network, UnitPayload ct, Number amount, boolean deFlow){
    return 0;
  }

  @Override
  public PayloadModule generateBindModule(){
    return new UnitBufferModule();
  }

  @Override
  public String localization(){
    return Core.bundle.get("misc.unit");
  }

  @Override
  public Color displayColor(){
    return Pal.accent;
  }

  @Override
  public Integer usedCapacity(){
    return (Integer) super.usedCapacity();
  }

  public class UnitPacket extends Packet<PayloadStack, UnitPayload>{
    OrderedSet<UnitPayload> payloads = new OrderedSet<>();

    private UnitPacket(){}

    public UnitPacket(Seq<UnitPayload> units, int amount){
      payloads.addAll(units);
      UnitType t = null;
      for(UnitPayload payload: units){
        if(t == null) t = payload.unit.type();
        else if(t != payload.unit.type())
          throw new IllegalArgumentException("cannot put two type to a same packet");
      }
      obj = new PayloadStack(t, amount);
    }

    @Override
    public int id(){
      return obj.item.id;
    }

    @Override
    public UnitPayload get(){
      return payloads.orderedItems().peek();
    }

    public UnitPayload take(){
      UnitPayload res = get();
      remove(res);

      return res;
    }

    @Override
    public Color color(){
      return Pal.accent;
    }

    @Override
    public String localization(){
      return obj.item.localizedName;
    }

    @Override
    public TextureRegion icon(){
      return obj.item.fullIcon;
    }

    @Override
    public int occupation(){
      return obj.amount*bufferType().unit();
    }

    @Override
    public Integer amount(){
      return obj.amount;
    }

    @Override
    protected void setZero(){
      payloads.clear();
      readCaching += obj.amount;
      obj.amount = 0;
    }

    @Override
    protected void merge(Packet<PayloadStack, UnitPayload> other){
      if(other.id() == id()){
        int i = payloads.size;
        payloads.addAll(((UnitPacket) other).payloads);
        int o = payloads.size - i;
        obj.amount += o;
        putCaching += o*bufferType().unit();
      }
    }

    @Override
    protected void remove(Packet<PayloadStack, UnitPayload> other){
      if(other.id() == id()){
        int i = payloads.size;
        payloads.removeAll(((UnitPacket) other).payloads.orderedItems());
        int o = i - payloads.size;
        obj.amount -= o;
        readCaching += o*bufferType().unit();
      }
    }

    public void put(UnitPayload unit){
      tmp.payloads.clear();
      tmp.payloads.add(unit);
      tmp.obj.item = obj.item;
      tmp.obj.amount = 1;
      UnitBuffer.this.put(tmp);
    }

    public void remove(UnitPayload unit){
      tmp.payloads.clear();
      tmp.payloads.add(unit);
      tmp.obj.item = obj.item;
      tmp.obj.amount = 1;
      UnitBuffer.this.remove(tmp);
    }

    public void deRead(int amount){
      tmp.payloads.clear();
      tmp.obj.item = obj.item;
      tmp.obj.amount = amount;
      UnitBuffer.this.deReadFlow(tmp);
    }

    public void dePut(int amount){
      tmp.payloads.clear();
      tmp.obj.item = obj.item;
      tmp.obj.amount = amount;
      UnitBuffer.this.dePutFlow(tmp);
    }

    @Override
    public Packet<PayloadStack, UnitPayload> copy(){
      return new UnitPacket(payloads.orderedItems(), obj.amount);
    }
  }

  public class UnitBufferModule extends PayloadModule{
    static final Seq<Payload> temp = new Seq<>();

    @Override
    public int total(){
      return usedCapacity();
    }

    @Override
    public void add(Payload payload){
      if(payload instanceof UnitPayload p){
        put(p);
      }
    }

    @Override
    public int amountOf(UnlockableContent type){
      UnitPacket packet = UnitBuffer.this.get(type.id);
      return packet == null? 0: packet.amount();
    }

    @Override
    public Payload take(){
      return UnitBuffer.this.take();
    }

    @Override
    public Payload get(){
      return UnitBuffer.this.peek();
    }

    @Override
    public Payload get(UnlockableContent type){
      UnitPacket packet = UnitBuffer.this.get(type.id);
      return packet == null? null: packet.get();
    }

    @Override
    public Payload remove(UnlockableContent type){
      UnitPacket packet = UnitBuffer.this.get(type.id);
      return packet == null? null: packet.take();
    }

    @Override
    public void removeAll(UnlockableContent type){
      for(UnitPacket packet: UnitBuffer.this){
        packet.setZero();
      }
    }

    @Override
    public boolean isEmpty(){
      return UnitBuffer.this.usedCapacity() <= 0;
    }

    @Override
    public Iterable<Payload> iterate(){
      temp.clear();
      for(UnitPacket packet: UnitBuffer.this){
        temp.addAll(packet.payloads);
      }
      return temp;
    }
  }
}
