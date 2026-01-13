package singularity.contents.override;

import arc.Core;
import mindustry.content.Planets;
import mindustry.ctype.UnlockableContent;
import universecore.util.OverrideContentList;

import static universecore.util.TechTreeConstructor.*;

public class OverrideTechThree implements OverrideContentList{
  @Override
  public void load(){
    currentRoot(Planets.serpulo.techTree);
  //  setNodeContent(OverrideBlocks.oldMelter, Blocks.melter);
   // setNodeContent(OverrideBlocks.oldPulverizer, Blocks.pulverizer);
  }

  private void setNodeContent(UnlockableContent old, UnlockableContent content){
    get(old).content = content;

    if(!content.unlocked() && Core.settings != null && Core.settings.getBool(old.name + "-unlocked", false)) content.quietUnlock();

    rebuildAll();
  }
}
