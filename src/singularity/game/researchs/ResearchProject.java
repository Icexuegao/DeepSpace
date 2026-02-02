package singularity.game.researchs;

import arc.Core;
import arc.Events;
import arc.graphics.g2d.TextureRegion;
import arc.math.Mathf;
import arc.struct.Seq;
import arc.util.Nullable;
import mindustry.ctype.UnlockableContent;
import singularity.Sgl;
import singularity.Singularity;
import singularity.core.SglEventTypes;
import singularity.game.planet.context.ResearchContext;
import singularity.world.blocks.research.ResearchDevice;

public class ResearchProject {
  public final String name;
  public final int techRequires;
  public final int techRequiresRandom;

  public final Seq<ResearchProject> dependencies = new Seq<>();
  public final Seq<UnlockableContent> contents = new Seq<>();
  public final Seq<ResearchDevice> requireDevices = new Seq<>();

  public String localizedName;
  public String description;
  public String slogan = "slogan";
  public TextureRegion icon;

  @Nullable public Inspire inspire;
  @Nullable public RevealGroup reveal;

  @Nullable public ResearchContext processing;

  public boolean showIfRevealess;
  public boolean hideTechs;
  public ResearchGroup group;

  protected boolean isCompleted;
  protected int techRequiresReal;
  protected int researched;

  public ResearchProject(String name, int techRequires, int techRequiresRandom) {
    this.name = name;
    this.techRequires = techRequires;
    this.techRequiresRandom = techRequiresRandom;

    localizedName = Core.bundle.get("research." + name + ".name");
    description = Core.bundle.get("research." + name + ".description");
    icon = Singularity.getModAtlas("research_" + name, null);
  }

  public ResearchProject(String name, int techRequires) {
    this(name, techRequires, 0);
  }

  public ResearchProject hideTechs(){
    hideTechs = true;
    return this;
  }

  public ResearchProject showRevealess(){
    showIfRevealess = true;
    return this;
  }

  public ResearchProject setInspire(Inspire inspire){
    this.inspire = inspire;
    return this;
  }

  public ResearchProject setReveal(RevealGroup reveal) {
    this.reveal = reveal;
    return this;
  }

  public ResearchProject addDependency(ResearchProject... dependencies){
    this.dependencies.addAll(dependencies);
    return this;
  }

  public ResearchProject addContent(UnlockableContent... contents){
    this.contents.addAll(contents);
    return this;
  }

  public ResearchProject addRequireDevice(ResearchDevice... requireDevices){
    this.requireDevices.addAll(requireDevices);
    return this;
  }

  public boolean checkDeviceValid(Seq<ResearchDevice> devices){
    o: for (ResearchDevice requireDevice : requireDevices) {
      for (ResearchDevice device : devices) {
        if (device.isCompatible(requireDevice)) continue o;
      }

      return false;
    }

    return true;
  }

  public void init(){
    if (inspire != null) {
      inspire.init(this);
      inspire.applyTrigger(this);
    }

    if (dependenciesCompleted() && researched >= techRequiresReal){
      isCompleted = true;
    }
  }

  public boolean dependenciesCompleted(){
    for (ResearchProject dependency : dependencies) {
      if(!dependency.isCompleted()) return false;
    }

    return true;
  }

  public boolean isRevealed() {
    return reveal == null || reveal.isRevealed();
  }

  public boolean requiresRevealed() {
    return reveal == null || reveal.require == null || reveal.require.isRevealed();
  }

  public boolean isCompleted(){
    return isCompleted;
  }

  public int getRealRequireTechs(){
    return techRequiresReal;
  }

  public int getResearched(){
    return researched;
  }

  public float progress(){
    return (float)researched / techRequiresReal;
  }

  public boolean isProcessing(){
    return processing != null;
  }

  public boolean researchProcess(int techPoints){
    if (isCompleted()) return true;
    researched += techPoints;

    boolean res = checkComplete();

    save();

    return res;
  }

  public boolean checkComplete(){
    if (isCompleted()) return true;

    for (ResearchProject dependency : dependencies) {
      if(!dependency.checkComplete()) return false;
    }

    if(researched >= techRequiresReal){
      completeNow();
      return true;
    }

    return false;
  }

  public void completeNow(){
    isCompleted = true;

    researched = techRequiresReal;

    for (UnlockableContent content : contents) {
      content.unlock();
    }

    Events.fire(new SglEventTypes.ResearchCompletedEvent(this));

    save();
  }

  public void applyInspireNow() {
    if (inspire != null) inspire.apply(this);
  }

  public void revealNow(){
    if (reveal != null) reveal.reveal();
  }

  public void reset(){
    techRequiresReal = techRequires + Mathf.random(techRequiresRandom);
    researched = 0;
    isCompleted = false;

    for (UnlockableContent content : contents) {
      content.clearUnlock();
    }

    if (inspire != null) inspire.reset();

    save();
  }

  public void load(){
    techRequiresReal = Sgl.globals.getInt("research_" + name + "_requireReal", techRequires + Mathf.random(techRequiresRandom));
    researched = Sgl.globals.getInt("research_" + name + "_researched", 0);
  }

  public void save(){
    Sgl.globals.put("research_" + name + "_requireReal", techRequiresReal);
    Sgl.globals.put("research_" + name + "_researched", researched);
  }
}
