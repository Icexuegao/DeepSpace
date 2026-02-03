package singularity.world.modules;

import arc.Core;
import arc.math.WindowedMean;
import arc.scene.ui.layout.Table;
import arc.util.Interval;
import arc.util.Scaling;
import arc.util.Strings;
import arc.util.io.Reads;
import arc.util.io.Writes;
import mindustry.Vars;
import mindustry.core.UI;
import mindustry.gen.Building;
import mindustry.graphics.Pal;
import mindustry.ui.Bar;
import mindustry.world.modules.BlockModule;
import singularity.core.UpdatePool;
import singularity.graphic.SglDrawConst;
import singularity.world.components.NuclearEnergyBuildComp;
import universecore.util.handler.FieldHandler;

public class NuclearEnergyModule extends BlockModule {
  static NuclearEnergyBuildComp lastShowFlow;

  static {
    UpdatePool.receive("updateEnergyFlow", () -> {
      Building nextFlowBuild = FieldHandler.getValueDefault(Vars.ui.hudfrag.blockfrag, "nextFlowBuild");

      if(nextFlowBuild instanceof NuclearEnergyBuildComp nuclearBuild && nuclearBuild.hasEnergy()){
        if (lastShowFlow != nuclearBuild){
          nuclearBuild.energy().stopFlow();
          lastShowFlow = nuclearBuild;
        }

        nuclearBuild.energy().updateFlow();
      }
    });
  }

  public final NuclearEnergyBuildComp entity;
  
  private float added, removed;
  private final WindowedMean addedMean = new WindowedMean(6), moveMean = new WindowedMean(6);
  private final Interval flowTimer = new Interval();
  
  /**目前具有的核能量大小*/
  private float energy = 0f;
  
  public float displayAdding = 0, displayMoving = 0f;
  
  public NuclearEnergyModule(NuclearEnergyBuildComp entity){
    this.entity = entity;
  }

  public void stopFlow() {
    added = 0;
    removed = 0;
    addedMean.clear();
    moveMean.clear();
    displayAdding = 0;
    displayMoving = 0;
  }

  public void updateFlow(){
    if(flowTimer.get(20f)){
      addedMean.add(added);
      moveMean.add(removed);
      added = 0;
      removed = 0;
      displayAdding = addedMean.hasEnoughData()? addedMean.mean()/20: -1;
      displayMoving = moveMean.hasEnoughData()? moveMean.mean()/20: -1;
    }
  }
  
  public void update(){
  }
  
  public float getEnergy(){
    return energy;
  }
  
  public void handle(float value){
    energy += value;
    if (value >= 0) added += value;
    if (value < 0) removed += value;
  }
  
  public void set(float value){
    energy = value;
  }
  
  public void display(Table table){
    table.row();
    table.table(energyBoard -> {
      energyBoard.defaults().pad(5).left();
      energyBoard.image(SglDrawConst.nuclearIcon).size(20).get().setScaling(Scaling.fit);
      energyBoard.add(new Bar(
          () -> Core.bundle.format("fragment.bars.nuclearContain",
              energy >= 1000? UI.formatAmount((long) energy): Strings.autoFixed(energy, 1),
              entity.energyCapacity() >= 1000? UI.formatAmount((long) entity.energyCapacity()): Strings.autoFixed(entity.energyCapacity(), 1),
              displayAdding < -0.1f? "--": "+" + UI.formatAmount((long) (displayAdding*60))
          ),
          () -> Pal.reactorPurple,
          () -> energy/entity.energyCapacity()
      )).height(18).padLeft(4).growX();
    }).growX().fillY();
  }
  
  @Override
  public void write(Writes write){
    write.f(energy);
  }

  @Override
  public void read(Reads read, boolean revision){
    energy = read.f();
    if (revision) {
      int length = read.i();
      for (int i = 0; i < length; i++) {
        read.i();
      }
    }
  }
}