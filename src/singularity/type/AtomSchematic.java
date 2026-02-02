package singularity.type;

import arc.Core;
import arc.func.Cons;
import mindustry.content.TechTree;
import mindustry.ctype.ContentType;
import mindustry.ctype.UnlockableContent;
import mindustry.game.Objectives;
import mindustry.type.Item;
import mindustry.type.ItemStack;
import singularity.world.consumers.SglConsumers;
import universecore.util.TechTreeConstructor;
import universecore.world.consumers.BaseConsumers;

public class AtomSchematic extends UnlockableContent{
  public static final ItemStack[] ITEM_STACKS = new ItemStack[0];
  public final Item item;
  public final int researchConsume;
  public final SglConsumers request = new SglConsumers(false);

  public UnlockableContent dependence;

  public AtomSchematic(Item item, int researchConsume){
    super("schematic_" + item.name);
    this.item = item;
    this.researchConsume = researchConsume;

    request.setSelectable(() -> unlockedNow() ? BaseConsumers.Visibility.usable : BaseConsumers.Visibility.hidden);
  }

  public void setTechTree(UnlockableContent dependence){
    this.dependence = dependence;
    TechTree.TechNode node = new TechTree.TechNode(TechTreeConstructor.get(dependence), this, ITEM_STACKS);
    node.objectives.add(new Objectives.Objective(){
      @Override
      public boolean complete(){
        return false;
      }

      @Override
      public String display(){
        return Core.bundle.get("infos.doDestruct");
      }
    });
  }

  public BaseConsumers.Visibility researchVisibility(){
    return unlockedNow()? BaseConsumers.Visibility.unusable: dependence == null || dependence.unlockedNow()?
        BaseConsumers.Visibility.usable: BaseConsumers.Visibility.hidden;
  }

  public void destructing(int amount){
    int curr;
    Core.settings.put("destructed." + name, curr = Math.min(researchConsume, Core.settings.getInt("destructed." + name, 0) + amount));
    if(curr >= researchConsume) unlock();
  }

  public int residualDemand(){
    return researchConsume - Core.settings.getInt("destructed." + name, 0);
  }

  public float researchProgress(){
    return 1 - (float)residualDemand()/researchConsume;
  }

  public int destructed(){
    return Core.settings.getInt("destructed." + name, 0);
  }

  @Override
  public void getDependencies(Cons<UnlockableContent> cons){
    cons.get(item);
  }

  @Override
  public ContentType getContentType(){
    return SglContents.atomSchematic;
  }
}
