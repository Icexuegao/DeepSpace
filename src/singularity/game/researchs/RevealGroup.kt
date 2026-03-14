package singularity.game.researchs

import arc.Core
import arc.Events
import arc.util.Nullable
import ice.ui.bundle.BaseBundle.Bundle.Companion.localizedName
import singularity.Sgl
import singularity.core.SglEventTypes.ResearchCompletedEvent
import singularity.core.SglEventTypes.RevealedEvent

abstract class RevealGroup(protected val name: String?) {
  protected var revealed: Boolean = false

  @Nullable
  var require: RevealGroup? = null

  fun init() {
    revealed = Core.settings.getBool(name + "_revealed", false)
    applyTrigger()
  }

  fun reveal() {
    if (!revealed) {
      revealed = true
      Sgl.globals.put(name + "_revealed", true)

      Events.fire(RevealedEvent(this))
    }
  }

  fun reset() {
    revealed = false
    Sgl.globals.put(name + "_revealed", false)
  }

  fun isRevealed(): Boolean {
    return (require == null || require!!.isRevealed()) && revealed
  }

  open fun localized(): String? {
    return Core.bundle.get("research.$name.reveal")
  }

  abstract fun applyTrigger()

  class ResearchReveal(name: String?, private val project: ResearchProject) : RevealGroup(name) {
    override fun localized(): String {
      return "研究 ${project.localizedName}"
    }

    override fun applyTrigger() {
      Events.on(ResearchCompletedEvent::class.java) {e: ResearchCompletedEvent? ->
        if (!revealed && e!!.research === project) {
          reveal()
        }
      }
    }
  }
}