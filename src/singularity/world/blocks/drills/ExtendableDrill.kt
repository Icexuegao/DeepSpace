package singularity.world.blocks.drills

import arc.func.Prov
import arc.struct.IntSet
import arc.struct.ObjectSet
import arc.struct.Seq
import arc.util.io.Reads
import arc.util.io.Writes
import mindustry.game.Team
import mindustry.gen.Building
import mindustry.type.ItemStack
import mindustry.world.Tile
import singularity.world.blocks.drills.ExtendMiner.ExtendMinerBuild
import universecore.components.blockcomp.ChainsBlockComp
import universecore.components.blockcomp.ChainsBuildComp
import universecore.world.blocks.modules.ChainsModule

open class ExtendableDrill(name: String) : BaseDrill(name), ChainsBlockComp {
  override var maxChainsHeight: Int = 10
  override var maxChainsWidth: Int = 10
  var validChildType: ObjectSet<ExtendMiner?> = ObjectSet<ExtendMiner?>()

  override fun chainable(other: ChainsBlockComp): Boolean {
    return other is ExtendMiner && validChildType.contains(other)
  }

  init {
    buildType = Prov(::ExtendableDrillBuild)
  }

  override fun setStats() {
    super.setStats()
    setChainsStats(stats)
  }

  // @Annotations.ImplEntries
  inner class ExtendableDrillBuild : BaseDrillBuild(), ChainsBuildComp {
    override var loadingInvalidPos = IntSet()
    override var chains = ChainsModule(this)
    var valid: Boolean = true
    var ores: Seq<ItemStack> = Seq<ItemStack>()
    var updatedMark: Boolean = false

    override fun init(tile: Tile?, team: Team?, shouldAdd: Boolean, rotation: Int): Building {
      super.init(tile, team, shouldAdd, rotation)
      chains.newContainer()
      return this
    }

    override fun updateValid(): Boolean {
      return super.updateValid() && valid
    }

    override fun onProximityRemoved() {
      super.onProximityRemoved()
      onChainsRemoved()
    }

    override fun write(write: Writes) {
      super.write(write)
      writeChains(write)
    }

    override fun read(read: Reads, revision: Byte) {
      super.read(read, revision)
      readChains(read)
    }

    override fun onProximityAdded() {
      super.onProximityAdded()
      onChainsAdded()
    }

    override fun onProximityUpdate() {
      noSleep()

      getMines(tile, block, ores)
      updatedMark = true
    }

    override fun updateTile() {
      super.updateTile()
      chains.container.update()

      if (updatedMark) {
        updateOres()
      }
    }

    override fun onChainsUpdated() {
      updateOres()
    }

    fun updateOres() {
      updatedMark = false

      outputItems.clear()

      for (ore in ores) {
        outputItems.add(ore.copy())
      }

      for (comp in chains.container.all) {
        if (comp is ExtendableDrillBuild && comp !== this) {
          valid = false
          return
        }
        if (comp is ExtendMinerBuild) {
          t@ for (mine in comp.mines) {
            for (ores in outputItems) {
              if (ores.item === mine!!.item) {
                ores.amount += mine.amount
                continue@t
              }
            }
            outputItems.add(mine!!.copy())
          }
        }

        valid = true
      }
      var reset = mineOreItems.size != outputItems.size
      if (!reset) {
        for (stack in outputItems) {
          if (!mineOreItems.contains(stack.item)) {
            reset = true
            break
          }
        }
      }
      if (reset) {
        mineOreItems.clear()
        for (stack in outputItems) {
          mineOreItems.add(stack.item)
        }

        currentMines = BooleanArray(outputItems.size)
        progress = FloatArray(outputItems.size)
        lastDrillSpeed = FloatArray(outputItems.size)
        if (outputItems.size == 1) currentMines[0] = true
      }
    }
  }
}