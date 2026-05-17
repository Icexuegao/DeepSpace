package singularity.core

import arc.Events
import arc.struct.ObjectMap
import mindustry.game.EventType

object UpdatePool {
  init {
    Events.run(EventType.Trigger.update, ::update)
  }

  private val updateTasks = ObjectMap<String, Runnable>()

  fun receive(key: String, task: Runnable) {
    updateTasks.put(key, task)
  }

  fun remove(key: String): Boolean {
    return updateTasks.remove(key) != null
  }

  fun update() {
    for(task in updateTasks.values()) {
      task.run()
    }
  }
}