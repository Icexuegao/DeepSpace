package ice.vars.researchs;

import arc.Core;
import arc.Events;
import arc.func.Boolf;
import mindustry.Vars;
import mindustry.game.EventType;
import mindustry.type.UnitType;
import mindustry.world.Block;

public abstract class Inspire {
  protected String name;

  public float provProgress = 0.5f;
  public boolean applied;

  public String localized;
  public String description;

  public Inspire() {
    this.name = null;
  }

  public Inspire(String name) {
    this.name = name;
  }

  public Inspire setProvProgress(float provProgress) {
    this.provProgress = provProgress;
    return this;
  }

  public void init(ResearchProject project){
    if (name == null) name = "inspire_" + project.name;
   // applied = Sgl.globals.getBool( name + "_applied", false);

    localized = Core.bundle.get("research." + name + ".inspire");
    description = Core.bundle.get(
        "research." + name + ".inspire.description",
        Core.bundle.format("infos.inspiredBy", project.localizedName)
    );
  }

  public void apply(ResearchProject project){
    if (applied || !project.isRevealed()) return;

    applied = true;
   // Sgl.globals.put(name + "_applied", true);

    project.researchProcess((int) (project.getRealRequireTechs()*provProgress));

  //  Events.fire(new SglEventTypes.ResearchInspiredEvent(this, project));
  }

  public String getName(){
    return name;
  }

  public void reset(){
    applied = false;
    //Sgl.globals.put(name + "_applied", false);
  }

  public abstract void applyTrigger(ResearchProject project);

  public static class EventInspire extends Inspire{
    public final Class<?> eventType;
    public final Boolf<?> check;

    public <T> EventInspire(String name, Class<T> eventType, Boolf<T> check) {
      super(name);
      this.eventType = eventType;
      this.check = check;
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    @Override
    public void applyTrigger(ResearchProject project) {
      Events.on(eventType, e -> {
        if(!applied && ((Boolf)check).get(e)) {
          apply(project);
        }
      });
    }
  }

  public static abstract class CounterInspire extends Inspire{
    public final int requireCount;

    private int count;

    protected CounterInspire(int requireCount) {
      this.requireCount = requireCount;
    }

    protected CounterInspire(String name, int requireCount) {
      super(name);
      this.requireCount = requireCount;
    }

    @Override
    public void init(ResearchProject project) {
      super.init(project);
    //  count = Sgl.globals.getInt(name + "_count", 0);
    }

    @Override
    public void reset() {
      super.reset();
      count = 0;
    //  Sgl.globals.put(name + "_count", 0);
    }

    @Override
    public void apply(ResearchProject project) {
      if (applied || !project.isRevealed()) return;
      count++;
      //Sgl.globals.put(name + "_count", count);

      if (count >= requireCount) super.apply(project);
    }
  }

  public static class ResearchInspire extends Inspire{
    public final ResearchProject researchProject;

    public ResearchInspire(ResearchProject researchProject) {
      super();
      this.researchProject = researchProject;
    }

    public ResearchInspire(String name, ResearchProject researchProject) {
      super(name);
      this.researchProject = researchProject;
    }

    @Override
    public void init(ResearchProject project) {
      super.init(project);

      localized = Core.bundle.format("research.inspire.researched", researchProject.localizedName);
    }

    @Override
    public void applyTrigger(ResearchProject project) {
   /*   Events.on(SglEventTypes.ResearchCompletedEvent.class, e -> {
        if(!applied && e.research == researchProject) {
          apply(project);
        }
      });*/
    }
  }

  public static class PlaceBlockInspire extends CounterInspire{
    public final Block block;

    public PlaceBlockInspire(Block block) {
      super(1);
      this.block = block;
    }

    public PlaceBlockInspire(Block block, int requireCount) {
      super(requireCount);
      this.block = block;
    }

    public PlaceBlockInspire(String name, Block block) {
      super(name, 1);
      this.block = block;
    }

    public PlaceBlockInspire(String name, Block block, int requireCount) {
      super(name, requireCount);
      this.block = block;
    }

    @Override
    public void init(ResearchProject project) {
      super.init(project);

      localized = requireCount == 1? Core.bundle.format("research.inspire.placeBlock", block.localizedName)
          : Core.bundle.format("research.inspire.placeBlocks", requireCount, block.localizedName);
    }

    @Override
    public void applyTrigger(ResearchProject project) {
      Events.on(EventType.BlockBuildEndEvent.class, e -> {
        if (!applied && e.team == Vars.player.team() && e.tile.build.block == block){
          apply(project);
        }
      });
    }
  }

  public static class CreateUnitInspire extends CounterInspire{
    public final UnitType unitType;

    public CreateUnitInspire(UnitType unitType) {
      super(1);
      this.unitType = unitType;
    }

    public CreateUnitInspire(UnitType unitType, int requireCount) {
      super(requireCount);
      this.unitType = unitType;
    }

    public CreateUnitInspire(String name, UnitType unitType) {
      super(name, 1);
      this.unitType = unitType;
    }

    public CreateUnitInspire(String name, UnitType unitType, int requireCount) {
      super(name, requireCount);
      this.unitType = unitType;
    }

    @Override
    public void init(ResearchProject project) {
      super.init(project);

      localized = requireCount == 1? Core.bundle.format("research.inspire.createUnit", unitType.localizedName)
          : Core.bundle.format("research.inspire.createUnits", requireCount, unitType.localizedName);
    }

    @Override
    public void applyTrigger(ResearchProject project) {
      Events.on(EventType.UnitCreateEvent.class, e -> {
        if (!applied && (e.spawner.team() == Vars.player.team() || e.spawnerUnit.team() == Vars.player.team())
        && e.unit.type == unitType){
          apply(project);
        }
      });
    }
  }
}
