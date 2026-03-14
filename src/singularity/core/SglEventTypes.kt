package singularity.core;

import arc.struct.Seq;
import mindustry.entities.units.BuildPlan;
import singularity.game.researchs.Inspire;
import singularity.game.researchs.ResearchProject;
import singularity.game.researchs.RevealGroup;

public class SglEventTypes {
  public static class BuildPlanRotateEvent{
    public Seq<BuildPlan> plans;
    public int direction;
  }

  public static class BuildFlipRotateEvent{
    public Seq<BuildPlan> plans;
    public boolean x;
  }

  public static class ResearchCompletedEvent{
    public final ResearchProject research;

    public ResearchCompletedEvent(ResearchProject research) {
      this.research = research;
    }
  }

  public static class ResearchInspiredEvent {
    public final Inspire inspire;
    public final ResearchProject research;
    public ResearchInspiredEvent(Inspire inspire, ResearchProject research) {
      this.inspire = inspire;
      this.research = research;
    }
  }

  public static class RevealedEvent{
    public final RevealGroup group;

    public RevealedEvent(RevealGroup group) {
      this.group = group;
    }
  }
}
