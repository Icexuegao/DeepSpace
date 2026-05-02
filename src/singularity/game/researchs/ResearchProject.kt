package singularity.game.researchs

import arc.Core
import arc.Events
import arc.graphics.g2d.TextureRegion
import arc.math.Mathf
import arc.struct.Seq
import arc.util.Nullable
import universecore.struct.texture.LazyTextureSingleDelegate
import ice.ui.bundle.Localizable
import mindustry.ctype.UnlockableContent
import singularity.contents.SglTechThree
import singularity.core.SglEventTypes.ResearchCompletedEvent

class ResearchProject(val name: String, val techRequires: Int, val techRequiresRandom: Int = 0) :Localizable {
  val dependencies: Seq<ResearchProject> = Seq<ResearchProject>()
  val contents: Seq<UnlockableContent> = Seq<UnlockableContent>()

  override var localizedName: String = ""

  override var description: String = ""

  override var details: String = ""

  init {
    localizedName = name
  }

  var slogan: String = "slogan"
  var icon: TextureRegion by LazyTextureSingleDelegate("research_$name")

  @Nullable
  var inspire: Inspire? = null

  @Nullable
  var reveal: RevealGroup? = null

  var showIfRevealess: Boolean = false
  var hideTechs: Boolean = false
  var group: ResearchGroup? = null

  var isCompleted: Boolean = false
    private set
  var realRequireTechs: Int = 0
    private set
  var researched: Int = 0
    private set

  fun hideTechs(): ResearchProject {
    hideTechs = true
    return this
  }

  fun showRevealess(): ResearchProject {
    showIfRevealess = true
    return this
  }

  fun setReveal(reveal: RevealGroup?): ResearchProject {
    this.reveal = reveal
    return this
  }

  fun addDependency(vararg dependencies: ResearchProject): ResearchProject {

    this.dependencies.addAll(*dependencies)

    return this
  }

  // 依赖应当在所有项目生成后开始
  fun dependencies(vararg dependencies: String) {
    SglTechThree.dependencies.add {
      for(dependency in dependencies) {
        addDependency(group!!.getResearch(dependency)!!)
      }
    }
  }

  fun addContent(vararg contents: UnlockableContent): ResearchProject {
    this.contents.addAll(*contents)
    return this
  }

  fun init() {
    if (inspire != null) {
      inspire!!.init(this)
      inspire!!.applyTrigger(this)
    }

    if (dependenciesCompleted() && researched >= this.realRequireTechs) {
      isCompleted = true
    }
  }

  fun dependenciesCompleted(): Boolean {
    for(dependency in dependencies) {
      if (!dependency.isCompleted) return false
    }

    return true
  }

  val isRevealed: Boolean
    get() = reveal == null || reveal!!.isRevealed()

  fun requiresRevealed(): Boolean {
    return reveal == null || reveal!!.require == null || reveal!!.require!!.isRevealed()
  }

  fun progress(): Float {
    return researched.toFloat() / this.realRequireTechs
  }

  val isProcessing: Boolean
    get() = progress() > 0f

  fun researchProcess(techPoints: Int): Boolean {
    if (this.isCompleted) return true
    if (researched < realRequireTechs) researched += techPoints

    val res = checkComplete()

    save()

    return res
  }

  fun checkComplete(): Boolean {
    if (this.isCompleted) return true

    for(dependency in dependencies) {
      if (!dependency.checkComplete()) return false
    }

    if (researched >= this.realRequireTechs) {
      completeNow()
      return true
    }

    return false
  }

  fun completeNow() {
    isCompleted = true

    researched = this.realRequireTechs

    for(content in contents) {
      content.unlock()
    }

    Events.fire(ResearchCompletedEvent(this))

    save()
  }

  fun applyInspireNow() {
    if (inspire != null) inspire!!.apply(this)
  }

  fun revealNow() {
    if (reveal != null) reveal!!.reveal()
  }

  fun reset() {
    this.realRequireTechs = techRequires + Mathf.random(techRequiresRandom)
    researched = 0
    isCompleted = false

    for(content in contents) {
      content.clearUnlock()
    }

    if (inspire != null) inspire!!.reset()

    save()
  }

  fun load() {
    this.realRequireTechs = Core.settings.getInt("research_" + name + "_requireReal", techRequires + Mathf.random(techRequiresRandom))
    researched = Core.settings.getInt("research_" + name + "_researched", 0)
  }

  fun save() {
    Core.settings.put("research_" + name + "_requireReal", this.realRequireTechs)
    Core.settings.put("research_" + name + "_researched", researched)
  }
}