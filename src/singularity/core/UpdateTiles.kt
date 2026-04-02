package singularity.core

import arc.Events
import arc.struct.ObjectMap
import ice.library.world.Load
import mindustry.Vars
import mindustry.game.EventType
import mindustry.world.Tile
import mindustry.world.Tiles

object UpdateTiles: Load {
  val updaters = ObjectMap<Tile, Updatable>()

  init {
    Events.on(EventType.WorldLoadEvent::class.java) {
      loadAll(Vars.world.tiles)
    }
  }

  fun update() {
    for (updater in updaters) {
      updater.value.update(updater.key)
    }
  }

  fun add(tile: Tile, updater: Updatable) {
    updaters.put(tile, updater)
  }

  fun clear(tile: Tile) {
    updaters.remove(tile)
  }

  fun clear() = updaters.clear()

  fun loadAll(all: Tiles) {
    updaters.clear()
    all.eachTile { t: Tile ->
      if (t.floor() is Updatable) add(t, t.floor() as Updatable)
      if (t.overlay() is Updatable) add(t, t.overlay() as Updatable)
    }
  }

  interface Updatable {
    fun update(tile: Tile)
  }
}