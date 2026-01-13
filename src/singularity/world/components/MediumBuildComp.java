package singularity.world.components;

import arc.util.Time;
import universecore.components.blockcomp.BuildCompBase;
import universecore.components.blockcomp.Takeable;

public interface MediumBuildComp extends BuildCompBase, Takeable{
 // @Annotations.BindField("mediumContains")
  default float mediumContains(){
    return 0;
  }

//  @Annotations.BindField("mediumContains")
  default void mediumContains(float value){}

  default MediumComp getMediumBlock(){
    return getBlock(MediumComp.class);
  }

  default boolean acceptMedium(MediumBuildComp source){
    return getBuilding().interactable(source.getBuilding().team) && mediumContains() < getMediumBlock().mediumCapacity();
  }

  default float remainingMediumCapacity(){
    return getMediumBlock().mediumCapacity() - mediumContains();
  }

  default float acceptMedium(MediumBuildComp source, float amount){
    return acceptMedium(source)? Math.min(remainingMediumCapacity(), amount): 0;
  }

  default void handleMedium(MediumBuildComp source, float amount){
    mediumContains(mediumContains() + Math.max(amount - getMediumBlock().lossRate()*Time.delta, 0));
  }

  default void removeMedium(float amount){
    mediumContains(mediumContains() - amount);
  }

  default void dumpMedium(){
    MediumBuildComp next = (MediumBuildComp) getNext("medium", e -> e instanceof MediumBuildComp && ((MediumBuildComp) e).acceptMedium(this, getMediumBlock().mediumMoveRate()) > 0);
    if(next == null) return;

    float move = Math.min(mediumContains(), getMediumBlock().mediumMoveRate()*getBuilding().delta());
    move = Math.min(move, next.remainingMediumCapacity());

    removeMedium(move);
    next.handleMedium(this, move);
  }
}
