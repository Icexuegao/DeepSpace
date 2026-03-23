package ice.content

import arc.func.Prov
import ice.DeepSpace
import ice.library.world.Load
import mindustry.type.Item
import singularity.Sgl
import universecore.world.consumers.BaseConsumers

object AtomSchematics : Load {
  var copper_schematic = AtomSchematic(IItems.铜锭, 14000).apply {
    request.medium(0.23f)
    request.time(30f)
  }

  var lead_schematic = AtomSchematic(IItems.铅锭, 14000).apply {
    request.medium(0.26f)
    request.time(30f)
  }

  var silicon_schematic = AtomSchematic(IItems.单晶硅, 18000).apply {
    request.medium(0.41f)
    request.item(IItems.金珀沙, 1)
    request.time(45f)
  }

  class AtomSchematic(val item: Item, val reqint: Int) {
    companion object {
      val all = mutableListOf<AtomSchematic>()
    }

    init {
      all.add(this)
    }

    var request = BaseConsumers(false).apply {
      selectable = Prov {if (getunlock()) BaseConsumers.Visibility.usable else BaseConsumers.Visibility.hidden}
    }
    var d = 0

    init {
      d = DeepSpace.globals.getInt("atomSchematic_${item.name}_d", d)
    }

    fun progession() = d.toFloat() / reqint

    fun destructing(amount: Int = 1) {
      if (getunlock()) return
      if (d >= reqint) {
        unlock()
      } else {
        d += amount
        DeepSpace.globals.put("atomSchematic_${item.name}_d", d)
      }
    }

    fun getunlock(): Boolean {
      return DeepSpace.globals.getBool("atomSchematic_${item.name}", false)
    }

    fun unlock() {
      DeepSpace.globals.put("atomSchematic_${item.name}", true)
    }

    fun cleanLock() {
      d = 0
      DeepSpace.globals.put("atomSchematic_${item.name}_d", d)
      DeepSpace.globals.put("atomSchematic_${item.name}", false)
    }
  }
}