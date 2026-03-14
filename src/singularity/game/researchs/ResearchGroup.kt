package singularity.game.researchs

import arc.func.Boolf
import arc.func.Prov
import arc.struct.OrderedMap
import arc.struct.Seq
import mindustry.ctype.UnlockableContent
import mindustry.type.Planet
import java.util.function.Consumer

class ResearchGroup(val onPlanet: Planet) {
  private val projects = OrderedMap<String?, ResearchProject>()
  private val revealGroups = OrderedMap<RevealGroup?, Seq<ResearchProject?>?>()

  fun addProject(project: ResearchProject) {
    projects.put(project.name, project)
    project.group = this
  }

  fun getResearch(name: String?): ResearchProject? {
    return projects.get(name)
  }

  fun listResearches(): Seq<ResearchProject> {
    return projects.values().toSeq()
  }

  fun getResearchByContent(content: UnlockableContent?): ResearchProject? {
    return projects.values().toSeq().find(Boolf {p: ResearchProject? -> p!!.contents.contains(content)})
  }

  fun init() {
    for (value in projects.values()) {
      value.init()
      if (value.reveal != null) revealGroups.get(value.reveal, Prov {Seq()})!!.add(value)
    }

    revealGroups.keys().forEach(Consumer {obj: RevealGroup? -> obj!!.init()})
  }

  fun reset() {
    projects.values().forEach(Consumer {obj: ResearchProject? -> obj!!.reset()})
    revealGroups.keys().forEach(Consumer {obj: RevealGroup? -> obj!!.reset()})
  }

  fun save() {
    projects.values().forEach(Consumer {obj: ResearchProject? -> obj!!.save()})
  }

  fun load() {
    projects.values().forEach(Consumer {obj: ResearchProject? -> obj!!.load()})
  }
}