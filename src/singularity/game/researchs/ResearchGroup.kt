package singularity.game.researchs

import arc.func.Boolf
import arc.struct.OrderedMap
import arc.struct.Seq
import mindustry.ctype.UnlockableContent
import mindustry.type.Planet

class ResearchGroup(val onPlanet: Planet) {
  private val projects = OrderedMap<String, ResearchProject>()
  private val revealGroups = OrderedMap<RevealGroup, Seq<ResearchProject>>()

  fun addProject(project: ResearchProject) {
    projects.put(project.name, project)
    project.group = this
  }

  fun getResearch(name: String): ResearchProject? {
    return projects.get(name)
  }

  fun listResearches(): Seq<ResearchProject> {
    return projects.values().toSeq()
  }

  fun getResearchByContent(content: UnlockableContent?): ResearchProject? {
    return projects.values().toSeq().find {p: ResearchProject -> p.contents.contains(content)}
  }

  fun init() {
    for (value in projects.values()) {
      value.init()
      if (value.reveal != null) revealGroups.get(value.reveal) {Seq()}.add(value)
    }

    for (group in revealGroups.keys()) {
      group.init()
    }
  }

  fun reset() {
    for (project in projects.values()) {
      project.reset()
    }

    for (group in revealGroups.keys()) {
      group.reset()
    }
  }

  fun save() {
    for (project in projects.values()) {
      project.save()
    }
  }

  fun load() {
    for (project in projects.values()) {
      project.load()
    }
  }
}