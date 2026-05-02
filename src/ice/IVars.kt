package ice

import arc.Events
import ice.graphics.windField.WindField
import universecore.world.Load
import mindustry.game.EventType

object IVars : Load {
  override fun setup() {
    Events.on(EventType.WorldLoadEndEvent::class.java) {
      windField = WindField()
    }
  }

  lateinit var windField: WindField
}