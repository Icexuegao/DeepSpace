package ice.vars.researchs;

import arc.Core;
import arc.util.Nullable;


public abstract class RevealGroup {
  protected final String name;
  protected boolean revealed;
  @Nullable protected RevealGroup require;

  public RevealGroup(String name) {
    this.name = name;
  }

  public void init(){
   // revealed = Sgl.globals.getBool(name + "_revealed", false);
    applyTrigger();
  }

  public void reveal(){
    if(!revealed){
      revealed = true;
    //  Sgl.globals.put(name + "_revealed", true);

    //  Events.fire(new SglEventTypes.RevealedEvent(this));
    }
  }

  public void reset() {
    revealed = false;
   // Sgl.globals.put(name + "_revealed", false);
  }

  public boolean isRevealed(){
    return (require == null || require.isRevealed()) && revealed;
  }

  public String localized(){
    return Core.bundle.get("research." + name + ".reveal");
  }

  public abstract void applyTrigger();

  public static class ResearchReveal extends RevealGroup{
    private final ResearchProject project;

    public ResearchReveal(String name, ResearchProject project) {
      super(name);
      this.project = project;
    }

    @Override
    public String localized() {
      return Core.bundle.format("research.inspire.researched", project.localizedName);
    }

    @Override
    public void applyTrigger() {
    /*  Events.on(SglEventTypes.ResearchCompletedEvent.class, e -> {
        if (!revealed && e.research == project) {
          reveal();
        }
      });*/
    }
  }
}
