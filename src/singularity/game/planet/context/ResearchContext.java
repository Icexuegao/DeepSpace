package singularity.game.planet.context;

import arc.struct.Seq;
import arc.util.io.Reads;
import arc.util.io.Writes;
import mindustry.Vars;
import mindustry.game.Team;
import singularity.Sgl;
import singularity.game.planet.Chunk;
import singularity.game.planet.ChunkContext;
import singularity.game.researchs.ResearchGroup;
import singularity.game.researchs.ResearchProject;
import singularity.world.blocks.research.ResearchDevice;

public class ResearchContext extends ChunkContext {
  public static float researchTimer = 60f;

  @Dependence public ResourceStatistic resources;

  public ResearchGroup researchGroup;

  public Seq<ResearchDevice> devices = new Seq<>();
  public int techPoints;
  public boolean processing;

  protected float timer;
  protected ResearchProject processingProject;

  public ResearchContext(Team team) {
    super(team);
  }

  public void switchProcessing(ResearchProject project) {
    if (processingProject != null) {
      processingProject.processing = null;
    }
    processingProject = project;
    processingProject.processing = this;
  }

  public ResearchProject currentProcessing(){
    return processingProject;
  }

  @Override
  public void init(Chunk ownerChunk) {
    super.init(ownerChunk);
    researchGroup = Sgl.researches.getGroup(ownerChunk.sector.planet);
  }

  @Override
  public void install() {

  }

  @Override
  public void uninstall() {

  }

  @Override
  public void update(float delta) {
    if (processingProject == null || processingProject.isCompleted()) return;

    if (checkResource()) timer += delta;

    while (timer >= researchTimer){
      timer -= researchTimer;

      processingProject.researchProcess(techPoints);
    }
  }

  protected boolean checkResource() {
    return true; //TODO
  }

  @Override public void updateFore(float delta) {}
  @Override public void updateBack(float delta) {}

  @Override
  public void load(Reads reads) {
    boolean proc = reads.bool();
    if (proc) switchProcessing(researchGroup.getResearch(reads.str()));
    processing = reads.bool();
    timer = reads.f();
  }

  @Override
  public void save(Writes writes) {
    writes.bool(processingProject != null);
    if (processingProject != null) writes.str(processingProject.name);
    writes.bool(processing);
    writes.f(timer);
  }

  @Override
  public boolean active() {
    return Vars.player.team() == team // 目前尚未考虑多队伍科技树，或许未来的某个作品会做队伍乱斗区分科技树
           && processing && processingProject != null && !processingProject.isCompleted();
  }

  public void updateTechs(int baseTechPoints) {
    techPoints = baseTechPoints;
    for (ResearchDevice device : devices) {
      techPoints += device.provTechPoints;
    }
  }
}
