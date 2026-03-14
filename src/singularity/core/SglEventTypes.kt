package singularity.core

import singularity.game.researchs.Inspire
import singularity.game.researchs.ResearchProject
import singularity.game.researchs.RevealGroup

class SglEventTypes {
  class ResearchCompletedEvent(val research: ResearchProject?)

  class ResearchInspiredEvent(val inspire: Inspire?, val research: ResearchProject?)

  class RevealedEvent(val group: RevealGroup?)
}