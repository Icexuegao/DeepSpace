package singularity.game.researchs;

import arc.struct.OrderedMap;
import arc.struct.Seq;
import mindustry.ctype.UnlockableContent;
import mindustry.type.Planet;
import singularity.Sgl;
import singularity.world.blocks.research.ResearchDevice;

public class ResearchManager {
  private final OrderedMap<Planet, ResearchGroup> allProjects = new OrderedMap<>();

  public ResearchGroup makeGroup(Planet planet){
    ResearchGroup group = new ResearchGroup(planet);
    allProjects.put(planet, group);

    return group;
  }

  public ResearchGroup getGroup(Planet planet){
    return allProjects.get(planet);
  }

  public Seq<ResearchProject> listResearches(Planet planet){
    return allProjects.get(planet).listResearches();
  }

  public void init(){
    allProjects.values().forEach(ResearchGroup::init);
  }

  public void reset(){
    allProjects.values().forEach(ResearchGroup::reset);
  }

  public void save(){
    allProjects.values().forEach(ResearchGroup::save);
  }

  public void load(){
    allProjects.values().forEach(ResearchGroup::load);
  }

  public static class ResearchSDL{
    private static ResearchGroup context;
    private static OrderedMap<ResearchProject, Runnable> tasks;
    private static ResearchProject currProject;
    private static boolean inProjectContext;
    private static ResearchManager manager = Sgl.researches;
    private static RevealGroup currReveal = null;

    protected static void setManager(ResearchManager manager){
      ResearchSDL.manager = manager;
    }

    protected static void makePlanetContext(Planet planet, Runnable runnable){
      ResearchGroup last = context;
      OrderedMap<ResearchProject, Runnable> lastTasks = tasks;
      context = manager.makeGroup(planet);
      tasks = new OrderedMap<>();

      runnable.run();
      tasks.forEach(e -> {
        inProjectContext = true;
        currProject = e.key;
        e.value.run();
        inProjectContext = false;
      });

      context = last;
      tasks = lastTasks;
    }

    protected static void reveal(RevealGroup group, Runnable runnable){
      RevealGroup last = currReveal;
      currReveal = group;
      group.require = last;
      runnable.run();
      currReveal = last;
    }

    protected static ResearchProject byName(String name){
      return context.getResearch(name);
    }

    protected static ResearchProject research(String name, int techRequires, int techRequiresRandom, Runnable runnable){
      ResearchProject res = research(name, techRequires, techRequiresRandom);
      res.setReveal(currReveal);
      tasks.put(res, runnable);

      return res;
    }

    protected static ResearchProject research(String name, int techRequires, Runnable runnable){
      ResearchProject res = research(name, techRequires);
      res.setReveal(currReveal);
      tasks.put(res, runnable);

      return res;
    }

    protected static ResearchProject research(String name, int techRequires, int techRequiresRandom){
      checkContext();

      ResearchProject project = new ResearchProject(name, techRequires, techRequiresRandom);
      project.setReveal(currReveal);
      context.addProject(project);

      return project;
    }

    protected static ResearchProject research(String name, int techRequires){
      checkContext();

      ResearchProject project = new ResearchProject(name, techRequires);
      project.setReveal(currReveal);
      context.addProject(project);

      return project;
    }

    protected static void contents(UnlockableContent... contents){
      checkProjectContext();
      currProject.addContent(contents);
    }

    protected static void dependencies(ResearchProject... dependencies){
      checkProjectContext();
      currProject.addDependency(dependencies);
    }

    protected static void dependencies(String... dependencies){
      checkProjectContext();
      for (String dependency : dependencies) {
        currProject.addDependency(context.getResearch(dependency));
      }
    }

    protected static void inspire(Inspire inspire){
      checkProjectContext();
      currProject.setInspire(inspire);
    }

    protected static void showRevealess(){
      checkProjectContext();
      currProject.showRevealess();
    }

    protected static void hideTechs(){
      checkProjectContext();
      currProject.hideTechs();
    }

    protected static void devices(ResearchDevice... devices){
      checkProjectContext();
      currProject.addRequireDevice(devices);
    }

    private static void checkContext() {
      if (context == null) throw new IllegalStateException("No planet context");
      if (inProjectContext) throw new IllegalStateException("Already in project context");
    }

    private static void checkProjectContext() {
      if (currProject == null) throw new IllegalStateException("No project context");
      if (!inProjectContext) throw new IllegalStateException("Not in project context");
    }
  }
}
