package ice

import arc.Events
import arc.util.Time
import ice.graphics.windField.WindField
import ice.library.world.Load
import mindustry.Vars
import mindustry.game.EventType
import singularity.core.UpdatePool

object IVars : Load {
  override fun setup() {
    Events.on(EventType.WorldLoadEndEvent::class.java) {
      windField = WindField(Vars.world.tiles.width, Vars.world.tiles.height)
    }
    UpdatePool.receive("windField") {
      if (::windField.isInitialized&& !Vars.state.isPaused)windField.update(Time.delta/60f)
    }
  }

  lateinit var windField: WindField
}