package singularity.game.researchs

import arc.Core
import arc.Events
import arc.func.Boolf
import arc.func.Cons
import mindustry.Vars
import mindustry.game.EventType
import mindustry.type.UnitType
import mindustry.world.Block
import singularity.Sgl
import singularity.core.SglEventTypes.ResearchCompletedEvent
import singularity.core.SglEventTypes.ResearchInspiredEvent

abstract class Inspire {
  var name: String?
    protected set

  var provProgress: Float = 0.5f
  var applied: Boolean = false

  var localized: String? = null
  var description: String? = null

  constructor() {
    this.name = null
  }

  constructor(name: String) {
    this.name = name
  }

  fun setProvProgress(provProgress: Float): Inspire {
    this.provProgress = provProgress
    return this
  }

  open fun init(project: ResearchProject) {
    if (name == null) name = "inspire_" + project.name
    applied = Core.settings.getBool(name + "_applied", false)

    localized = Core.bundle.get("research." + name + ".inspire")
    description = Core.bundle.get(
      "research." + name + ".inspire.description", Core.bundle.format("infos.inspiredBy", project.getLocalizedName())
    )
  }

  open fun apply(project: ResearchProject) {
    if (applied || !project.isRevealed) return

    applied = true
    Core.settings.put(name + "_applied", true)

    project.researchProcess((project.realRequireTechs * provProgress).toInt())

    Events.fire<ResearchInspiredEvent?>(ResearchInspiredEvent(this, project))
  }

  open fun reset() {
    applied = false
    Sgl.globals.put(name + "_applied", false)
  }

  abstract fun applyTrigger(project: ResearchProject)

  class EventInspire<T>(name: String, val eventType: Class<T?>?, val check: Boolf<T>) : Inspire(name) {

    override fun applyTrigger(project: ResearchProject) {
      Events.on(eventType) {e ->
        if (!applied && check.get(e)) {
          apply(project)
        }
      }
    }
  }

  abstract class CounterInspire : Inspire {
    val requireCount: Int

    private var count = 0

    protected constructor(requireCount: Int) {
      this.requireCount = requireCount
    }

    protected constructor(name: String, requireCount: Int) : super(name) {
      this.requireCount = requireCount
    }

    override fun init(project: ResearchProject) {
      super.init(project)
      count = Sgl.globals.getInt(name + "_count", 0)
    }

    override fun reset() {
      super.reset()
      count = 0
      Sgl.globals.put(name + "_count", 0)
    }

    override fun apply(project: ResearchProject) {
      if (applied || !project.isRevealed) return
      count++
      Sgl.globals.put(name + "_count", count)

      if (count >= requireCount) super.apply(project)
    }
  }

  class ResearchInspire : Inspire {
    val researchProject: ResearchProject

    constructor(researchProject: ResearchProject) : super() {
      this.researchProject = researchProject
    }

    constructor(name: String, researchProject: ResearchProject) : super(name) {
      this.researchProject = researchProject
    }

    override fun init(project: ResearchProject) {
      super.init(project)

      localized = Core.bundle.format("research.inspire.researched", researchProject.getLocalizedName())
    }

    override fun applyTrigger(project: ResearchProject) {
      Events.on<ResearchCompletedEvent?>(ResearchCompletedEvent::class.java, Cons {e: ResearchCompletedEvent? ->
        if (!applied && e!!.research == researchProject) {
          apply(project)
        }
      })
    }
  }

  class PlaceBlockInspire : CounterInspire {
    val block: Block

    constructor(block: Block) : super(1) {
      this.block = block
    }

    constructor(block: Block, requireCount: Int) : super(requireCount) {
      this.block = block
    }

    constructor(name: String, block: Block) : super(name, 1) {
      this.block = block
    }

    constructor(name: String, block: Block, requireCount: Int) : super(name, requireCount) {
      this.block = block
    }

    override fun init(project: ResearchProject) {
      super.init(project)

      localized = if (requireCount == 1) Core.bundle.format("research.inspire.placeBlock", block.localizedName)
      else Core.bundle.format("research.inspire.placeBlocks", requireCount, block.localizedName)
    }

    override fun applyTrigger(project: ResearchProject) {
      Events.on<EventType.BlockBuildEndEvent?>(EventType.BlockBuildEndEvent::class.java, Cons {e: EventType.BlockBuildEndEvent? ->
        if (!applied && e!!.team === Vars.player.team() && e.tile.build.block === block) {
          apply(project)
        }
      })
    }
  }

  class CreateUnitInspire : CounterInspire {
    val unitType: UnitType

    constructor(unitType: UnitType) : super(1) {
      this.unitType = unitType
    }

    constructor(unitType: UnitType, requireCount: Int) : super(requireCount) {
      this.unitType = unitType
    }

    constructor(name: String, unitType: UnitType) : super(name, 1) {
      this.unitType = unitType
    }

    constructor(name: String, unitType: UnitType, requireCount: Int) : super(name, requireCount) {
      this.unitType = unitType
    }

    override fun init(project: ResearchProject) {
      super.init(project)

      localized = if (requireCount == 1) Core.bundle.format("research.inspire.createUnit", unitType.localizedName)
      else Core.bundle.format("research.inspire.createUnits", requireCount, unitType.localizedName)
    }

    override fun applyTrigger(project: ResearchProject) {
      Events.on<EventType.UnitCreateEvent?>(EventType.UnitCreateEvent::class.java, Cons {e: EventType.UnitCreateEvent? ->
        if (!applied && (e!!.spawner.team() === Vars.player.team() || e.spawnerUnit.team() === Vars.player.team()) && e.unit.type === unitType) {
          apply(project)
        }
      })
    }
  }
}