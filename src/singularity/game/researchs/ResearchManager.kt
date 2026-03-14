package singularity.game.researchs

import arc.struct.OrderedMap
import arc.struct.Seq
import mindustry.ctype.UnlockableContent
import mindustry.type.Planet
import singularity.Sgl
import singularity.world.blocks.research.ResearchDevice
import java.util.function.Consumer

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
    companion object {
      private var context: ResearchGroup? = null
      private var tasks: OrderedMap<ResearchProject, Runnable>? = null
      private var currProject: ResearchProject? = null
      private var inProjectContext = false
      private var manager: ResearchManager = Sgl.researches
      private var currReveal: RevealGroup? = null
    }

    protected fun setManager(manager: ResearchManager) {
      Companion.manager = manager
    }

    protected fun makePlanetContext(planet: Planet, runnable: Runnable) {
      context = manager.makeGroup(planet)
      val last: ResearchGroup? = context
      val lastTasks: OrderedMap<ResearchProject, Runnable>? = tasks

      tasks = OrderedMap<ResearchProject, Runnable>()

      runnable.run()
      tasks!!.forEach {e ->
        inProjectContext = true
        currProject = e!!.key
        e.value!!.run()
        inProjectContext = false
      }

      context = last
      tasks = lastTasks
    }

    protected fun reveal(group: RevealGroup, runnable: Runnable) {
      val last = currReveal
      currReveal = group
      group.require = last
      runnable.run()
      currReveal = last
    }

    protected fun byName(name: String?): ResearchProject? {
      return context!!.getResearch(name)
    }

    protected fun research(name: String, techRequires: Int, techRequiresRandom: Int, runnable: Runnable?): ResearchProject {
      val res = research(name, techRequires, techRequiresRandom)
      res.setReveal(currReveal)
      tasks!!.put(res, runnable)

      return res
    }

    protected fun research(name: String, techRequires: Int, runnable: Runnable?): ResearchProject {
      val res = research(name, techRequires)
      res.setReveal(currReveal)
      tasks!!.put(res, runnable)

      return res
    }

    protected fun research(name: String, techRequires: Int, techRequiresRandom: Int): ResearchProject {
      checkContext()

      val project = ResearchProject(name, techRequires, techRequiresRandom)
      project.setReveal(currReveal)
      context!!.addProject(project)

      return project
    }

    protected fun research(name: String, techRequires: Int): ResearchProject {
      checkContext()

      val project = ResearchProject(name, techRequires)
      project.setReveal(currReveal)
      context!!.addProject(project)

      return project
    }

    protected fun contents(vararg contents: UnlockableContent?) {
      checkProjectContext()
      currProject!!.addContent(*contents)
    }

    protected fun dependencies(vararg dependencies: ResearchProject?) {
      checkProjectContext()
      currProject!!.addDependency(*dependencies)
    }

    protected fun dependencies(vararg dependencies: String?) {
      checkProjectContext()
      for (dependency in dependencies) {
        currProject!!.addDependency(context!!.getResearch(dependency))
      }
    }

    protected fun inspire(inspire: Inspire?) {
      checkProjectContext()
      currProject!!.setInspire(inspire)
    }

    protected fun showRevealess() {
      checkProjectContext()
      currProject!!.showRevealess()
    }

    protected fun hideTechs() {
      checkProjectContext()
      currProject!!.hideTechs()
    }

    protected fun devices(vararg devices: ResearchDevice?) {
      checkProjectContext()
      currProject!!.addRequireDevice(*devices)
    }

    private fun checkContext() {
      checkNotNull(context) {"No planet context"}
      check(!inProjectContext) {"Already in project context"}
    }

    private fun checkProjectContext() {
      checkNotNull(currProject) {"No project context"}
      check(inProjectContext) {"Not in project context"}
    }
  }
}