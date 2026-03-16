package singularity.game.researchs

import arc.struct.OrderedMap
import arc.struct.Seq
import mindustry.type.Planet
import singularity.Sgl

class ResearchManager {
  private val allProjects = OrderedMap<Planet, ResearchGroup>()

  fun makeGroup(planet: Planet): ResearchGroup {
    val group = ResearchGroup(planet)
    allProjects.put(planet, group)

    return group
  }

  fun getGroup(planet: Planet): ResearchGroup {
    return allProjects.get(planet)
  }

  fun listResearches(planet: Planet): Seq<ResearchProject> {
    return allProjects.get(planet)?.listResearches() ?: throw Exception("No research group for planet $planet")
  }

  fun init() {
    for (group in allProjects.values()) {
      group.init()
    }
  }

  fun reset() {
    for (group in allProjects.values()) {
      group.reset()
    }
  }

  fun save() {
    for (group in allProjects.values()) {
      group.save()
    }
  }

  fun load() {
    for (group in allProjects.values()) {
      group.load()
    }
  }

  open class ResearchSDL {
    private var context: ResearchGroup? = null
    private var manager: ResearchManager = Sgl.researches
     val dependencies= Seq<Runnable>()
    private var addResearching= false

    protected fun makePlanetContext(planet: Planet, runnable: Runnable) {
      context = manager.makeGroup(planet)
      addResearching=true
      runnable.run()
      addResearching=false
      dependencies.forEach { it.run() }
    }

    protected fun byName(name: String): ResearchProject? {
      return context!!.getResearch(name)
    }
    protected fun research(name: String, techRequires: Int, techRequiresRandom: Int, runnable: ResearchProject.()->Unit): ResearchProject {
      val res = research(name, techRequires, techRequiresRandom)
      runnable.invoke(res)
      return res
    }

    protected fun research(name: String, techRequires: Int, runnable: ResearchProject.()->Unit): ResearchProject {
      val res = research(name, techRequires)
      runnable.invoke(res)
      return res
    }

    protected fun research(name: String, techRequires: Int, techRequiresRandom: Int): ResearchProject {
      val project = ResearchProject(name, techRequires, techRequiresRandom)
      context!!.addProject(project)
      return project
    }

    protected fun research(name: String, techRequires: Int): ResearchProject {
      val project = ResearchProject(name, techRequires)
      context!!.addProject(project)
      return project
    }
  }
}