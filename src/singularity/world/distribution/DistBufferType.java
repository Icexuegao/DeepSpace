package singularity.world.distribution;

import arc.func.Prov;
import arc.struct.Seq;
import mindustry.ctype.ContentType;
import mindustry.gen.Building;
import mindustry.world.blocks.payloads.Payload;
import singularity.world.components.PayloadBuildComp;
import singularity.world.distribution.buffers.BaseBuffer;
import singularity.world.distribution.buffers.ItemsBuffer;
import singularity.world.distribution.buffers.LiquidsBuffer;
import singularity.world.distribution.buffers.UnitBuffer;
import singularity.world.modules.SglLiquidModule;

public abstract class DistBufferType<T extends BaseBuffer<?, ?, ?>>{
  private static final Seq<DistBufferType<?>> tmp = new Seq<>();
  public static DistBufferType<?>[] all;

  public static DistBufferType<ItemsBuffer> itemBuffer = new DistBufferType<>(ContentType.item, 8, ItemsBuffer::new){
    @Override
    public Integer containerUsed(Building build){
      return build.items.total();
    }
  };
  public static DistBufferType<LiquidsBuffer> liquidBuffer = new DistBufferType<>(ContentType.liquid, 1, LiquidsBuffer::new){
    @Override
    public Float containerUsed(Building build){
      if(build.liquids instanceof SglLiquidModule li){
        return li.total();
      }

      float[] total = {0};
      build.liquids.each((l, a) -> total[0] += a);
      return total[0];
    }
  };
  public static DistBufferType<UnitBuffer> unitBuffer = new DistBufferType<>(ContentType.unit, 64, UnitBuffer::new){
    static final Seq<Payload> seq = new Seq<>();

    @Override
    public Number containerUsed(Building build){
      if(build instanceof PayloadBuildComp b){
        return b.getPayloadCapacity();
      }
      else{
        if(build.block.acceptsPayload){
          return build.getPayload() == null? 1: 0;
        }
        else return 0;
      }
    }
  };

  public final int id;

  private final ContentType contentType;
  private final Prov<T> initializer;
  private final int unit;
  
  public DistBufferType(ContentType contentType, int unit, Prov<T> initializer){
    this.contentType = contentType;
    this.unit = unit;
    this.initializer = initializer;
    id = tmp.size;
    tmp.add(this);

    all = tmp.toArray(DistBufferType.class);
  }

  public static DistBufferType<?> typeOf(ContentType type) {
    for (DistBufferType<?> distBufferType : all) {
      if (distBufferType.targetType() == type) return distBufferType;
    }

    return null;
  }

  public T get(int capacity){
    T buffer = initializer.get();
    buffer.capacity = capacity;
    return buffer;
  }

  public ContentType targetType(){
    return contentType;
  }

  public abstract Number containerUsed(Building build);

  public int unit(){
    return unit;
  }
}
