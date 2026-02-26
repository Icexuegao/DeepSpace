package ice.world.content.blocks.effect

import arc.Core
import arc.func.Prov
import arc.util.Strings
import arc.util.io.Reads
import arc.util.io.Writes
import ice.content.IUnitTypes
import mindustry.content.Fx
import mindustry.graphics.Pal
import mindustry.ui.Bar
import mindustry.world.blocks.storage.CoreBlock
import mindustry.world.meta.Stat
import mindustry.world.meta.StatUnit

open class CoreBlock(name: String) : CoreBlock(name) {
  var powerProduct = 0f

  init {
    hasPower = true
    consumesPower = false
    outputsPower = true
    buildType = Prov(::CoreBuild)
  }

  override fun setBars() {
    super.setBars()
    if (hasPower && outputsPower) {
      addBar<CoreBuild>("power") { entity ->
        Bar({
          Core.bundle.format(
            "bar.poweroutput", Strings.fixed(entity.powerProduction * 60 * entity.timeScale(), 1)
          )
        }, { Pal.powerBar }, { 1f })
      }
    }
  }

  override fun setStats() {
    super.setStats()
    if (powerProduct != 0f) stats.add(Stat.basePowerGeneration, powerProduct * 60.0f, StatUnit.powerSecond)
  }

  inner class CoreBuild : CoreBlock.CoreBuild() {

    var add = false

    override fun updateTile() {
      super.updateTile()
      if (!add) {
        add = true
        repeat(2) {
          IUnitTypes.收割.spawn(team, x, y)
          Fx.spawn.at(this)
        }
      }
    }

    override fun read(read: Reads, revision: Byte) {
      super.read(read, revision)
      add=read.bool()
    }

    override fun write(write: Writes) {
      super.write(write)
      write.bool(add)
    }
    override fun getPowerProduction(): Float {
      return if (enabled) powerProduct else 0f
    }
  }
}