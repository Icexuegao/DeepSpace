package singularity.world.distribution;

import arc.func.Prov;
import arc.struct.ObjectMap;
import mindustry.Vars;
import mindustry.ctype.UnlockableContent;
import mindustry.world.Block;
import mindustry.world.blocks.liquid.LiquidRouter;
import mindustry.world.blocks.storage.StorageBlock;

public class DistSupportContainerTable{
  private final ObjectMap<Block, Container> supports = new ObjectMap<>();

  public Container getContainer(Block block){
    return getContainer(block, null);
  }

  public Container getContainer(Block block, Prov<Container> prov){
    if(prov == null) return supports.get(block);
    return supports.get(block, prov);
  }

  public void setSupport(Block block, boolean isIntegrate, Object... capacities){
    Container cont = supports.get(block);
    if(cont == null){
      supports.put(block, cont = new Container(block, isIntegrate));
    }

    for(int i = 0; i < capacities.length; i+=2){
      cont.capacities.put((DistBufferType<?>) capacities[i], ((Number)capacities[i+1]).floatValue());
    }
  }

  public void setDefaultSupports(){
    for(Block block: Vars.content.blocks()){
      if(block instanceof StorageBlock){
        setSupport(block, false, DistBufferType.itemBuffer, block.itemCapacity);
      }

      if(block instanceof LiquidRouter && block.liquidCapacity > 100){
        setSupport(block, true, DistBufferType.liquidBuffer, block.liquidCapacity);
      }
    }
  }

  public static class Container{
    public final Block cont;
    public final boolean isIntegrate;
    public final ObjectMap<DistBufferType<?>, Float> capacities = new ObjectMap<>();

    public Container(Block cont, boolean isIntegrate){
      this.cont = cont;
      this.isIntegrate = isIntegrate;
    }

    public void setCapacity(DistBufferType<?> buff, float amount){
      capacities.put(buff, isIntegrate? amount:
          amount*Vars.content.getBy(buff.targetType()).select(c -> c instanceof UnlockableContent uc && !uc.isHidden()).size);
    }

    public float getCapacity(DistBufferType<?> type){
      return capacities.get(type, 0f);
    }
  }
}
