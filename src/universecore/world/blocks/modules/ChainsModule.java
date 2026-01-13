package universecore.world.blocks.modules;

import arc.func.Cons;
import arc.util.io.Writes;
import mindustry.world.modules.BlockModule;
import universecore.components.blockcomp.ChainsBuildComp;
import universecore.world.blocks.chains.ChainsContainer;

public class ChainsModule extends BlockModule/* implements ExtraVariableComp */{
  public ChainsBuildComp entity;
  public ChainsContainer container;
  
  public ChainsModule(ChainsBuildComp entity){
    this.entity = entity;
  }
  
  public ChainsContainer newContainer(){
    ChainsContainer old = entity.getChains().container;

    entity.getChains().container = new ChainsContainer();
    entity.containerCreated(old);

    entity.getChains().container.add(entity);

    return entity.getChains().container;
  }
  
  public void each(Cons<ChainsBuildComp> cons){
    for(ChainsBuildComp other: container.all){
      cons.get(other);
    }
  }

 /* @Override
  public Map<String, Object> extra() {
    return container.extra();
  }

  *//**@ 请使用setVar(String, Object)
   * @see ChainsModule #setVar(String, Object) *//*
  @Deprecated
  public void putVar(String key, Object obj){
    container.setVar(key, obj);
  }
  */
  @Override
  public void write(Writes write){}
}
